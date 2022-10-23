import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String SITE_URL = "http://www.playback.ru/ ";
    public static void main(String[] args) {
        Link rootUrl = new Link(SITE_URL);
        new ForkJoinPool().invoke(new LinkRecursiveTask(rootUrl, rootUrl));
        DBConnection.getConnection();
        try {
            DBConnection.linkCounter(rootUrl);
        }
        catch (SQLException e){
            e.printStackTrace();
        }




    }
}
