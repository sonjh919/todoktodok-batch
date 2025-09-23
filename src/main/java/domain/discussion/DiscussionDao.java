package domain.discussion;

import static setting.Setting.THRESHOLD;

import database.ConnectMysql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import util.DiscussionContentGenerator;
import util.RandomString;
import util.SkewedDistribution;
import util.TitleGenerator;

public class DiscussionDao {

    private final ConnectMysql connectMysql;

    public DiscussionDao(final ConnectMysql connectMysql) {
        this.connectMysql = connectMysql;
    }

    public void addBatch(final int totalCount, final int threads, final int memberCount, final int bookCount) {
        if(totalCount <= THRESHOLD){
            addBatchSingleThread(totalCount, memberCount, bookCount);
        }
        else{
            addBatchMultiThread(totalCount, threads, memberCount, bookCount);
        }
    }

    private void addBatchMultiThread(int totalCount, int threadCount, final int memberCount,
                                     final int bookCount) {
        System.out.println("Discussion Batch Start (Multi Thread)");
        long methodStart = System.currentTimeMillis();

        int chunkSize = totalCount / threadCount;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        TitleGenerator titleGenerator = new TitleGenerator();
        SkewedDistribution distribution = new SkewedDistribution(memberCount, bookCount);
        DiscussionContentGenerator contentGenerator = new DiscussionContentGenerator();

        for (int t = 0; t < threadCount; t++) {
            final int start = t * chunkSize;
            final int end = (t == threadCount - 1) ? totalCount : start + chunkSize;

            executor.submit(() -> {
                try (Connection connection = connectMysql.create();
                     PreparedStatement ps = connection.prepareStatement(
                             "INSERT INTO discussion (created_at, modified_at, deleted_at, member_id, book_id, title, content) " +
                                     "VALUES (?, ?, NULL, ?, ?, ?, ?)")) {

                    connection.setAutoCommit(false);

                    for (int i = start; i < end; i++) {
                        String time = String.valueOf(RandomString.generateRandomDateTime(LocalDateTime.now()));
                        long memberId = distribution.skewedMemberId();
                        long bookId = distribution.skewedBookId();

                        String title = titleGenerator.makeTitle((long)i, 0.28);
                        // 🔥 DiscussionContentGenerator 사용
                        String content = contentGenerator.makeContent((long)i, 0.30); // 30% 확률 인기 콘텐츠 재사용

                        ps.setString(1, time);
                        ps.setString(2, time);
                        ps.setLong(3, memberId);
                        ps.setLong(4, bookId);
                        ps.setString(5, title);
                        ps.setString(6, content);
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
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long methodEnd = System.currentTimeMillis();
        System.out.println("Discussion Batch Complete: " + (methodEnd - methodStart) + "ms");
    }



    private void addBatchSingleThread(int totalCount, final int memberCount, final int bookCount) {
        System.out.println("Discussion Batch Start (Single Thread)");
        long methodStart = System.currentTimeMillis();

        final var query =
                """
                INSERT INTO discussion (created_at, modified_at, deleted_at, member_id, book_id, title, content)
                VALUES (?, ?, NULL, ?, ?, ?, ?)
                """;

        try (Connection connection = connectMysql.create();
             final var preparedStatement = connection.prepareStatement(query)) {

            connection.setAutoCommit(false);

            for (int i = 0; i < totalCount; i++) {
                String time = String.valueOf(RandomString.generateRandomDateTime(LocalDateTime.now()));

                long memberId = (i % memberCount) + 1;
                long bookId = (i % bookCount) + 1;
                String title = RandomString.generateRandomHangulWithSpace(30);
                String content = "Discussion Content " + i;

                preparedStatement.setString(1, time);
                preparedStatement.setString(2, time);
                preparedStatement.setLong(3, memberId);
                preparedStatement.setLong(4, bookId);
                preparedStatement.setString(5, title);
                preparedStatement.setString(6, content);
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

        System.out.println("Discussion Batch End (Single Thread)");
        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds\n");
    }

    public void deleteAll() {
        System.out.println("Discussion Delete Start");
        long methodStart = System.currentTimeMillis();

        final String disableFkCheck = "SET FOREIGN_KEY_CHECKS = 0";
        final String truncateQuery = "TRUNCATE TABLE discussion";
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
            System.out.println("Discussion Delete End");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds\n");
    }

}
