```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     쿼리 설명 : 이메일(email)을 기준으로 단일 회원(Member)을 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM member
     WHERE email = ?;
     */
    Optional<Member> findByEmail(final String email);

    /**
     쿼리 설명 : 닉네임(nickname)이 이미 존재하는지 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM member
     WHERE nickname = ?;
     */
    boolean existsByNickname(final String nickname);

    /**
     쿼리 설명 : 이메일(email)이 이미 존재하는지 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM member
     WHERE email = ?;
     */
    boolean existsByEmail(final String email);

    /**
     쿼리 설명 : 회원 아이디(id)가 존재하는지 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM member
     WHERE id = ?;
     */
    boolean existsById(final Long id);
}

```