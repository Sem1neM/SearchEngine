import models.Field;
import models.Lemma;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class DBModel {
    private final LinkRecursiveTask linkRecursiveTask;
    private final  static Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();

    public DBModel(LinkRecursiveTask linkRecursiveTask) {
        this.linkRecursiveTask = linkRecursiveTask;
        saveField();
    }


    public void savePage(Link rootUrl){
        Transaction transaction = session.beginTransaction();
        for (Link child : rootUrl.getChildren()){
            session.save(child.getPage());
        }
        transaction.commit();
    }

    private static void saveField(){
        Field title = new Field("title", "title", 1);
        Field body = new Field("body", "body", (float)0.8);
        Transaction transaction = session.beginTransaction();
        session.save(title);
        session.save(body);
        transaction.commit();
    }

    public void saveLemmas(){
        Transaction transaction = session.beginTransaction();
        List<Lemma> lemmas = linkRecursiveTask.getLemmas();
                lemmas.forEach(session::save);
        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    public void saveIndex(){
        Transaction transaction = session.beginTransaction();
        linkRecursiveTask.getIndices().forEach(session::save);
        transaction.commit();
    }
}
