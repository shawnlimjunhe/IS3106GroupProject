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
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
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

    private boolean userApplied;

    private Influencer influencer;

    private Long applicationId;

    private Application selectedApplication;

    private boolean accepted;

    private boolean processing;

    private boolean rejected;

    public ApplicationManagedBean() {
    }

    public String createApplication() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Application a = new Application();
        try {
            Influencer i = influencerSessionBeanLocal.getInfluencer(influencerAuthenticationManagedBean.getInfluencerId());
            Post p = postSessionBeanLocal.getPost(postId);
            if (p.getMinFollowers() > i.getNumberFollowers()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sorry you couldn't apply for that job because you do not meet the minimum number of followers requirement.", ""));
                return "/influencerSecret/viewJob.xhtml?pId=" + postId +"&faces-redirect=true";
            }
            a.setInfluencerRank(i.getRanking());
            a.setInfluencerId(influencerAuthenticationManagedBean.getInfluencerId());
            a.setCaption(caption);
            System.out.print(caption);
            a.setCompanyName(companyName);
            a.setInfluencerUsername(i.getUsername());
            a.setPostId(postId);
            a.setAccepted("processing"); //INITIAL VALUE SET TO PROCESSING, CHANGE TO 'accepted' or 'rejected' when application gets processed
            applicationSessionBeanLocal.createApplication(a);
            influencerSessionBeanLocal.addApplication(i, a);
            postSessionBeanLocal.addApplication(selectedPost, a);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Successfully applied for job!", ""));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to apply for job right now!", ""));
            return "/influencerSecret/viewJob.xhtml?pId=" + postId +"&faces-redirect=true";
        }

        return "/influencerSecret/viewJob.xhtml?pId=" + postId +"&faces-redirect=true";
    }

    public void loadSelectedPost() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (postId != null) {
            try {
                selectedPost = postSessionBeanLocal.getPost(postId);
                companyName = selectedPost.getCompany().getName();
                influencer = influencerSessionBeanLocal.getInfluencer(influencerAuthenticationManagedBean.getInfluencerId());
                List<Application> apps = influencer.getApplications();
                for (Application a : apps) {
                    if (a.getPostId().equals(postId)) {
                        userApplied = true;
                    }
                }
            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Unable to load job", ""));
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

    public void loadSelectedApplication() {
        System.out.print(applicationId);
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (applicationId != null) {
            try {
                selectedApplication = applicationSessionBeanLocal.getApplication(applicationId);
                if (selectedApplication.getAccepted().equals("processing")) {
                    processing = true;
                } else if (selectedApplication.getAccepted().equals("accepted")) {
                    accepted = true;
                } else {
                    rejected = true;
                }

            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Unable to load application", ""));
            }
        }
    }

    public String deleteApplication() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            applicationSessionBeanLocal.deleteApplication(applicationId);
            context.addMessage(null, new FacesMessage("Successfully deleted application!", ""));
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Unable to delete application", ""));
        }
        return "/influencerSecret/viewInfluencerJobs.xhtml?iId=" + influencerAuthenticationManagedBean.getInfluencerId() + "&faces-redirect=true";
    } //end delete

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

    public boolean isUserApplied() {
        return userApplied;
    }

    public void setUserApplied(boolean userApplied) {
        this.userApplied = userApplied;
    }

    public Influencer getInfluencer() {
        return influencer;
    }

    public void setInfluencer(Influencer influencer) {
        this.influencer = influencer;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Application getSelectedApplication() {
        return selectedApplication;
    }

    public void setSelectedApplication(Application selectedApplication) {
        this.selectedApplication = selectedApplication;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

}
