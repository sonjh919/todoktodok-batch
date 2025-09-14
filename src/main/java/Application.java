import static setting.Setting.ADD_FLAG;
import static setting.Setting.BLOCK_COUNT;
import static setting.Setting.BOOK_COUNT;
import static setting.Setting.COMMENT_COUNT;
import static setting.Setting.COMMENT_LIKE_COUNT;
import static setting.Setting.COMMENT_REPORT_COUNT;
import static setting.Setting.CURRENT_BOOK_COUNT;
import static setting.Setting.CURRENT_COMMENT_COUNT;
import static setting.Setting.CURRENT_DISCUSSION_COUNT;
import static setting.Setting.CURRENT_MEMBER_COUNT;
import static setting.Setting.CURRENT_REPLY_COUNT;
import static setting.Setting.DELETE_FLAG;
import static setting.Setting.DISCUSSION_COUNT;
import static setting.Setting.DISCUSSION_LIKE_COUNT;
import static setting.Setting.DISCUSSION_REPORT_COUNT;
import static setting.Setting.MEMBER_COUNT;
import static setting.Setting.MEMBER_REPORT_COUNT;
import static setting.Setting.REFRESH_TOKEN_COUNT;
import static setting.Setting.REPLY_COUNT;
import static setting.Setting.REPLY_LIKE_COUNT;
import static setting.Setting.REPLY_REPORT_COUNT;
import static setting.Setting.THREAD;
import static setting.Setting.ZERO;

import database.ConnectMysql;
import domain.book.BookDao;
import domain.comment.CommentDao;
import domain.comment.CommentLikeDao;
import domain.comment.CommentReportDao;
import domain.discussion.DiscussionDao;
import domain.discussion.DiscussionLikeDao;
import domain.discussion.DiscussionReportDao;
import domain.member.BlockDao;
import domain.member.MemberDao;
import domain.member.MemberReportDao;
import domain.member.RefreshTokenDao;
import domain.reply.ReplyDao;
import domain.reply.ReplyLikeDao;
import domain.reply.ReplyReportDao;

public class Application {

    public static void main(String[] args) {

        ConnectMysql connectMysql = new ConnectMysql();

        // setting
        MemberDao memberDao = new MemberDao(connectMysql);
        BlockDao blockDao = new BlockDao(connectMysql);
        MemberReportDao memberReportDao = new MemberReportDao(connectMysql);
        RefreshTokenDao refreshTokenDao = new RefreshTokenDao(connectMysql);

        BookDao bookDao = new BookDao(connectMysql);

        DiscussionDao discussionDao = new DiscussionDao(connectMysql);
        DiscussionLikeDao discussionLikeDao = new DiscussionLikeDao(connectMysql);
        DiscussionReportDao discussionReportDao = new DiscussionReportDao(connectMysql);

        CommentDao commentDao = new CommentDao(connectMysql);
        CommentLikeDao commentLikeDao = new CommentLikeDao(connectMysql);
        CommentReportDao commentReportDao = new CommentReportDao(connectMysql);

        ReplyDao replyDao = new ReplyDao(connectMysql);
        ReplyLikeDao replyLikeDao = new ReplyLikeDao(connectMysql);
        ReplyReportDao replyReportDao = new ReplyReportDao(connectMysql);

        if (DELETE_FLAG) {
            memberDao.deleteAll();
            bookDao.deleteAll();
            discussionDao.deleteAll();
            commentDao.deleteAll();
            replyDao.deleteAll();
        }

        if (ADD_FLAG) {
            // member
            if (MEMBER_COUNT > ZERO) {
                memberDao.addBatch(MEMBER_COUNT, THREAD);
            }

            if (BLOCK_COUNT > ZERO) {
                blockDao.addBatch(BLOCK_COUNT, THREAD, MEMBER_COUNT + CURRENT_MEMBER_COUNT);
            }

            if (MEMBER_REPORT_COUNT > ZERO) {
                memberReportDao.addBatch(MEMBER_REPORT_COUNT, THREAD, MEMBER_COUNT + CURRENT_MEMBER_COUNT);
            }

            if (REFRESH_TOKEN_COUNT > ZERO) {
                refreshTokenDao.addBatch(REFRESH_TOKEN_COUNT, THREAD);
            }

            // book
            if (BOOK_COUNT > ZERO) {
                bookDao.addBatch(BOOK_COUNT, THREAD);
            }

            // discussion
            if (DISCUSSION_COUNT > ZERO) {
                discussionDao.addBatch(DISCUSSION_COUNT, THREAD, MEMBER_COUNT + CURRENT_MEMBER_COUNT,
                        BOOK_COUNT + CURRENT_BOOK_COUNT);
            }

            if (DISCUSSION_LIKE_COUNT > ZERO) {
                discussionLikeDao.addBatch(DISCUSSION_LIKE_COUNT, THREAD, DISCUSSION_COUNT + CURRENT_DISCUSSION_COUNT,
                        MEMBER_COUNT + CURRENT_MEMBER_COUNT);
            }

            if (DISCUSSION_REPORT_COUNT > ZERO) {
                discussionReportDao.addBatch(DISCUSSION_REPORT_COUNT, THREAD,
                        DISCUSSION_COUNT + CURRENT_DISCUSSION_COUNT,
                        MEMBER_COUNT + CURRENT_MEMBER_COUNT);
            }

            // comment
            if (COMMENT_COUNT > ZERO) {
                commentDao.addBatch(COMMENT_COUNT, THREAD, MEMBER_COUNT + CURRENT_MEMBER_COUNT,
                        DISCUSSION_COUNT + CURRENT_DISCUSSION_COUNT);
            }

            if (COMMENT_LIKE_COUNT > ZERO) {
                commentLikeDao.addBatch(COMMENT_LIKE_COUNT, THREAD, COMMENT_COUNT + CURRENT_COMMENT_COUNT,
                        MEMBER_COUNT + CURRENT_MEMBER_COUNT);
            }

            if (COMMENT_REPORT_COUNT > ZERO) {
                commentReportDao.addBatch(COMMENT_REPORT_COUNT, THREAD, COMMENT_COUNT + CURRENT_COMMENT_COUNT,
                        MEMBER_COUNT + CURRENT_MEMBER_COUNT);
            }

            // reply
            if (REPLY_COUNT > ZERO) {
                replyDao.addBatch(REPLY_COUNT, THREAD, MEMBER_COUNT + CURRENT_MEMBER_COUNT,
                        COMMENT_COUNT + CURRENT_COMMENT_COUNT);
            }

            if (REPLY_LIKE_COUNT > ZERO) {
                replyLikeDao.addBatch(REPLY_LIKE_COUNT, THREAD, REPLY_COUNT + CURRENT_REPLY_COUNT,
                        MEMBER_COUNT + CURRENT_MEMBER_COUNT);
            }

            if (REPLY_REPORT_COUNT > ZERO) {
                replyReportDao.addBatch(REPLY_REPORT_COUNT, THREAD, REPLY_COUNT + CURRENT_REPLY_COUNT,
                        MEMBER_COUNT + CURRENT_MEMBER_COUNT);
            }
        }
    }
}
