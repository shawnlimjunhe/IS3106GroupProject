/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Application;
import entity.Influencer;
import error.NoResultException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Cze_J
 */
@Stateless
public class ApplicationSessionBean implements ApplicationSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void createApplication(Application a) {
        em.persist(a);
    }

    @Override
    public void updateApplication(Application a) throws NoResultException {
        Application oldA = em.find(Application.class, a.getId());
        if (oldA != null) {
            oldA.setCaption(a.getCaption());
        } else {
            throw new NoResultException("Not found");
        }
    }

    @Override
    public void deleteApplication(Long aId) throws NoResultException {
        Application a = em.find(Application.class, aId);
        if (a != null) {
            Query q = em.createQuery("SELECT i FROM Influencer i WHERE :application MEMBER OF i.applications");
            q.setParameter("application", a);

            for (Object influencer : q.getResultList()) {
                Influencer i = (Influencer) influencer;
                i.getApplications().remove(a);
            }
            em.remove(a);
        } else {
            throw new NoResultException("Not found");
        }
    }

    @Override
    public List<Application> searchApplicationsByInfluencer(Long iId) throws NoResultException {
        Influencer i = em.find(Influencer.class, iId);
        Query q;
        if (i != null) {
            q = em.createQuery("SELECT a FROM Application a, Influencer i WHERE a MEMBER OF i.applications");
        } else {
            return null;
        }

        return q.getResultList();
    }

    @Override
    public Application getApplication(Long aId) throws NoResultException {
        Application app = em.find(Application.class, aId);

        if (app != null) {
            return app;
        } else {
            throw new NoResultException("Not found");
        }
    }
}
