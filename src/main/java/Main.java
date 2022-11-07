import org.jsoup.Jsoup;

import java.io.IOException;

public class Main {
    private static final String SITE_URL = "http://www.playback.ru/";
    public static void main(String[] args) {
        try {
            Jsoup.connect(SITE_URL).get().getAllElements();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        //Jsoup.parse(SITE_URL).getAllElements().forEach(System.out::println);
//        Link rootUrl = new Link(SITE_URL.trim());
//        new ForkJoinPool().invoke(new LinkRecursiveTask(rootUrl, rootUrl));
//        DBConnection.getConnection();
//        try {
//            DBConnection.linkCounter(rootUrl,rootUrl);
//        }
//        catch (SQLException e){
//            e.printStackTrace();
//        }




    }
}
