package searchengine.config;

import searchengine.models.Field;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;


public class DBModel {
    private final static Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
    private final static HashMap<String, Lemma> lemmas = new HashMap<>();
    public DBModel() {
//        saveFields();
    }

    public synchronized void saveAll(Page page, List<Lemma> lemmaList, List<Index> indices){
        Transaction transaction = session.beginTransaction();
        savePage(page);
        saveLemmas(lemmaList);
        saveIndices(indices);
        if (transaction.isActive()){
            transaction.commit();
        }
    }

    public synchronized static void savePage(Page page ){
        Transaction transaction = session.beginTransaction();
            session.save(page);
            if (transaction.isActive()){
                transaction.commit();
            }
    }

    public void saveFields(){
        Field title = new Field("title", "title", 1);
        Field body = new Field("body", "body", (float)0.8);
        Transaction transaction = session.beginTransaction();
        session.save(title);
        session.save(body);
        transaction.commit();
    }

    public synchronized static void saveLemmas(List<Lemma> lemmaList){
        Transaction transaction = session.beginTransaction();
        lemmaList.forEach(session::save);
        transaction.commit();
    }

    public synchronized static void saveIndices(List<Index> indices){
        Transaction transaction = session.beginTransaction();
        indices.forEach(session::persist);
        if (transaction.isActive()) {
            transaction.commit();
        }
    }
}
