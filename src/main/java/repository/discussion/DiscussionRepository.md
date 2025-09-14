```java
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    /**
     쿼리 설명 : 특정 회원(member)이 작성한 모든 Discussion 목록을 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM discussion
     WHERE member_id = ?;
     */
    List<Discussion> findDiscussionsByMember(final Member member);

    /**
     쿼리 설명 : 키워드(keyword)를 포함하는 토론 제목 또는 연관된 책 제목이 일치하는 토론 목록을 조회,
     삭제되지 않은(d.deletedAt IS NULL) 토론과 책을 대상으로 함
     대응 쿼리 :
     SELECT * FROM discussion d
     WHERE UPPER(d.title) LIKE UPPER('%keyword%')
     AND d.deleted_at IS NULL
     UNION
     SELECT d.* FROM discussion d
     JOIN book b ON d.book_id = b.id
     WHERE UPPER(b.title) LIKE UPPER('%keyword%')
     AND d.deleted_at IS NULL AND b.deleted_at IS NULL;
     */
    @Query("""
                SELECT d FROM Discussion d
                WHERE UPPER(d.title) LIKE UPPER(CONCAT('%', :keyword, '%'))
                AND d.deletedAt IS NULL
                UNION
                SELECT d FROM Discussion d
                JOIN d.book b
                WHERE UPPER(b.title) LIKE UPPER(CONCAT('%', :keyword, '%'))
                AND d.deletedAt IS NULL
                AND b.deletedAt IS NULL
            """)
    List<Discussion> searchByKeyword(
            @Param("keyword") final String keyword
    );

    /**
     쿼리 설명 : 키워드(keyword)를 포함하는 토론 제목 또는 연관된 책 제목이 일치하는 토론 목록 중에
     특정 회원(member)이 작성한 토론만 조회하며 삭제되지 않은 엔티티를 대상으로 함
     대응 쿼리 : 위 쿼리 + AND d.member_id = ?
     */
    @Query("""
                SELECT d FROM Discussion d
                WHERE UPPER(d.title) LIKE UPPER(CONCAT('%', :keyword, '%'))
                AND d.deletedAt IS NULL
                AND d.member = :member
                UNION
                SELECT d FROM Discussion d
                JOIN d.book b
                WHERE UPPER(b.title) LIKE UPPER(CONCAT('%', :keyword, '%'))
                AND d.deletedAt IS NULL
                AND b.deletedAt IS NULL
                AND d.member = :member
            """)
    List<Discussion> searchByKeywordAndMember(
            @Param("keyword") final String keyword,
            @Param("member") final Member member
    );

    /**
     쿼리 설명 : 회원(memberId)이 참여한 토론들을 중복 없이 모두 조회하는 네이티브 쿼리
     참여 조건: 토론 작성자, 댓글 작성자, 답글 작성자
     AND 토론, 댓글, 답글 모두 삭제되지 않은 경우만 조회
     대응 쿼리 :
     SELECT d.*
     FROM discussion d
     WHERE d.member_id = :memberId AND d.deleted_at IS NULL
     UNION
     SELECT d.*
     FROM discussion d
     JOIN comment c ON c.discussion_id = d.id
     WHERE c.member_id = :memberId AND d.deleted_at IS NULL AND c.deleted_at IS NULL
     UNION
     SELECT d.*
     FROM discussion d
     JOIN comment c ON c.discussion_id = d.id
     JOIN reply r ON r.comment_id = c.id
     WHERE r.member_id = :memberId AND d.deleted_at IS NULL AND c.deleted_at IS NULL AND r.deleted_at IS NULL;
     */
    @Query(value = """
                SELECT d.* 
                FROM discussion d
                WHERE d.member_id = :memberId
                AND d.deleted_at IS NULL
                    
                UNION
                    
                SELECT d.* 
                FROM discussion d
                JOIN comment c ON c.discussion_id = d.id
                WHERE c.member_id = :memberId
                AND d.deleted_at IS NULL
                AND c.deleted_at IS NULL
                    
                UNION
                    
                SELECT d.* 
                FROM discussion d
                JOIN comment c ON c.discussion_id = d.id
                JOIN reply r ON r.comment_id = c.id
                WHERE r.member_id = :memberId
                AND d.deleted_at IS NULL
                AND c.deleted_at IS NULL
                AND r.deleted_at IS NULL
            """, nativeQuery = true)
    List<Discussion> findParticipatedDiscussionsByMember(@Param("memberId") final Long memberId);

    /**
     쿼리 설명 : 페이징 조건에 맞는 모든 Discussion 엔티티를 Slice로 조회
     대응 쿼리 :
     SELECT *
     FROM discussion
     ORDER BY (페이징 조건)
     LIMIT ?, ?;
     */
    Slice<Discussion> findAllBy(final Pageable pageable);

    /**
     쿼리 설명 : cursorId 기준보다 작은 Discussion ID를 가진 토론들을 페이징 조회 (커서 기반 페이징)
     대응 쿼리 :
     SELECT *
     FROM discussion
     WHERE id < ?
     ORDER BY id DESC
     LIMIT ?, ?;
     */
    @Query("""
            SELECT d
            FROM Discussion d
            WHERE :cursorId IS NULL OR d.id < :cursorId
            """)
    Slice<Discussion> findByIdLessThan(
            @Param("cursorId") final Long cursorId,
            final Pageable pageable
    );

    /**
     쿼리 설명 : 특정 기간(periodStart) 이후 댓글이 달린 토론 중 활성 토론을 커서 기준(cursorLastCommentedAt, cursorId)으로
     정렬, 각 토론과 현재 회원(member)에 대한 좋아요 유무, 댓글 및 답글 수, 최근 댓글 작성일을 함께 조회
     대응 쿼리 :
     SELECT d.*, COUNT(DISTINCT dl.id), (COUNT(DISTINCT c.id) + COUNT(DISTINCT r.id)),
            CASE WHEN COUNT(DISTINCT dlByMe.id) > 0 THEN TRUE ELSE FALSE END,
            MAX(c.created_at)
     FROM discussion d
     JOIN comment c ON c.discussion_id = d.id AND c.created_at >= ?
     LEFT JOIN reply r ON r.comment_id = c.id
     LEFT JOIN discussion_like dl ON dl.discussion_id = d.id
     LEFT JOIN discussion_like dlByMe ON dlByMe.discussion_id = d.id AND dlByMe.member_id = ?
     GROUP BY d.id
     HAVING (:cursorLastCommentedAt IS NULL OR MAX(c.created_at) < :cursorLastCommentedAt
            OR (MAX(c.created_at) = :cursorLastCommentedAt AND d.id < :cursorId))
     ORDER BY MAX(c.created_at) DESC, d.id DESC
     LIMIT ?, ?;
     */
    @Query("""
            SELECT new todoktodok.backend.discussion.application.dto.response.ActiveDiscussionResponse(
                         d,
                         COUNT(DISTINCT dl.id),
                         (COUNT(DISTINCT c.id) + COUNT(DISTINCT r.id)),
                         CASE WHEN COUNT(DISTINCT dlByMe.id) > 0 THEN true ELSE false END,
                         MAX(c.createdAt)
             )
            FROM Discussion d
            JOIN Comment c ON c.discussion = d AND c.createdAt >= :periodStart
            LEFT JOIN Reply r ON r.comment = c
            LEFT JOIN DiscussionLike dl ON dl.discussion = d
            LEFT JOIN DiscussionLike dlByMe ON dlByMe.discussion = d AND dlByMe.member = :member
            GROUP BY d
            HAVING (
                :cursorLastCommentedAt IS NULL
                OR MAX(c.createdAt) < :cursorLastCommentedAt
                OR MAX(c.createdAt) = :cursorLastCommentedAt AND d.id < :cursorId
            )
            ORDER BY MAX(c.createdAt) DESC, d.id DESC
    """)
    List<ActiveDiscussionResponse> findActiveDiscussionsByCursor(
            @Param("member") final Member member,
            @Param("periodStart") final LocalDateTime periodStart,
            @Param("cursorLastCommentedAt") final LocalDateTime cursorLastCommentedAt,
            @Param("cursorId") final Long cursorId,
            final Pageable pageable
    );
}

```