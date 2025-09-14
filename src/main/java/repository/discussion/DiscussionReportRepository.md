```java
public interface DiscussionReportRepository extends JpaRepository<DiscussionReport, Long> {

    /**
     쿼리 설명 : 특정 토론(discussion)에 대해 특정 회원(member)이 이미 신고했는지 존재 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM discussion_report
     WHERE discussion_id = ? AND member_id = ?;
     */
    boolean existsByDiscussionAndMember(final Discussion discussion, final Member member);
}
```