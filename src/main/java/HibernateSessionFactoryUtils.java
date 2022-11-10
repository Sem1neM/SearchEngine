import models.Lemma;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateSessionFactoryUtils {

    private static SessionFactory sessionFactory;

    private HibernateSessionFactoryUtils() {
    }

    public static SessionFactory getSessionFactory(){
        if (sessionFactory == null){
            try {
                StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                        .configure("hibernate.cfg.xml").build();
                Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            }

            catch (Exception e){
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
