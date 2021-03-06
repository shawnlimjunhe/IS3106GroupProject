package session;

import entity.Application;
import entity.Contract;
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
    public Influencer login(String username, String password) throws NoResultException {
        Query query = em.createQuery("SELECT i FROM Influencer i WHERE i.username = :username");
        query.setParameter("username", username);
        Influencer i = null;
        try {
            i = (Influencer) query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }

        if (i != null) {
            if (i.getPassword().equals(password)) {
                return i;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Influencer getInfluencer(Long iId) throws NoResultException {
        Influencer influencer = em.find(Influencer.class, iId);

        if (influencer != null) {
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

        if (oldI != null) {
            oldI.setUsername(i.getUsername());
            oldI.setNumberFollowers(i.getNumberFollowers());
            oldI.setFileName(i.getFileName());
        } else {
            throw new NoResultException("Not found");
        }
    }

    @Override
    public void changePassword(Influencer i) throws NoResultException {
        Influencer oldI = em.find(Influencer.class, i.getId());

        if (oldI != null) {
            oldI.setPassword(i.getPassword());
        } else {
            throw new NoResultException("Not found");
        }
    }

    @Override
    public void deleteInfluencer(Long iId) throws NoResultException {
        Influencer i = em.find(Influencer.class, iId);

        if (i == null) {
            throw new NoResultException("Not found");
        }

        List<Application> applications = i.getApplications();
        i.setApplications(null);
        for (Application a : applications) {
            em.remove(a);
        }

        List<Contract> contracts = i.getContracts();
        i.setContracts(null);
        for (Contract c : contracts) {
            em.remove(c);
        }
        em.remove(i);
    }

    @Override
    public void updateBalance(Influencer i) throws NoResultException {
        Influencer oldI = em.find(Influencer.class, i.getId());

        if (oldI != null) {
            oldI.setBalance(i.getBalance());
        } else {
            throw new NoResultException("Not found");
        }
    }

    @Override
    public void addApplication(Influencer i, Application a) throws NoResultException {
        Influencer inf = em.find(Influencer.class, i.getId());
        Application app = em.find(Application.class, a.getId());

        if (inf != null && app != null) {
            inf.getApplications().add(app);
        } else {
            throw new NoResultException("Not found");
        }
    }

    @Override
    public void addContract(Long iId, Long contractId) throws NoResultException {
        Influencer i = em.find(Influencer.class, iId);
        Contract con = em.find(Contract.class, contractId);

        if (i != null && con != null) {
            i.getContracts().add(con);
        } else {
            throw new NoResultException("Not found");
        }

    }

    @Override
    public void acceptContract(Long iId) throws NoResultException {
        Influencer i = getInfluencer(iId);
        i.setRanking(i.getRanking() + 5);
    }

    @Override
    public void creditInfluencer(Long iId, double salary) throws NoResultException {
        Influencer i = getInfluencer(iId);
        i.setBalance(i.getBalance() + salary);
    }

}
