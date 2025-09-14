```java
public interface BlockRepository extends JpaRepository<Block, Long> {

    /**
     쿼리 설명 : 특정 회원(member)이 특정 타겟(target)을 차단했는지 존재 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM block
     WHERE member_id = ? AND target_id = ?;
     */
    boolean existsByMemberAndTarget(final Member member, final Member target);

    /**
     쿼리 설명 : 특정 회원(member)이 차단한 모든 Block 엔티티를 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM block
     WHERE member_id = ?;
     */
    @Query("""
                SELECT b FROM Block b
                WHERE b.member = :member
            """)
    List<Block> findBlocksByMember(final Member member);

    /**
     쿼리 설명 : 특정 회원(member)이 특정 타겟(target)을 차단한 Block 엔티티를 단일 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM block
     WHERE member_id = ? AND target_id = ?;
     */
    @Query("""
                SELECT b FROM Block b
                WHERE b.member = :member
                AND b.target = :target
            """)
    Block findByMemberAndTarget(final Member member, final Member target);
}

```