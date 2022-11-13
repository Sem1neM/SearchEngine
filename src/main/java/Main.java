import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String SITE_URL = "http://www.playback.ru/";

    private static  Link rootUrl = new Link(SITE_URL.trim());
    public static void main(String[] args) {

        new ForkJoinPool().invoke(new LinkRecursiveTask(rootUrl, rootUrl));

        DBModel dbModel = new DBModel(SITE_URL);
        dbModel.fillDataBase(rootUrl);
        dbModel.saveLemmas(rootUrl);
    }

}
