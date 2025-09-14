```java
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    /**
     쿼리 설명 : 특정 댓글(comment)에 달린 답글 수를 카운트하는 쿼리
     대응 쿼리 :
     SELECT COUNT(*)
     FROM reply
     WHERE comment_id = ?;
     */
    int countRepliesByComment(final Comment comment);

    /**
     쿼리 설명 : 주어진 댓글 ID 리스트(commentIds)에 대해 각 댓글별 답글 개수를 조회하는 쿼리
     대응 쿼리 :
     SELECT c.id, COUNT(r.id)
     FROM comment c
     LEFT JOIN reply r ON r.comment_id = c.id
     WHERE c.id IN (?,?,...)
     GROUP BY c.id;
     */
    @Query("""
                SELECT new todoktodok.backend.comment.application.service.query.CommentReplyCountDto(c.id, COUNT(r))
                FROM Comment c
                LEFT JOIN Reply r ON r.comment = c
                WHERE c.id IN :commentIds
                GROUP BY c.id
            """)
    List<CommentReplyCountDto> findReplyCountsByCommentIds(@Param("commentIds") final List<Long> commentIds);

    /**
     쿼리 설명 : 특정 댓글(comment)에 속한 모든 Reply 목록을 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM reply
     WHERE comment_id = ?;
     */
    List<Reply> findRepliesByComment(final Comment comment);

    /**
     쿼리 설명 : 특정 댓글(comment)에 답글이 존재하는지 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM reply
     WHERE comment_id = ?;
     */
    boolean existsByComment(final Comment comment);

    /**
     쿼리 설명 : 특정 토론 ID(discussionId)에 포함된 모든 댓글에 달린 답글 수를 카운트하는 쿼리
     대응 쿼리 :
     SELECT COUNT(r.id)
     FROM reply r
     JOIN comment c ON r.comment_id = c.id
     WHERE c.discussion_id = ?;
     */
    @Query("""
                SELECT COUNT(r)
                FROM Reply r
                JOIN r.comment c
                WHERE c.discussion.id = :discussionId
            """)
    Long countRepliesByDiscussionId(@Param("discussionId") Long discussionId);
}

```