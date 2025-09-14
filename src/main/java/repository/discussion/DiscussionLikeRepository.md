```java
public interface DiscussionLikeRepository extends JpaRepository<DiscussionLike, Long> {

    /**
     쿼리 설명 : 특정 회원(member)이 특정 토론(discussion)에 좋아요를 눌렀는지 단일 DiscussionLike 엔티티를 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM discussion_like
     WHERE member_id = ? AND discussion_id = ?;
     */
    Optional<DiscussionLike> findByMemberAndDiscussion(final Member member, final Discussion discussion);

    /**
     쿼리 설명 : 특정 토론 ID(discussionId)에 대한 좋아요 수를 단일 카운트하는 쿼리
     대응 쿼리 :
     SELECT COUNT(*)
     FROM discussion_like
     WHERE discussion_id = ?;
     */
    @Query("""
                SELECT COUNT(dl)
                FROM DiscussionLike dl
                WHERE dl.discussion.id = :discussionId
            """)
    Long findLikeCountsByDiscussionId(@Param("discussionId") final Long discussionId);

    /**
     쿼리 설명 : 주어진 토론 ID 리스트(discussionIds)에 대해 각 토론별 좋아요 개수를 조회하는 쿼리
     대응 쿼리 :
     SELECT d.id, COUNT(dl.id)
     FROM discussion d
     LEFT JOIN discussion_like dl ON dl.discussion_id = d.id
     WHERE d.id IN (?,?,...)
     GROUP BY d.id;
     */
    @Query("""
                SELECT new todoktodok.backend.discussion.application.service.query.DiscussionLikeCountDto(d.id, COUNT(dl))
                FROM Discussion d
                LEFT JOIN DiscussionLike dl ON dl.discussion = d
                WHERE d.id IN :discussionIds
                GROUP BY d.id
            """)
    List<DiscussionLikeCountDto> findLikeCountsByDiscussionIds(@Param("discussionIds") final List<Long> discussionIds);

    /**
     쿼리 설명 : 특정 일자 이후(sinceDate)에 생성된 좋아요만 포함하여 주어진 토론 ID 리스트(discussionIds)에 대해 좋아요 개수를 조회하는 쿼리
     대응 쿼리 :
     SELECT d.id, COUNT(dl.id)
     FROM discussion d
     LEFT JOIN discussion_like dl ON dl.discussion_id = d.id AND dl.created_at >= ?
     WHERE d.id IN (?,?,...)
     GROUP BY d.id;
     */
    @Query("""
                SELECT new todoktodok.backend.discussion.application.service.query.DiscussionLikeCountDto(d.id, COUNT(dl))
                FROM Discussion d
                LEFT JOIN DiscussionLike dl ON dl.discussion = d AND dl.createdAt >= :sinceDate
                WHERE d.id IN :discussionIds
                GROUP BY d.id
            """)
    List<DiscussionLikeCountDto> findLikeCountsByDiscussionIdsSinceDate(
            @Param("discussionIds") final List<Long> discussionIds,
            @Param("sinceDate") final LocalDateTime sinceDate
    );

    /**
     쿼리 설명 : 특정 회원(member)이 주어진 토론 ID 리스트(discussionIds) 중 좋아요를 누른 토론 ID 목록을 조회하는 쿼리
     대응 쿼리 :
     SELECT discussion_id
     FROM discussion_like
     WHERE member_id = ? AND discussion_id IN (?,?,...);
     */
    @Query("""
                SELECT dl.discussion.id
                FROM DiscussionLike dl
                WHERE dl.member = :member
                AND dl.discussion.id IN :discussionIds
            """)
    List<Long> findLikedDiscussionIdsByMember(
            @Param("member") final Member member,
            @Param("discussionIds") final List<Long> discussionIds
    );

    /**
     쿼리 설명 : 특정 회원(member)이 특정 토론(discussion)에 좋아요를 눌렀는지 존재 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM discussion_like
     WHERE member_id = ? AND discussion_id = ?;
     */
    boolean existsByMemberAndDiscussion(final Member member, final Discussion discussion);
}

```