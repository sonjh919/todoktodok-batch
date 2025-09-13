import database.ConnectMysql;
import domain.BookDao;
import domain.CommentDao;
import domain.DiscussionDao;
import domain.MemberDao;
import domain.ReplyDao;

public class Application {

    /*
    SET SQL_SAFE_UPDATES = 0;
    DELETE FROM reply;
    ALTER TABLE reply AUTO_INCREMENT = 1;
    DELETE FROM comment;
    ALTER TABLE comment AUTO_INCREMENT = 1;
    DELETE FROM discussion;
    ALTER TABLE discussion AUTO_INCREMENT = 1;
    DELETE FROM book;
    ALTER TABLE book AUTO_INCREMENT = 1;
    DELETE FROM member;
    ALTER TABLE member AUTO_INCREMENT = 1;
     */

    public static int ONE_HUNDRED_THOUSAND = 100000; // 십만
    public static int ONE_MILLION = 1000000; // 백만
    public static int TEN_MILLION = 10000000; // 천만

    public static void main(String[] args) {

        ConnectMysql connectMysql = new ConnectMysql();

        int cores = Runtime.getRuntime().availableProcessors();
        int threads = cores * 2;
        System.out.println("CPU cores: " + cores);

        int memberCount = 1000;
        int bookCount = 100;
        int discussionCount = ONE_MILLION;
        int commentCount = TEN_MILLION;
        int replyCount = TEN_MILLION;

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
        discussionDao.addDiscussionBatchMultiThread(discussionCount, threads, memberCount, bookCount);

        // Comment
        CommentDao commentDao = new CommentDao(connectMysql);
//        commentDao.deleteAllComments();
//        commentDao.addCommentBatch(commentCount, memberCount, discussionCount);
        commentDao.addCommentBatchMultiThread(commentCount, threads, memberCount, discussionCount);

        // Reply
        ReplyDao replyDao = new ReplyDao(connectMysql);
//        replyDao.deleteAllReplies();
//        replyDao.addReplyBatch(replyCount, memberCount, commentCount);
        replyDao.addReplyBatchMultiThread(replyCount, threads, memberCount, commentCount);
    }
}
