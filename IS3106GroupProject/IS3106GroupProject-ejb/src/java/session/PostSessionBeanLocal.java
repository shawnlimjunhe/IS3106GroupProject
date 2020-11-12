/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Application;
import entity.Post;
import error.NoResultException;
import java.util.List;
import javax.ejb.Local;

@Local
public interface PostSessionBeanLocal {

    public Long createPost(Post p);

    public void updatePost(Post p) throws NoResultException;

    public void deletePost(Long pId) throws NoResultException;

    public List<Post> searchPostsByCompany(Long cId) throws NoResultException;

    public Post getPost(Long pId) throws NoResultException;

    public void addCompanyToPost(Long pId, Long cId);

    public List<Post> searchPostsFromCompany(String queryTerm, Long cId);

    public List<Post> getAllPosts(String name) throws NoResultException;

    public void addApplication(Post p, Application a) throws NoResultException;
}
