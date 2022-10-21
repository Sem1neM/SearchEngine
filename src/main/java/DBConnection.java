import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class DBConnection {
    private static Connection connection;
    private final static String dbName = "search_engine";
    private final static String dbUser = "root";
    private final static String dbPass = "cnjldflwfnbvbkkbvtnhjdsq";

    public static Connection getConnection() {

        if (connection == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/" + dbName;
                connection = DriverManager.getConnection(url, dbUser, dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS page");
                connection.createStatement().execute("CREATE TABLE page(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "path TEXT NOT NULL, " +
                        "code INT NOT NULL, " +
                        "content MEDIUMTEXT NOT NULL ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
