/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Influencer;
import error.NoResultException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import session.InfluencerSessionBeanLocal;

/**
 *
 * @author laurentiakky
 */
@Named(value = "influencerAuthenticationManagedBean")
@SessionScoped
public class InfluencerAuthenticationManagedBean implements Serializable {

    private String username = null;
    private String password = null;
    private Long influencerId = new Long(-1);
    
    private Influencer influencer;
    
    @EJB
    private InfluencerSessionBeanLocal influencerSessionBeanLocal;
    
    public InfluencerAuthenticationManagedBean() {
    }
    
    public String login() throws NoResultException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        influencer = influencerSessionBeanLocal.login(username, password);
        
        if (influencer == null) {
            //login failed
            username = null;
            password = null;
            influencerId = new Long(-1);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong login information!", ""));
            return "influencerLogin.xhtml?faces-redirect=true";
        } else {
            influencerId = influencer.getId();
            return "/influencerSecret/influencerLandingPage.xhtml?faces-redirect=true";
        }
    }
    
     public String logout() {
        username = null;
        password = null;
        influencerId = new Long(-1);

        return "/influencerLogin.xhtml?faces-redirect=true";
    } //end logout

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

    public Long getInfluencerId() {
        return influencerId;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public Influencer getInfluencer() {
        return influencer;
    }

    public void setInfluencer(Influencer influencer) {
        this.influencer = influencer;
    }
    
}
