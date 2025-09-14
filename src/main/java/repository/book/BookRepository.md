```java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    /**
     쿼리 설명 : ISBN 컬럼을 기준으로 단일 책을 조회하는 쿼리
     대응 쿼리 :
     SELECT *
     FROM book
     WHERE isbn = ?;
     */
    Optional<Book> findByIsbn(final String isbn);

     /**
      쿼리 설명 : 해당 회원이 작성한 Discussion, Comment, Reply에 연관된 책들을 중복 없이 모두 조회
      대응 쿼리 :
      SELECT DISTINCT b.*
      FROM discussion d
      JOIN book b ON d.book_id = b.id
      WHERE d.member_id = ?

      UNION

      SELECT DISTINCT b.*
      FROM comment c
      JOIN discussion d ON c.discussion_id = d.id
      JOIN book b ON d.book_id = b.id
      WHERE c.member_id = ?

      UNION

      SELECT DISTINCT b.*
      FROM reply r
      JOIN comment c ON r.comment_id = c.id
      JOIN discussion d ON c.discussion_id = d.id
      JOIN book b ON d.book_id = b.id
      WHERE r.member_id = ?;
      */
    @Query("""
    SELECT DISTINCT d.book FROM Discussion d
    WHERE d.member = :member
    UNION
    SELECT DISTINCT c.discussion.book FROM Comment c
    WHERE c.member = :member
    UNION
    SELECT DISTINCT r.comment.discussion.book FROM Reply r
    WHERE r.member = :member
    """)
    List<Book> findActiveBooksByMember(@Param("member") final Member member);
}
```