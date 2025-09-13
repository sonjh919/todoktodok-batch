package domain;

import database.ConnectMysql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MemberDao {

    private final ConnectMysql connectMysql;

    public MemberDao(final ConnectMysql connectMysql) {
        this.connectMysql = connectMysql;
    }

    public void addMemberBatchMultiThread(int totalCount, int threadCount) {
        System.out.println("Batch Start");
        long methodStart = System.currentTimeMillis();

        int chunkSize = totalCount / threadCount;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int start = t * chunkSize;
            final int end = (t == threadCount - 1) ? totalCount : start + chunkSize;

            executor.submit(() -> {
                try (Connection connection = connectMysql.create();
                     PreparedStatement ps = connection.prepareStatement(
                             "INSERT INTO MEMBER (email, nickname, profile_image, profile_message, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)")) {

                    connection.setAutoCommit(false);
                    String time = String.valueOf(LocalDateTime.now());

                    for (int i = start; i < end; i++) {
                        String temp = String.format("test%d", i);
                        ps.setString(1, String.format("%s@test.com", temp));
                        ps.setString(2, temp);
                        ps.setString(3, temp);
                        ps.setString(4, temp);
                        ps.setString(5, time);
                        ps.setString(6, time);
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
            System.out.println("Batch End");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds");
    }

    public void deleteAllMembers() {
        System.out.println("Delete Start");
        long methodStart = System.currentTimeMillis();

        final String query = "DELETE FROM MEMBER";

        try (Connection connection = connectMysql.create();
             final var preparedStatement = connection.prepareStatement(query)) {

            connection.setAutoCommit(false);

            preparedStatement.executeUpdate();

            connection.commit();
            connectMysql.close(connection);
            System.out.println("Delete End");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds");
    }


}
