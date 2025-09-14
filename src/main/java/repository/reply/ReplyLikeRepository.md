```java
public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {

    /**
     쿼리 설명 : 특정 회원(member)이 특정 답글(reply)에 좋아요를 눌렀는지 단일 ReplyLike 엔티티를 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM reply_like
     WHERE member_id = ? AND reply_id = ?;
     */
    Optional<ReplyLike> findByMemberAndReply(final Member member, final Reply reply);

    /**
     쿼리 설명 : 주어진 답글 ID 리스트(replyIds)에 대해 각 답글별 좋아요 개수를 조회하는 쿼리
     대응 쿼리 :
     SELECT r.id, COUNT(rl.id)
     FROM reply r
     LEFT JOIN reply_like rl ON rl.reply_id = r.id
     WHERE r.id IN (?,?,...)
     GROUP BY r.id;
     */
    @Query("""
                SELECT new todoktodok.backend.reply.application.service.query.ReplyLikeCountDto(r.id, COUNT(rl))
                FROM Reply r
                LEFT JOIN ReplyLike rl ON rl.reply = r
                WHERE r.id IN :replyIds
                GROUP BY r.id
            """)
    List<ReplyLikeCountDto> findLikeCountsByReplyIds(@Param("replyIds") final List<Long> replyIds);

    /**
     쿼리 설명 : 특정 회원(member)이 주어진 답글 ID 리스트(replyIds) 중 좋아요를 누른 답글 ID 목록을 조회하는 쿼리
     대응 쿼리 :
     SELECT reply_id
     FROM reply_like
     WHERE member_id = ? AND reply_id IN (?,?,...);
     */
    @Query("""
                SELECT rl.reply.id
                FROM ReplyLike rl
                WHERE rl.member = :member
                AND rl.reply.id IN :replyIds
            """)
    List<Long> findLikedReplyIdsByMember(
            @Param("member") final Member member,
            @Param("replyIds") final List<Long> replyIds
    );
}
```