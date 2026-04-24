package connection;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private DatabaseConnection() {

    }

    public void connectToDatabase() throws SQLException {
        String server = "localhost";
        String port = "3306";
        String database = "chat_application";
        String userName = "root";
        String password = "";
        try {
            // MySQL Connector/J 8+
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            // Fallback for older Connector/J (if used)
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ignore) {
                // DriverManager may still find the driver via Service Provider
            }
        }

        String url = "jdbc:mysql://" + server + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        connection = java.sql.DriverManager.getConnection(url, userName, password);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
