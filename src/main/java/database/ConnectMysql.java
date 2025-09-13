package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectMysql {
    private static final String SERVER = "localhost:3306"; // MySQL 서버 주소
    private static final String DATABASE = "todoktodok_test"; // MySQL DATABASE 이름
    private static final String OPTION = "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = ""; //  MySQL 서버 아이디
    private static final String PASSWORD = ""; // MySQL 서버 비밀번호

    public Connection create() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DATABASE + OPTION, USERNAME, PASSWORD);
        } catch (final SQLException e) {
            System.err.println("SQLState: " + e.getSQLState());
            e.printStackTrace();
            throw new RuntimeException("[ERROR] DB 연결 오류");
        }
    }

    public void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("[ERROR] DB 연결 닫기 오류");
        }
    }
}
