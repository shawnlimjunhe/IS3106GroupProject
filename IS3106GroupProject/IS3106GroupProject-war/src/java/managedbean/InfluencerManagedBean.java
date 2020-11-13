/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Application;
import entity.Influencer;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
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

    @Inject
    private InfluencerAuthenticationManagedBean influencerAuthenticationManagedBean;

    private String username;

    private String password;

    private String oldPassword;

    private String newPassword;

    private int followers;

    private Influencer selectedInfluencer;

    private Long iid;

    private double balance;

    private int ranking;

    private List<Influencer> influencers;

    private String searchString;

    private double withdrawAmount;

    private Date today = new Date();

    private Date deadline;

    private int difference;
    
    private Part uploadedfile;

    private String filename = "";

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
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Account is successfully created!", ""));
            return "influencerLogin.xhtml?faces-redirect=true";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Username is taken!", ""));
            return "registerInfluencer.xhtml?faces-redirect=true";
        }
    }

    public void loadSelectedInfluencer() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            selectedInfluencer = influencerSessionBeanLocal.getInfluencer(iid);
            username = selectedInfluencer.getUsername();
            balance = selectedInfluencer.getBalance();
            followers = selectedInfluencer.getNumberFollowers();
            ranking = selectedInfluencer.getRanking();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load influencer"));
        }
    }

    public String updateInfluencer() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        selectedInfluencer.setUsername(username);
        selectedInfluencer.setNumberFollowers(followers);
        try {
            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

            //get the deployment path
            String UPLOAD_DIRECTORY = ctx.getRealPath("/") + "upload/";
            System.out.println("#UPLOAD_DIRECTORY : " + UPLOAD_DIRECTORY);

            //debug purposes
            setFilename(Paths.get(uploadedfile.getSubmittedFileName()).getFileName().toString());
            System.out.println("filename: " + getFilename());
            selectedInfluencer.setFileName(getFilename());
            //---------------------

            //replace existing file
            java.nio.file.Path path = Paths.get(UPLOAD_DIRECTORY + getFilename());
            InputStream bytes = uploadedfile.getInputStream();
            Files.copy(bytes, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {}
        try {
            influencerSessionBeanLocal.updateInfluencer(selectedInfluencer);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Unable to update profile", ""));
        }

        context.addMessage(null, new FacesMessage("Successfully updated profile!", ""));
        return "/influencerSecret/viewInfluencerProfile.xhtml?iId=" + iid + "&faces-redirect=true";
    }

    public String changePassword() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (oldPassword.equals(selectedInfluencer.getPassword())) {
            selectedInfluencer.setPassword(newPassword);
            try {
                influencerSessionBeanLocal.changePassword(selectedInfluencer);
                context.addMessage(null, new FacesMessage("Password is changed successfully!", ""));
            } catch (Exception e) {
                //show with an error icon
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Unable to change password", ""));
            }
        } else {
            context.addMessage(null, new FacesMessage("Old password is wrong, please try again!", ""));
            return "/influencerSecret/passwordChange.xhtml?iId=" + iid + "&faces-redirect=true";
        }
        return "/influencerSecret/viewInfluencerProfile.xhtml?iId=" + iid + "&faces-redirect=true";
    }

    @PostConstruct
    public void conductSearch() {
        if (searchString == null || searchString.equals("")) {
            influencers = influencerSessionBeanLocal.searchInfluencers(null);
        } else {
            influencers = influencerSessionBeanLocal.searchInfluencers(searchString);
        }
    }

    public String updateBalance() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        double currentBalance = selectedInfluencer.getBalance();
        if (currentBalance < withdrawAmount) {
            context.addMessage(null, new FacesMessage("Unable to withdraw because amount is more than account balance!", ""));
            return "/influencerSecret/viewInfluencerProfile.xhtml?iId=" + iid + "&faces-redirect=true";
        }
        selectedInfluencer.setBalance(currentBalance - withdrawAmount);
        try {
            influencerSessionBeanLocal.updateBalance(selectedInfluencer);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to withdraw!", ""));
        }

        context.addMessage(null, new FacesMessage("Successfully withdrew amount!", ""));
        return "/influencerSecret/viewInfluencerProfile.xhtml?iId=" + iid + "&faces-redirect=true";
    }

    public String deleteInfluencer() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            influencerSessionBeanLocal.deleteInfluencer(influencerAuthenticationManagedBean.getInfluencerId());
            context.addMessage(null, new FacesMessage("Successfully deleted account!", ""));
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Unable to delete account", ""));
        }
        return "/registerInfluencer.xhtml" + "&faces-redirect=true";

    } //end delete

    public void daysLeft() {
        if (today != null && deadline != null) {
            difference = (int) ((today.getTime() - deadline.getTime()) / (1000 * 60 * 60 * 24));
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

    public Long getIid() {
        return iid;
    }

    public void setIid(Long iid) {
        this.iid = iid;
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

    public List<Influencer> getInfluencers() {
        return influencers;
    }

    public void setInfluencers(List<Influencer> influencers) {
        this.influencers = influencers;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public double getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(double withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
        this.difference = difference;
    }

    public Part getUploadedfile() {
        return uploadedfile;
    }

    public void setUploadedfile(Part uploadedfile) {
        this.uploadedfile = uploadedfile;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
