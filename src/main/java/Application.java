import static domain.Setting.ADD_FLAG;
import static domain.Setting.BOOK_COUNT;
import static domain.Setting.COMMENT_COUNT;
import static domain.Setting.CURRENT_BOOK_COUNT;
import static domain.Setting.CURRENT_COMMENT_COUNT;
import static domain.Setting.CURRENT_DISCUSSION_COUNT;
import static domain.Setting.CURRENT_MEMBER_COUNT;
import static domain.Setting.DELETE_FLAG;
import static domain.Setting.DISCUSSION_COUNT;
import static domain.Setting.MEMBER_COUNT;
import static domain.Setting.REPLY_COUNT;
import static domain.Setting.THREAD;
import static domain.Setting.ZERO;

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

        if (DELETE_FLAG) {
            memberDao.deleteAll();
            bookDao.deleteAll();
            discussionDao.deleteAll();
            commentDao.deleteAll();
            replyDao.deleteAll();
        }

        if (ADD_FLAG) {
            if (MEMBER_COUNT > ZERO) {
                memberDao.addBatch(MEMBER_COUNT, THREAD);
            }

            if (BOOK_COUNT > ZERO) {
                bookDao.addBatch(BOOK_COUNT, THREAD);
            }

            if (DISCUSSION_COUNT > ZERO) {
                discussionDao.addBatch(DISCUSSION_COUNT, THREAD, MEMBER_COUNT + CURRENT_MEMBER_COUNT,
                        BOOK_COUNT + CURRENT_BOOK_COUNT);
            }

            if (COMMENT_COUNT > ZERO) {
                commentDao.addBatch(COMMENT_COUNT, THREAD, MEMBER_COUNT + CURRENT_MEMBER_COUNT,
                        DISCUSSION_COUNT + CURRENT_DISCUSSION_COUNT);
            }

            if (REPLY_COUNT > ZERO) {
                replyDao.addBatch(REPLY_COUNT, THREAD, MEMBER_COUNT + CURRENT_MEMBER_COUNT,
                        COMMENT_COUNT + CURRENT_COMMENT_COUNT);
            }
        }
    }
}
