package domain;

import database.ConnectMysql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BookDao {

    private final ConnectMysql connectMysql;

    public BookDao(final ConnectMysql connectMysql) {
        this.connectMysql = connectMysql;
    }

    public void addBookBatchMultiThread(int totalCount, int threadCount) {
        System.out.println("Book Batch Start (Multi Thread)");
        long methodStart = System.currentTimeMillis();

        int chunkSize = totalCount / threadCount;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int start = t * chunkSize;
            final int end = (t == threadCount - 1) ? totalCount : start + chunkSize;

            executor.submit(() -> {
                final String query =
                        "INSERT INTO book (created_at, modified_at, deleted_at, isbn, author, image, publisher, summary, title) " +
                                "VALUES (?, ?, NULL, ?, ?, ?, ?, ?, ?)";

                try (Connection connection = connectMysql.create();
                     PreparedStatement ps = connection.prepareStatement(query)) {

                    connection.setAutoCommit(false);
                    String time = String.valueOf(LocalDateTime.now());

                    for (int i = start; i < end; i++) {
                        // 고유 isbn 생성 (예: "9780000000000" 형태)
                        String isbn = String.format("978%010d", i);

                        String author = "author" + i;
                        String image = "image" + i;
                        String publisher = "publisher" + i;
                        String summary = "summary" + i;
                        String title = "title" + i;

                        ps.setString(1, time);
                        ps.setString(2, time);
                        // deleted_at은 NULL로 고정
                        ps.setString(3, isbn);
                        ps.setString(4, author);
                        ps.setString(5, image);
                        ps.setString(6, publisher);
                        ps.setString(7, summary);
                        ps.setString(8, title);
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
            System.out.println("Book Batch End (Multi Thread)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds");
    }

    public void addBookBatch(int totalCount) {
        System.out.println("Member Batch Start (Single Thread)");
        long methodStart = System.currentTimeMillis();

        final var query =
                """
                INSERT INTO book (created_at, modified_at, deleted_at, isbn, author, image, publisher, summary, title)
                VALUES (?, ?, NULL, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = connectMysql.create();
             final var preparedStatement = connection.prepareStatement(query)) {

            connection.setAutoCommit(false); // 수동 커밋 제어

            String time = String.valueOf(LocalDateTime.now());

            for (int i = 0; i < totalCount; i++) {
                // 고유 isbn 생성 (예: "9780000000000" 형태)
                String isbn = String.format("978%010d", i);
                String author = "author" + i;
                String image = "image" + i;
                String publisher = "publisher" + i;
                String summary = "summary" + i;
                String title = "title" + i;

                preparedStatement.setString(1, time);
                preparedStatement.setString(2, time);
                // deleted_at은 NULL 고정
                preparedStatement.setString(3, isbn);
                preparedStatement.setString(4, author);
                preparedStatement.setString(5, image);
                preparedStatement.setString(6, publisher);
                preparedStatement.setString(7, summary);
                preparedStatement.setString(8, title);
                preparedStatement.addBatch();

                if (i % 1000 == 0 && i != 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }

            preparedStatement.executeBatch(); // 마지막 잔여 배치 실행
            connection.commit();

            connectMysql.close(connection);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Book Batch End (Single Thread)");
        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds");
    }

    public void deleteAllBooks() {
        System.out.println("Book Delete Start");
        long methodStart = System.currentTimeMillis();

        final String query = "DELETE FROM book";

        try (Connection connection = connectMysql.create();
             final var preparedStatement = connection.prepareStatement(query)) {

            connection.setAutoCommit(false);

            preparedStatement.executeUpdate();

            connection.commit();
            connectMysql.close(connection);
            System.out.println("Book Delete End");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        long methodEnd = System.currentTimeMillis();
        long elapsedSeconds = (methodEnd - methodStart) / 1000;
        System.out.println("Total method elapsed time: " + elapsedSeconds + " seconds");
    }

}
