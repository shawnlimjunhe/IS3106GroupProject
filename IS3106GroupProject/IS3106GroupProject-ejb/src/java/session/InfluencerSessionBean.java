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

@Stateless
public class InfluencerSessionBean implements InfluencerSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void createInfluencer(Influencer i) {
        em.persist(i);
    }

    @Override
    public Influencer getInfluencer(Long iId) throws NoResultException {
        Influencer influencer = em.find(Influencer.class, iId);
        
        if(influencer != null) {
            return influencer;
        } else {
            throw new NoResultException("Not found");
        }
    }
    
    @Override
    public List<Influencer> searchInfluencers(String name) {
         Query q;
        if (name != null) {
            q = em.createQuery("SELECT i FROM Influencer i WHERE "
                    + "LOWER(i.username) LIKE :name");
            q.setParameter("name", "%" + name.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT i FROM Influencer i");
        }

        return q.getResultList();
    }

    @Override
    public void updateInfluencer(Influencer i) throws NoResultException {
        Influencer oldI = em.find(Influencer.class, i.getId());
        
        if(oldI != null) {
            oldI.setUsername(i.getUsername());
            oldI.setPassword(i.getPassword());
        } else {
            throw new NoResultException("Not found");
        }
    }

    @Override
    public void deleteInfluencer(Long iId) throws NoResultException {
        Influencer i = em.find(Influencer.class, iId);
        
        if(i == null) {
            throw new NoResultException("Not found");
        }
        
        List<Application> applications = i.getApplications();
        i.setApplications(null);
        for (Application a : applications) {
            em.remove(a);
        }
        em.remove(i);
    }

    
    
}
