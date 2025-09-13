import domain.BookDao;
import domain.MemberDao;
import database.ConnectMysql;

public class Application {
    public static void main(String[] args) {

        ConnectMysql connectMysql = new ConnectMysql();

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU cores: " + cores);

        // Member
//        MemberDao memberDao = new MemberDao(connectMysql);
//        memberDao.deleteAllMembers();
//        memberDao.addMemberBatch(1000000);
//        memberDao.addMemberBatchMultiThread(1000000, cores*2);

        // Book
//        BookDao bookDao = new BookDao(connectMysql);
//        bookDao.deleteAllBooks();
//        bookDao.addBookBatchMultiThread(100, cores*2);
//        bookDao.addBookBatch(100);

        // Discussion

        // Comment

        // Reply
    }
}
