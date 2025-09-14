package database;

import static setting.Setting.DATABASE;
import static setting.Setting.OPTION;
import static setting.Setting.PASSWORD;
import static setting.Setting.SERVER;
import static setting.Setting.USERNAME;

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
