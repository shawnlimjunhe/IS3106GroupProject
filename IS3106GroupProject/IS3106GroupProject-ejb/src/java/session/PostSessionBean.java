/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Application;
import entity.Company;
import entity.Influencer;
import entity.Post;
import error.NoResultException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

@Stateless
public class PostSessionBean implements PostSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Long createPost(Post p) {
        em.persist(p);
        em.flush();

        return p.getId();
    }

    @Override
    public Post getPost(Long pId) throws NoResultException {
        Post post = em.find(Post.class, pId);

        if (post != null) {
            return post;
        } else {
            throw new NoResultException("Not found");
        }
    }

    @Override
    public void addCompanyToPost(Long pId, Long cId) {
        Post post = em.find(Post.class, pId);
        Company company = em.find(Company.class, cId);

        post.setCompany(company);
        company.addPost(post);
        em.flush();
    }

    @Override
    public void updatePost(Post p) throws NoResultException {
        Post oldP = em.find(Post.class, p.getId());

        if (oldP != null) {
            oldP.setDeadline(p.getDeadline());
            oldP.setDescription(p.getDescription());
            oldP.setMinFollowers(p.getMinFollowers());
            oldP.setSalary(p.getSalary());
            oldP.setCompany(p.getCompany());
        }
    }

    @Override
    public void deletePost(Long pId) throws NoResultException {
        Post p = em.find(Post.class, pId);
        if (p != null) {
            Query q = em.createQuery("SELECT c FROM Company c WHERE :post MEMBER OF c.posts");
            q.setParameter("post", p);

            for (Object company : q.getResultList()) {
                Company c = (Company) company;
                c.getPosts().remove(p);
            }
            em.remove(p);
        } else {
            throw new NoResultException("Not found");
        }
    }

    @Override
    public List<Post> searchPostsByCompany(Long cId) throws NoResultException {
        Company c = em.find(Company.class, cId);
        Query q;
        if (c != null) {
            q = em.createQuery("SELECT p FROM Post p, Company c WHERE p MEMBER OF c.posts");
        } else {
            return null;
        }

        return q.getResultList();
    }

    public List<Post> searchPostsFromCompany(String queryTerm, Long cId) {
        Company c = em.find(Company.class, cId);
        Query q;
        if (queryTerm != null) {
            q = em.createQuery("SELECT p FROM Post p, Company c WHERE p MEMBER OF c.posts AND LOWER(p.title) LIKE :name");
            q.setParameter("name", "%" + queryTerm.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT p FROM Post p, Company c WHERE p MEMBER OF c.posts");
        }

        return q.getResultList();
    }

    @Override
    public List<Post> getAllPosts(String name) throws NoResultException {
        Query q;
        if (name != null) {
            q = em.createQuery("SELECT p FROM Post p WHERE "
                    + "LOWER(p.title) LIKE :name");
            q.setParameter("name", "%" + name.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT p FROM Post p");
        }

        return q.getResultList();
    }

    @Override
    public void addApplication(Post p, Application a) throws NoResultException {
        Post post = em.find(Post.class, p.getId());
        Application app = em.find(Application.class, a.getId());

        if (post != null && app != null) {
            post.getApplications().add(a);
        } else {
            throw new NoResultException("Not found");
        }
    }
}
