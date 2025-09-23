package domain.reply;

import static setting.Setting.THRESHOLD;

import database.ConnectMysql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import util.CommentGenerator;
import util.RandomString;

public class ReplyDao {

    private final ConnectMysql connectMysql;

    public ReplyDao(final ConnectMysql connectMysql) {
        this.connectMysql = connectMysql;
    }

    public void addBatch(final int totalCount, final int threads, final int memberCount, final int commentCount) {
        if(totalCount <= THRESHOLD){
            addBatchSingleThread(totalCount, memberCount, commentCount);
        }
        else{
            addBatchMultiThread(totalCount, threads, memberCount, commentCount);
        }
    }

    public void addBatchMultiThread(int totalCount, int threadCount, final int memberCount, final int commentCount) {
        System.out.println("Reply Batch Start (Multi Thread)");
        long methodStart = System.currentTimeMillis();

        int chunkSize = totalCount / threadCount;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // CommentGenerator 인스턴스 재사용
        CommentGenerator commentGenerator = new CommentGenerator();

        for (int t = 0; t < threadCount; t++) {
            final int start = t * chunkSize;
            final int end = (t == threadCount - 1) ? totalCount : start + chunkSize;

            executor.submit(() -> {
                final String query =
                        "INSERT INTO reply (created_at, modified_at, deleted_at, comment_id, member_id, content) " +
                                "VALUES (?, ?, NULL, ?, ?, ?)";

                try (Connection connection = connectMysql.create();
                     PreparedStatement ps = connection.prepareStatement(query)) {

                    connection.setAutoCommit(false);

                    for (int i = start; i < end; i++) {
                        String time = String.valueOf(RandomString.generateRandomDateTime(LocalDateTime.now()));

                        long commentId = (i % commentCount) + 1;
                        long memberId = (i % memberCount) + 1;

                        // 🔥 CommentGenerator 사용
                        String content = commentGenerator.makeComment((long)i, 0.15); // 15% 확률로 인기 댓글 재사용

                        ps.setString(1, time);
                        ps.setString(2, time);
                        ps.setLong(3, commentId);
                        ps.setLong(4, memberId);
                        ps.setString(5, content);
                        ps.addBatch();

                        if ((i - start + 1) % 1000 == 0) {
                            ps.executeBatch();
                            ps.clearBatch();
                        }
                    }
                    ps.executeBatch();
                    connection.commit();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
            executor.shutdownNow();
            System.out.println("Reply Batch End (Multi Thread)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds\n");
    }

    public void addBatchSingleThread(int totalCount, final int memberCount, final int commentCount) {
        System.out.println("Reply Batch Start (Single Thread)");
        long methodStart = System.currentTimeMillis();

        final var query =
                """
                INSERT INTO reply (created_at, modified_at, deleted_at, comment_id, member_id, content)
                VALUES (?, ?, NULL, ?, ?, ?)
                """;

        try (Connection connection = connectMysql.create();
             final var preparedStatement = connection.prepareStatement(query)) {

            connection.setAutoCommit(false);

            for (int i = 0; i < totalCount; i++) {
                String time = String.valueOf(RandomString.generateRandomDateTime(LocalDateTime.now()));

                long commentId = (i % commentCount) + 1;
                long memberId = (i % memberCount) + 1;
                String content = "Reply Content " + i;

                preparedStatement.setString(1, time);
                preparedStatement.setString(2, time);
                preparedStatement.setLong(3, commentId);
                preparedStatement.setLong(4, memberId);
                preparedStatement.setString(5, content);
                preparedStatement.addBatch();

                if (i % 1000 == 0 && i != 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }

            preparedStatement.executeBatch();
            connection.commit();

            connectMysql.close(connection);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Reply Batch End (Single Thread)");
        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds\n");
    }

    public void deleteAll() {
        System.out.println("Reply Delete Start");
        long methodStart = System.currentTimeMillis();

        final String disableFkCheck = "SET FOREIGN_KEY_CHECKS = 0";
        final String truncateQuery = "TRUNCATE TABLE reply";
        final String enableFkCheck = "SET FOREIGN_KEY_CHECKS = 1";

        try (Connection connection = connectMysql.create()) {
            connection.setAutoCommit(false);

            try (var stmt = connection.createStatement()) {
                stmt.execute(disableFkCheck);
                stmt.execute(truncateQuery);
                stmt.execute(enableFkCheck);
            }

            connection.commit();
            connectMysql.close(connection);
            System.out.println("Reply Delete End");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds\n");
    }
}
