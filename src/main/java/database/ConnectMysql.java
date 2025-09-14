package database;

import static domain.Setting.DATABASE;
import static domain.Setting.OPTION;
import static domain.Setting.PASSWORD;
import static domain.Setting.SERVER;
import static domain.Setting.USERNAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectMysql {


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
