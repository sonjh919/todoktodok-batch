```java
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     쿼리 설명 : 특정 Discussion에 속한 모든 Comment 목록을 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM comment
     WHERE discussion_id = ?;
     */
    List<Comment> findCommentsByDiscussion(final Discussion discussion);

    /**
     쿼리 설명 : 특정 Discussion에 댓글이 존재하는지 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM comment
     WHERE discussion_id = ?;
     */
    boolean existsCommentsByDiscussion(final Discussion discussion);

    /**
     쿼리 설명 : 주어진 Discussion ID 리스트(discussionIds)에 대해 각 Discussion별 댓글 수와 댓글에 달린 답글 수를 함께 조회하는 쿼리
     대응 쿼리 :
     SELECT d.id, COUNT(DISTINCT c.id), COUNT(DISTINCT r.id)
     FROM discussion d
     LEFT JOIN comment c ON c.discussion_id = d.id
     LEFT JOIN reply r ON r.comment_id = c.id
     WHERE d.id IN (?,?,...)
     GROUP BY d.id;
     */
    @Query("""
        SELECT new todoktodok.backend.discussion.application.service.query.DiscussionCommentCountDto(
            d.id,
            COUNT(DISTINCT c.id),
            COUNT(DISTINCT r.id)
        )
        FROM Discussion d
        LEFT JOIN Comment c ON c.discussion = d
        LEFT JOIN Reply r ON r.comment = c
        WHERE d.id IN :discussionIds
        GROUP BY d.id
    """)
    List<DiscussionCommentCountDto> findCommentCountsByDiscussionIds(@Param("discussionIds") final List<Long> discussionIds);

    /**
     쿼리 설명 : 특정 일자 이후(sinceDate) 생성된 댓글과 답글의 수를 포함하여 주어진 Discussion ID 리스트(discussionIds)에 대해 댓글 및 답글 수를 조회하는 쿼리
     대응 쿼리 :
     SELECT d.id, COUNT(DISTINCT c.id), COUNT(DISTINCT r.id)
     FROM discussion d
     LEFT JOIN comment c ON c.discussion_id = d.id AND c.created_at >= ?
     LEFT JOIN reply r ON r.comment.discussion_id = d.id AND r.created_at >= ?
     WHERE d.id IN (?,?,...)
     GROUP BY d.id;
     */
    @Query("""
        SELECT new todoktodok.backend.discussion.application.service.query.DiscussionCommentCountDto(
            d.id,
            COUNT(DISTINCT c.id),
            COUNT(DISTINCT r.id)
        )
        FROM Discussion d
        LEFT JOIN Comment c ON c.discussion = d AND c.createdAt >= :sinceDate
        LEFT JOIN Reply r ON r.comment.discussion = d AND r.createdAt >= :sinceDate
        WHERE d.id IN :discussionIds
        GROUP BY d.id
    """)
    List<DiscussionCommentCountDto> findCommentCountsByDiscussionIdsSinceDate(
            @Param("discussionIds") final List<Long> discussionIds,
            @Param("sinceDate") final LocalDateTime sinceDate
    );

    /**
     쿼리 설명 : 특정 Discussion ID에 속한 댓글 수를 단순 카운트하는 쿼리
     대응 쿼리 :
     SELECT COUNT(*)
     FROM comment
     WHERE discussion_id = ?;
     */
    @Query("""
        SELECT COUNT(c)
        FROM Comment c
        WHERE c.discussion.id = :discussionId
    """)
    Long countCommentsByDiscussionId(@Param("discussionId") final Long discussionId);
}

```