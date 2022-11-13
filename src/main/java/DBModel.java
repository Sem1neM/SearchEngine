import models.Field;
import models.Lemma;
import models.Page;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.Map;

public class DBModel {
    private Map<String, Integer> bodyMap = new HashMap<>();
    private Map<String, Integer> titleMap = new HashMap<>();
    private static String SITE_URL;
    private static Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();

    public DBModel(String SITE_URL) {
        DBModel.SITE_URL = SITE_URL;
        saveField();
    }

    public void fillDataBase(Link rootUrl) {
        for (Link child : rootUrl.getChildren()) {
            Transaction transaction = session.beginTransaction();
            session.save(savePage(child));
            if (transaction.isActive()) {
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

    private static void saveField(){
        Field title = new Field("title", "title", 1);
        Field body = new Field("body", "body", (float)0.8);
        Transaction transaction = session.beginTransaction();
        session.save(title);
        session.save(body);
        transaction.commit();
    }

    public void saveLemmas(Link rootUrl){
        Transaction transaction = session.beginTransaction();
        for (Link child : rootUrl.getChildren()) {
            child.getTitleMap().forEach((s, integer) -> {
                    if (titleMap.containsKey(s)) {
                        titleMap.replace(s, titleMap.get(s) + 1);
                    } else {
                        titleMap.put(s, 1);
                    }
            });
            child.getBodyMap().forEach((s, integer) -> {
                    if (bodyMap.containsKey(s)) {
                        bodyMap.replace(s, bodyMap.get(s) + 1);
                    } else {
                        bodyMap.put(s, 1);
                    }

            });
        }
        titleMap.forEach((s, integer) -> session.save(new Lemma(s,integer)));
        bodyMap.forEach((s, integer) -> session.save(new Lemma(s,integer)));

        if (transaction.isActive()) {
            transaction.commit();
        }
    }
}
