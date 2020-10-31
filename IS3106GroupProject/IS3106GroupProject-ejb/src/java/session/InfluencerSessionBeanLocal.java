/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Influencer;
import error.NoResultException;
import java.util.List;
import javax.ejb.Local;

@Local
public interface InfluencerSessionBeanLocal {
    public void createInfluencer(Influencer i);
    
    public Influencer getInfluencer(Long iId) throws NoResultException;

    public List<Influencer> searchInfluencers(String name);

    public void updateInfluencer(Influencer i) throws NoResultException;

    public void deleteInfluencer(Long iId) throws NoResultException;
}
