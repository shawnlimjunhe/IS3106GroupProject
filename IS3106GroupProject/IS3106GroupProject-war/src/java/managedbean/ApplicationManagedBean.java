/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Application;
import entity.Influencer;
import entity.Post;
import java.io.Serializable;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import session.ApplicationSessionBean;
import session.ApplicationSessionBeanLocal;
import session.InfluencerSessionBeanLocal;
import session.PostSessionBeanLocal;

/**
 *
 * @author laurentiakky
 */
@Named(value = "applicationManagedBean")
@ViewScoped
public class ApplicationManagedBean implements Serializable {

    @EJB
    private ApplicationSessionBeanLocal applicationSessionBeanLocal;

    @Inject
    private InfluencerAuthenticationManagedBean influencerAuthenticationManagedBean;

    @EJB
    private InfluencerSessionBeanLocal influencerSessionBeanLocal;
    
    @EJB
    private PostSessionBeanLocal postSessionBeanLocal;

    private String caption;

    private String companyName;
    
    private Long postId;
    
    private Post selectedPost;

    public ApplicationManagedBean() {
    }

    public String createApplication() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Application a = new Application();
        try {
            Influencer i = influencerSessionBeanLocal.getInfluencer(influencerAuthenticationManagedBean.getInfluencerId());
            a.setInfluencerRank(i.getRanking());
            a.setCaption(caption);
            a.setCompanyName(companyName);
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to apply for job right now!", ""));
            return "/influencerSecret/jobFeed.xhtml" + "&faces-redirect=true";
        }
        applicationSessionBeanLocal.createApplication(a);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Post successfully created!", ""));

        return "/influencerSecret/viewInfluencerJobs.xhtml?iId=" + influencerAuthenticationManagedBean.getInfluencerId() + "&faces-redirect=true";
    }
    
    public void loadSelectedPost() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (postId != null) {
            try {
                selectedPost = postSessionBeanLocal.getPost(postId);
                companyName = selectedPost.getCompany().getName();
            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load post"));
            }
        }

    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    

}
