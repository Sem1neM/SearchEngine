import models.Page;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private static StringBuilder insertQuery = new StringBuilder();
    private static final String SITE_URL = "http://www.playback.ru/";
    public static void main(String[] args) {

        Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();

        Link rootUrl = new Link(SITE_URL.trim());
        new ForkJoinPool().invoke(new LinkRecursiveTask(rootUrl, rootUrl));
        for(Link child : rootUrl.getChildren()){
            Transaction transaction = session.beginTransaction();
            session.save(savePage(child));
            if(transaction.isActive()) {
                transaction.commit();
            }
        }

    }
    private static Page savePage(Link child){
        String root = SITE_URL.substring(0, SITE_URL.length()-1);
        String path = child.getUrl().replace(root, "");
        String html = child.getHtmlFile();
        String content = MySQLUtils.mysql_real_escape_string(html);
        return new Page(path, child.getCode(), content);
    }
}
