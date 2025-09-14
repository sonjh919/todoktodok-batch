```java
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     쿼리 설명 : 토큰(token) 값을 기준으로 단일 RefreshToken 엔티티를 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM refresh_token
     WHERE token = ?;
     */
    Optional<RefreshToken> findByToken(final String token);

    /**
     쿼리 설명 : 특정 토큰(refreshToken)이 존재하는지 여부를 확인하는 쿼리
     대응 쿼리 :
     SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     FROM refresh_token
     WHERE token = ?;
     */
    boolean existsByToken(final String refreshToken);
}

```