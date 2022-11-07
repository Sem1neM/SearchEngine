import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;
    private final static String dbName = "search_engine";
    private final static String dbUser = "root";
    private final static String dbPass = "cnjldflwfnbvbkkbvtnhjdsq";
    private static StringBuilder insertQuery = new StringBuilder();

    public static Connection getConnection() {

        if (connection == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false";
                connection = DriverManager.getConnection(url, dbUser, dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS page");
                connection.createStatement().execute("DROP TABLE IF EXISTS field");
                connection.createStatement().execute("DROP TABLE IF EXISTS lemma");
                connection.createStatement().execute("DROP TABLE IF EXISTS page_index");
                connection.createStatement().execute("CREATE TABLE page(" +
                        "id INT NOT NULL primary key AUTO_INCREMENT, " +
                        "path TEXT NOT NULL, " +
                        "code INT NOT NULL, " +
                        "content MEDIUMTEXT NOT NULL )");
                connection.createStatement().execute("CREATE TABLE field(" +
                        "id INT NOT NULL primary key AUTO_INCREMENT, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "selector VARCHAR(255) NOT NULL, " +
                        "weight FLOAT NOT NULL)");
                connection.createStatement().execute("CREATE TABLE lemma(" +
                        "id INT NOT NULL primary key AUTO_INCREMENT, " +
                        "lemma VARCHAR(255) NOT NULL, " +
                        "frequency INT NOT NULL)");
                connection.createStatement().execute("CREATE TABLE page_index(" +
                        "id INT NOT NULL primary key AUTO_INCREMENT, " +
                        "page_id INT NOT NULL, " +
                        "lemma_id INT NOT NULL, " +
                        "page_rank FLOAT NOT NULL)");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void linkCounter(Link node, Link rootLink) throws SQLException{
        for (Link child : node.getChildren()){
            builderAppend(child, rootLink);
        }
        executeMultiInsert();
    }



    public static void executeMultiInsert() throws SQLException {
        String sql = "INSERT INTO page(path, code, content)" +
                " VALUES " + insertQuery.toString();
        DBConnection.getConnection().prepareStatement(sql).execute(sql);
        insertQuery = new StringBuilder();
    }

    public static void builderAppend(Link child, Link rootLink){

        String root = rootLink.getUrl().substring(0, rootLink.getUrl().length()-1);
        String path = child.getUrl().replace(root, "");
        String html = child.getHtmlFile();
        try {
            String content = MySQLUtils.mysql_real_escape_string(html);
            int code = child.getCode();
            insertQuery.append(insertQuery.isEmpty() ? "" : ",")
                    .append("('")
                    .append(path)
                    .append("', ")
                    .append(code)
                    .append(" , '")
                    .append(content)
                    .append("')");
        }
        catch (Exception e){
            e.getStackTrace();
        }
    }
}
