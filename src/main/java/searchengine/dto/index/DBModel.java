package searchengine.dto.index;

import org.hibernate.SessionFactory;
import searchengine.models.*;
import org.hibernate.Session;
import org.hibernate.Transaction;


import java.io.IOException;
import java.util.*;

public class DBModel {
    private final static SessionFactory sessionFactory = HibernateSessionFactoryUtils.getSessionFactory();
    public DBModel() {
    }

    public static void saveFields(){
        Session session = sessionFactory.openSession();
        Field title = new Field("title", "title", 1);
        Field body = new Field("body", "body", (float)0.8);
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(title);
        session.saveOrUpdate( body);
        transaction.commit();
        session.close();
    }
    public synchronized static void savePage(Page page){
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(page);
        if (transaction.isActive()) {
            transaction.commit();
        }
        session.close();
    }
    public synchronized static void saveIndices(List<Index> indices) throws IOException{
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        indices.forEach(index -> {
            try{
                updateFrequency(index.getLemmaId());
                session.saveOrUpdate(index);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        });
        if (transaction.isActive()) {
            transaction.commit();
        }
        session.close();
    }

    public synchronized static void saveSite(SiteEntity siteEntity) throws IOException {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(siteEntity);
        if (transaction.isActive()) {
            transaction.commit();
        }
        session.close();
    }

    public synchronized static boolean updateFrequency(Lemma lemma) throws IOException{
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        String hql = "From Lemma where lemma = " + lemma.getLemma();
        Lemma DBLemma = (Lemma) session.createQuery(hql, Lemma.class);
        if (DBLemma.getLemma().isEmpty()){
           return false;
        }
            DBLemma.setFrequency(+1);
            session.save(DBLemma);
            transaction.commit();
            session.close();
            return true;
    }
}
