package domain;

import database.ConnectMysql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiscussionDao {

    private final ConnectMysql connectMysql;

    public DiscussionDao(final ConnectMysql connectMysql) {
        this.connectMysql = connectMysql;
    }

    public void addDiscussionBatchMultiThread(int totalCount, int threadCount, final int memberCount,
                                              final int bookCount) {
        System.out.println("Discussion Batch Start (Multi Thread)");
        long methodStart = System.currentTimeMillis();

        int chunkSize = totalCount / threadCount;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int start = t * chunkSize;
            final int end = (t == threadCount - 1) ? totalCount : start + chunkSize;

            executor.submit(() -> {
                try (Connection connection = connectMysql.create();
                     PreparedStatement ps = connection.prepareStatement(
                             "INSERT INTO discussion (created_at, modified_at, deleted_at, member_id, book_id, title, content) VALUES (?, ?, NULL, ?, ?, ?, ?)")) {

                    connection.setAutoCommit(false);
                    String time = String.valueOf(LocalDateTime.now());

                    for (int i = start; i < end; i++) {
                        // 예시용 member_id, book_id 생성 (실제 환경에 맞게 조정)
                        long memberId = (i % memberCount) + 1; // 1~count 반복
                        long bookId = (i % bookCount) + 1;    // 1~count 반복
                        String title = "Discussion Title " + i;
                        String content = "Discussion Content " + i;

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
            executor.awaitTermination(1, TimeUnit.MINUTES);
            System.out.println("Discussion Batch End (Multi Thread)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds\n");
    }

    public void addDiscussionBatch(int totalCount, final int memberCount, final int bookCount) {
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

            String time = String.valueOf(LocalDateTime.now());

            for (int i = 0; i < totalCount; i++) {
                long memberId = (i % memberCount) + 1;
                long bookId = (i % bookCount) + 1;
                String title = "Discussion Title " + i;
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

    public void deleteAllDiscussions() {
        System.out.println("Discussion Delete Start");
        long methodStart = System.currentTimeMillis();

        final String query = "DELETE FROM discussion";

        try (Connection connection = connectMysql.create();
             final var preparedStatement = connection.prepareStatement(query)) {

            connection.setAutoCommit(false);

            preparedStatement.executeUpdate();

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
