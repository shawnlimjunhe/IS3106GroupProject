/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Post;
import error.NoResultException;
import java.util.List;
import javax.ejb.Local;

@Local
public interface PostSessionBeanLocal {

    public void createPost(Post p);

    public void updatePost(Post p) throws NoResultException;

    public void deletePost(Long pId) throws NoResultException;

    public List<Post> searchPostsByCompany(Long cId) throws NoResultException;

    public Post getPost(Long pId) throws NoResultException;
}
