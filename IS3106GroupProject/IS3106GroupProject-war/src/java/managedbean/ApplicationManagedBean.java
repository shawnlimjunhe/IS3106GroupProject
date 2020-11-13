/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Application;
import entity.Influencer;
import entity.Post;
import error.NoResultException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private List<Post> posts;

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
            a.setAccepted("processing"); //INITIAL VALUE SET TO PROCESSING, CHANGE TO 'accepted' or 'rejected' when application gets processed
            applicationSessionBeanLocal.createApplication(a);
            influencerSessionBeanLocal.addApplication(i, a);
            postSessionBeanLocal.addApplication(selectedPost, a);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Post successfully created!", ""));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to apply for job right now!", ""));
            return "/influencerSecret/jobFeed.xhtml" + "&faces-redirect=true";
        }

        return "/influencerSecret/viewInfluencerJobs.xhtml?iId=" + influencerAuthenticationManagedBean.getInfluencerId() + "&faces-redirect=true";
    }

    public void loadSelectedPost() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (postId != null) {
            try {
                selectedPost = postSessionBeanLocal.getPost(postId);
                companyName = selectedPost.getCompany().getName();
            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load post"));
            }
        }
    }

    public void loadAllPosts() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            posts = postSessionBeanLocal.getAllPosts(null);
        } catch (NoResultException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Unable to load jobs", ""));
        }
    }

    public List<Application> getApplicationsWithStatus(String status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            List<Application> applications = applicationSessionBeanLocal.getApplicationsWithStatus(influencerAuthenticationManagedBean.getInfluencerId(), status);
            return applications;
        } catch (NoResultException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Unable to load applications", ""));
            return null;
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

    public Post getSelectedPost() {
        return selectedPost;
    }

    public void setSelectedPost(Post selectedPost) {
        this.selectedPost = selectedPost;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

}