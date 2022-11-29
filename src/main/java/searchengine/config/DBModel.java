package searchengine.config;

import searchengine.models.Field;
import searchengine.models.Index;
import searchengine.models.Page;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.util.*;

public class DBModel {
    private final static Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
    public DBModel() {
    }

    public static void saveFields(){
        Field title = new Field("title", "title", 1);
        Field body = new Field("body", "body", (float)0.8);
        Transaction transaction = session.beginTransaction();
        session.save(title);
        session.save(body);
        transaction.commit();
    }
    public synchronized static void savePage(Page page){
        Transaction transaction = session.beginTransaction();
        session.save(page);
        if (transaction.isActive()) {
            transaction.commit();
        }
    }
    public synchronized static void saveIndices(List<Index> indices) throws IOException{
        Transaction transaction = session.beginTransaction();
        indices.forEach(session::saveOrUpdate);
        if (transaction.isActive()) {
            transaction.commit();
        }
    }
}
