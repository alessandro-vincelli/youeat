package it.av.youeat.service.todb;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Ristorante;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IndexDB {

    public void performIndex() throws YoueatException {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        EntityManagerFactory entityManagerFactory = (EntityManagerFactory) context.getBean("entityManagerFactory");
        EntityManager em = entityManagerFactory.createEntityManager();
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        em.getTransaction().begin();
        
        Collection<Ristorante> ristos= em.createQuery("select risto from Ristorante as risto").getResultList();
        for (Ristorante risto : ristos) {
            fullTextEntityManager.index(risto);
        }
        
        Collection<Eater> eaters = em.createQuery("select eater from Eater as eater").getResultList();
        for (Eater eater : eaters) {
            fullTextEntityManager.index(eater);
        }
       
//        List<City> cities = em.createQuery("select city from City as city").getResultList();
//        for (City city : cities) {
//            fullTextEntityManager.index(city);
//        }
        em.getTransaction().commit();
        em.close();
    }
    public static void main(String[] main) throws YoueatException{
        IndexDB indexDB = new IndexDB();
        indexDB.performIndex();
    }
}
