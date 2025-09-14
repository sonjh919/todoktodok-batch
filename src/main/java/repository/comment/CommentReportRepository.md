```java
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    /**
     쿼리 설명 : 특정 회원(member)이 특정 댓글(comment)을 이미 신고했는지 존재 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM comment_report
     WHERE member_id = ? AND comment_id = ?;
     */
    boolean existsByMemberAndComment(final Member member, final Comment comment);
}
```