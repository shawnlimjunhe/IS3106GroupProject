/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Influencer;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import session.InfluencerSessionBeanLocal;

/**
 *
 * @author laurentiakky
 */
@Named(value = "influencerManagedBean")
@ViewScoped
public class InfluencerManagedBean implements Serializable {

    @EJB
    private InfluencerSessionBeanLocal influencerSessionBeanLocal;
    
    private String username;
    
    private String password;
    
    private int followers;
    
    private Influencer selectedInfluencer;
    
    private Long iId;
    
    private double balance;
    
    private int ranking;

    public InfluencerManagedBean() {
    }

    public String createInfluencer() throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        System.out.println(followers);
        try {
            Influencer i = new Influencer();
            i.setUsername(username);
            i.setPassword(password);
            i.setNumberFollowers(followers);
            
            influencerSessionBeanLocal.createInfluencer(i);
            return "influencerLogin.xhtml?faces-redirect=true";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to create influencer account!"));
            return "index.xhtml?faces-redirect=true";
        }
    }
    
    public void loadSelectedInfluencer() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            selectedInfluencer = influencerSessionBeanLocal.getInfluencer(iId);
            username = selectedInfluencer.getUsername();
            balance = selectedInfluencer.getBalance();
            followers = selectedInfluencer.getNumberFollowers();
            ranking = selectedInfluencer.getRanking();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load influencer"));
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }


    public Influencer getSelectedInfluencer() {
        return selectedInfluencer;
    }

    public void setSelectedInfluencer(Influencer selectedInfluencer) {
        this.selectedInfluencer = selectedInfluencer;
    }

    public Long getiId() {
        return iId;
    }

    public void setiId(Long iId) {
        this.iId = iId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
    
    
}