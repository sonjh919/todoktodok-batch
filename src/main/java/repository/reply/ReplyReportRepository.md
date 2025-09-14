```java
public interface ReplyReportRepository extends JpaRepository<ReplyReport, Long> {

    /**
     쿼리 설명 : 특정 회원(member)이 특정 답글(reply)을 이미 신고했는지 존재 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM reply_report
     WHERE member_id = ? AND reply_id = ?;
     */
    boolean existsByMemberAndReply(final Member member, final Reply reply);
}

```