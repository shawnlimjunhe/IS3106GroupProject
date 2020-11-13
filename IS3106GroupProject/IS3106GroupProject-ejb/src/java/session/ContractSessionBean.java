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
    public void createContract(Contract c) {
        em.persist(c);
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
    
    @Override
    public void updateContract(Contract c) throws NoResultException {
        Contract oldC = em.find(Contract.class, c.getId());
        System.out.print("updating contract");
        
        if (oldC != null) {
            System.out.print("found contract");
            oldC.setApproved(c.isApproved());
            oldC.setCompanyId(c.getCompanyId());
            oldC.setInfluencerId(c.getInfluencerId());
            oldC.setLink(c.getLink());
            System.out.print("updated contract successfully");
            //whats there to change for company
        } else {
            throw new NoResultException("Not found");
        }
    }
    
    @Override
    public void setContractCompanyandInfluencer(Long contractId, Long cId, Long iId) {
        Contract c = em.find(Contract.class, contractId);
        c.setCompanyId(cId);
        c.setInfluencerId(iId);
        System.out.print("I love JPA");
        em.flush();
    }
}
