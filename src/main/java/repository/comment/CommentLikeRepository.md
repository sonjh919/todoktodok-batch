```java
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    /**
     쿼리 설명 : 특정 회원(member)이 특정 댓글(comment)에 좋아요를 눌렀는지 단일 CommentLike 엔티티를 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM comment_like
     WHERE member_id = ? AND comment_id = ?;
     */
    Optional<CommentLike> findByMemberAndComment(final Member member, final Comment comment);

    /**
     쿼리 설명 : 주어진 댓글 ID 리스트(commentIds)에 대해 각 댓글별 좋아요 개수 카운트를 조회하는 쿼리
     대응 쿼리 :
     SELECT c.id, COUNT(cl.id)
     FROM comment c
     LEFT JOIN comment_like cl ON cl.comment_id = c.id
     WHERE c.id IN (?,?,...)
     GROUP BY c.id;
     */
    @Query("""
                SELECT new todoktodok.backend.comment.application.service.query.CommentLikeCountDto(c.id, COUNT(cl))
                FROM Comment c
                LEFT JOIN CommentLike cl ON cl.comment = c
                WHERE c.id IN :commentIds
                GROUP BY c.id
            """)
    List<CommentLikeCountDto> findLikeCountsByCommentIds(@Param("commentIds") final List<Long> commentIds);

    /**
     쿼리 설명 : 특정 회원 ID(memberId)와 댓글 ID(commentId)를 기준으로 좋아요 존재 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM comment_like
     WHERE member_id = ? AND comment_id = ?;
     */
    boolean existsByMemberIdAndCommentId(
            @Param("memberId") final Long memberId,
            @Param("commentId") final Long commentId
    );

    /**
     쿼리 설명 : 특정 회원(memberId)이 주어진 댓글 ID 리스트(commentIds)들 중 좋아요를 누른 댓글 ID 목록을 조회하는 쿼리
     대응 쿼리 :
     SELECT comment_id
     FROM comment_like
     WHERE member_id = ? AND comment_id IN (?,?,...);
     */
    @Query("""
            SELECT cl.comment.id
            FROM CommentLike cl
            WHERE cl.member.id = :memberId AND cl.comment.id IN :commentIds
            """)
    List<Long> findLikedCommentIdsByMember(
            @Param("memberId") final Long memberId,
            @Param("commentIds") final List<Long> commentIds);
}

```