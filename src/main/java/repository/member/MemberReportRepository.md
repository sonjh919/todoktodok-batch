```java
public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {

    /**
     쿼리 설명 : 특정 회원(member)이 특정 타겟(target)을 이미 신고했는지 존재 여부 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM member_report
     WHERE member_id = ? AND target_id = ?;
     */
    boolean existsByMemberAndTarget(final Member member, final Member target);
}
```