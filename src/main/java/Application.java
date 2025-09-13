import domain.BookDao;
import domain.DiscussionDao;
import domain.MemberDao;
import database.ConnectMysql;

public class Application {
    public static void main(String[] args) {

        ConnectMysql connectMysql = new ConnectMysql();

        int cores = Runtime.getRuntime().availableProcessors();
        int threads = cores*2;
        System.out.println("CPU cores: " + cores);

        int memberCount = 1000;
        int bookCount = 100;
        int discussionCount = 1000000;
        int commentCount = 1000000;
        int replyCount = 1000000;

        // Member
        MemberDao memberDao = new MemberDao(connectMysql);
//        memberDao.deleteAllMembers();
        memberDao.addMemberBatch(memberCount);
//        memberDao.addMemberBatchMultiThread(memberCount, threads);

        // Book
        BookDao bookDao = new BookDao(connectMysql);
//        bookDao.deleteAllBooks();
        bookDao.addBookBatch(bookCount);
//        bookDao.addBookBatchMultiThread(bookCount, threads);

        // Discussion
        DiscussionDao discussionDao = new DiscussionDao(connectMysql);
//        discussionDao.deleteAllDiscussions();
//        discussionDao.addDiscussionBatch(discussionCount, memberCount, bookCount);
        discussionDao.addDiscussionBatchMultiThread(discussionCount,threads, memberCount, bookCount);

        // Comment

        // Reply
    }
}
