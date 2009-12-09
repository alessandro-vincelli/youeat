package it.av.eatt.todb;

import it.av.eatt.JackWicketException;
import it.av.eatt.ocm.model.Ristorante;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IndexDB {

    public void performIndex() throws JackWicketException {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        EntityManagerFactory entityManagerFactory = (EntityManagerFactory) context.getBean("entityManagerFactory");
        EntityManager em = entityManagerFactory.createEntityManager();
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        em.getTransaction().begin();
        
        Collection<Ristorante> ristos= em.createQuery("select risto from Ristorante as risto").getResultList();
        for (Ristorante risto : ristos) {
            fullTextEntityManager.index(risto);
        }
       
//        List<City> cities = em.createQuery("select city from City as city").getResultList();
//        for (City city : cities) {
//            fullTextEntityManager.index(city);
//        }
        em.getTransaction().commit();
        em.close();
    }
    public static void main(String[] main) throws JackWicketException{
        IndexDB indexDB = new IndexDB();
        indexDB.performIndex();
    }
}
