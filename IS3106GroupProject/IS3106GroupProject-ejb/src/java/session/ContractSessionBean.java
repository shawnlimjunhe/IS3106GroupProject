/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Company;
import entity.Contract;
import entity.Influencer;
import error.NoResultException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ContractSessionBean implements ContractSessionBeanLocal {
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public void createContract(Contract c, Long companyId, Long influencerId) {
        em.persist(c);
        Company comp = em.find(Company.class, companyId);
        Influencer influ = em.find(Influencer.class, influencerId);
        if (comp != null && influ != null) {
            c.setCompany(comp);
            c.setInfluencer(influ);
            comp.getContracts().add(c);
            influ.getContracts().add(c);
            System.out.print("set associations");
        }
    }
    
    @Override
    public void deleteContract(Long cId) throws NoResultException {
        Contract c = em.find(Contract.class, cId);
        if (c != null) {
            Query q = em.createQuery("SELECT i FROM Influencer i WHERE :contract MEMBER OF i.contracts");
            q.setParameter("contract", c);
            
            for (Object influencer : q.getResultList()) {
                Influencer i = (Influencer) influencer;
                i.getContracts().remove(c);
            }
            
            Query q1 = em.createQuery("SELECT c FROM Company c WHERE :contract MEMBER OF c.contracts");
            q1.setParameter("contract", c);
            
            for (Object company : q.getResultList()) {
                Company com = (Company) company;
                com.getContracts().remove(c);
            }
            
            em.remove(c);
        } else {
            throw new NoResultException("Not found");
        }
    }
    
    @Override
    public List<Contract> searchContractsByInfluencer(Long iId) throws NoResultException {
        Influencer i = em.find(Influencer.class, iId);
        Query q;
        if (i != null) {
            q = em.createQuery("SELECT c FROM Contract c, Influencer i WHERE c MEMBER OF i.contracts");
        } else {
            return null;
        }
        
        return q.getResultList();
    }
    
    @Override
    public List<Contract> searchContractsByCompany(Long cId) throws NoResultException {
        Company c = em.find(Company.class, cId);
        Query q;
        if (c != null) {
            q = em.createQuery("SELECT c FROM Contract c, Company com WHERE c MEMBER OF com.contracts");
        } else {
            return null;
        }
        
        return q.getResultList();
    }
    
    @Override
    public List<Contract> getInfPastContracts(Long iId) throws NoResultException {
        Influencer inf = em.find(Influencer.class, iId);
        
        if (inf != null) {
            Query q = em.createQuery("SELECT c FROM Contract c, Influencer i WHERE c MEMBER OF i.contracts AND c.endDate < current_date");
            
            return q.getResultList();
        } else {
            throw new NoResultException("Not found");
        }
    }
    
    @Override
    public List<Contract> getInfOngoingContracts(Long iId) throws NoResultException {
        Influencer inf = em.find(Influencer.class, iId);
        
        if (inf != null) {
            Query q = em.createQuery("SELECT c FROM Contract c, Influencer i WHERE c MEMBER OF i.contracts AND c.endDate >= current_date");
            
            return q.getResultList();
        } else {
            throw new NoResultException("Not found");
        }
    }
}
