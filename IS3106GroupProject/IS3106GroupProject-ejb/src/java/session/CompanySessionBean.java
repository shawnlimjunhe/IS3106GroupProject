/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Company;
import entity.Contract;
import entity.Post;
import error.NoResultException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class CompanySessionBean implements CompanySessionBeanLocal {
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public void createCompany(Company c) {
        em.persist(c);
    }
    
    @Override
    public Company login(String companyUsername, String password) throws NoResultException {
        Query query = em.createQuery("SELECT c FROM Company c WHERE c.username = :username");
        query.setParameter("username", companyUsername);
        Company c = null;
        try {
            c = (Company) query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
        
        if (c != null) {
            if (c.getPassword().equals(password)) {
                return c;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    @Override
    public List<Company> searchCompanies(String name) {
        Query q;
        if (name != null) {
            q = em.createQuery("SELECT c FROM Company c WHERE "
                    + "LOWER(c.name) LIKE :name");
            q.setParameter("name", "%" + name.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT c FROM Company c");
        }
        return q.getResultList();
    }
    
    @Override
    public void updateCompany(Company c) throws NoResultException {
        Company oldC = em.find(Company.class, c.getId());
        
        if (oldC != null) {
            oldC.setName(c.getName());
            oldC.setPassword(c.getPassword());
            oldC.setPosts(c.getPosts());
            oldC.setContracts(c.getContracts());
            //whats there to change for company
        } else {
            throw new NoResultException("Not found");
        }
    }
    
    @Override
    public void deleteCompany(Long cId) throws NoResultException {
        Company comp = em.find(Company.class, cId);
        
        if (comp == null) {
            throw new NoResultException("Not found");
        }
        
        List<Post> posts = comp.getPosts();
        comp.setPosts(null);
        List<Contract> contracts = comp.getContracts(); //need to add check whether contracts have been paid/fulfilled
        comp.setContracts(null);
        
        for (Post p : posts) { //might throw a concurrentmodificationexception, change to for int i loop if that happens
            em.remove(p);
        }
        
        for (Contract cont : contracts) {
            //need to remove contract from influencer also? maybe use contractSessionBeanLocal to remove
            em.remove(cont);
        }
        
        em.remove(comp);
    }
    
    @Override
    public Company getCompany(Long cId) throws NoResultException {
        Company company = em.find(Company.class, cId);
        
        if (company != null) {
            return company;
        } else {
            throw new NoResultException("Not found");
        }
    }
    
    @Override
    public boolean checkDuplicate(String username) {
        Query q;
        if (username != null) {
            q = em.createQuery("SELECT c FROM Company c WHERE "
                    + "LOWER(c.username) LIKE :username");
            q.setParameter("username", "%" + username.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT c FROM Company c");
        }
        if (q.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }
    
    @Override
    public void addContract(Long compId, Long contractId) throws NoResultException {
        Company c = em.find(Company.class, compId);
        Contract con = em.find(Contract.class, contractId);
        
        if (c != null && con != null) {
            c.getContracts().add(con);
        } else {
            throw new NoResultException("Not found");
        }
    }
    
    @Override
    public void debitInfluencer(Long cId, double salary) throws NoResultException {
        Company c = getCompany(cId);
        c.setBalance(c.getBalance() - salary);
    }
    
    @Override
    public void updateProfile(Long cId, String name) throws NoResultException {
        Company c = getCompany(cId);
        c.setName(name);
    }
    
    public void topup(Long companyId, double topup) throws NoResultException {
        Company c = getCompany(companyId);
        c.setBalance(c.getBalance() + topup);
    }
}
