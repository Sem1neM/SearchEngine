import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String SITE_URL = "http://www.playback.ru/";

    private static  Link rootUrl = new Link(SITE_URL.trim());
    public static void main(String[] args) {
        LinkRecursiveTask linkRecursiveTask = new LinkRecursiveTask(rootUrl, rootUrl);
        new ForkJoinPool().invoke(linkRecursiveTask);

        DBModel dbModel = new DBModel(linkRecursiveTask);
        dbModel.savePage(rootUrl);
        linkRecursiveTask.addLemmaIndex();
        dbModel.saveLemmas();
        dbModel.saveIndex();

    }

}
