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
    private static StringBuilder insertQuery = new StringBuilder();
    private static final SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");

    public static Connection getConnection() {

        if (connection == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/" + dbName;
                connection = DriverManager.getConnection(url, dbUser, dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
                connection.createStatement().execute("CREATE TABLE voter_count(" +
                        //"id INT NOT NULL AUTO_INCREMENT, " +
                        "name VARCHAR(200), " +
                        "birthDate DATE NOT NULL, " +
                        "count INT NOT NULL, " +
                        "PRIMARY KEY (name))");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

}
