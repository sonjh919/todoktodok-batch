import static domain.Setting.ADD_FLAG;
import static domain.Setting.BOOK_COUNT;
import static domain.Setting.COMMENT_COUNT;
import static domain.Setting.DELETE_FLAG;
import static domain.Setting.DISCUSSION_COUNT;
import static domain.Setting.MEMBER_COUNT;
import static domain.Setting.REPLY_COUNT;
import static domain.Setting.THREAD;

import database.ConnectMysql;
import domain.BookDao;
import domain.CommentDao;
import domain.DiscussionDao;
import domain.MemberDao;
import domain.ReplyDao;

public class Application {

    public static void main(String[] args) {

        ConnectMysql connectMysql = new ConnectMysql();

        // setting
        MemberDao memberDao = new MemberDao(connectMysql);
        BookDao bookDao = new BookDao(connectMysql);
        DiscussionDao discussionDao = new DiscussionDao(connectMysql);
        CommentDao commentDao = new CommentDao(connectMysql);
        ReplyDao replyDao = new ReplyDao(connectMysql);

        if(DELETE_FLAG){
            memberDao.deleteAll();
            bookDao.deleteAll();
            discussionDao.deleteAll();
            commentDao.deleteAll();
            replyDao.deleteAll();
        }

        if(ADD_FLAG){
            memberDao.addBatch(MEMBER_COUNT, THREAD);
            bookDao.addBatch(BOOK_COUNT, THREAD);
            discussionDao.addBatch(DISCUSSION_COUNT, THREAD, MEMBER_COUNT, BOOK_COUNT);
            commentDao.addBatch(COMMENT_COUNT, THREAD, MEMBER_COUNT, DISCUSSION_COUNT);
            replyDao.addBatch(REPLY_COUNT, THREAD, MEMBER_COUNT, COMMENT_COUNT);
        }

    }
}
