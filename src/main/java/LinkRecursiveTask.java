import org.hibernate.Session;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.Inet4Address;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveAction;

import static java.lang.Thread.sleep;

public class LinkRecursiveTask extends RecursiveAction {

    private Session session =  HibernateSessionFactoryUtils.getSessionFactory().openSession();
    private Link url;
    private Link rootUrl;
    private static CopyOnWriteArraySet<String> allLinks = new CopyOnWriteArraySet<>();



    public LinkRecursiveTask(Link url, Link rootUrl) {
        this.url = url;
        this.rootUrl = rootUrl;
    }
    @Override
    protected void compute() {
        Set<LinkRecursiveTask> taskList = new HashSet<>();
        try {
            sleep(500);
            linkParse(url);
            Connection connection = Jsoup.connect(url.getUrl())
                    .timeout(1000000);
            Document doc = connection
                    .userAgent("OurSearchBot")
                    .referrer("http://www.google.com")
                    .get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String absUrl = link.attr("abs:href");
                if (isCorrected(absUrl)) {
                    url.addChildren(new Link(absUrl));
                    allLinks.add(absUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Link link : url.getChildren()) {
            LinkRecursiveTask task = new LinkRecursiveTask(link, rootUrl);
            task.fork();
            taskList.add(task);
            try{
                linkParse(link);
                 link.setBodyMap(fieldParse(link, "body"));
                 link.setTitleMap(fieldParse(link, "title"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (LinkRecursiveTask task : taskList) {
            task.join();
        }
    }

    private boolean isCorrected(String url) {
        return (!url.isEmpty() && url.startsWith(rootUrl.getUrl())
                && !allLinks.contains(url) && !url.contains("#")
                && !url.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)"));
    }

    private void linkParse(Link link) throws Exception{
        link.setHtmlFile(Jsoup.connect(link.getUrl()).get().html());
        Connection.Response inboxJson = Jsoup.connect(link.getUrl())
                .timeout(1000000)
                .header("Accept", "text/javascript")
                .userAgent("OurSearchBot")
                .execute();
        int statusCode = inboxJson.statusCode();
        link.setCode(statusCode);
        Jsoup.parse(link.getUrl()).getAllElements();

    }

    private Map<String, Integer> fieldParse(Link link, String element) throws Exception {
        LemmaFinder lemmaFinder = LemmaFinder.getInstance();
        Connection connection = Jsoup.connect(link.getUrl());
        Document document = connection.get();
        Elements elements = document.select(element);
        return lemmaFinder.collectLemmas(Jsoup.parse(String.valueOf(elements)).text());
    }
}
