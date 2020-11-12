/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Company;
import entity.Contract;
import entity.Post;
import entity.Application;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import session.CompanySessionBeanLocal;
import session.PostSessionBeanLocal;

/**
 *
 * @author laurentiakky
 */
@Named(value = "companyManagedBean")
@ViewScoped
public class CompanyManagedBean implements Serializable {

    @Inject
    private CompanyAuthenticationManagedBean caMB;

    @EJB
    private CompanySessionBeanLocal companySB;
    @EJB
    private PostSessionBeanLocal postSB;

    private String name;
    private String searchTerm;

    private Company selectedCompany;

    private List<Post> posts;
    private List<Contract> contracts;
    private List<Application> applications;

    private Post selectedPost;

    private Long pId;
    private Long cId;

    private double balance;

    private String password;

    // variables for creating posts
    private String postTitle;
    private String postDescription;
    private Date postDeadline;
    private double postSalary;
    private int minFollowers;

    public CompanyManagedBean() {
    }

    public String createCompany() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            Company c = new Company();
            c.setName(name);
            c.setPassword(password);
            companySB.createCompany(c);
            return "companyLogin.xhtml?faces-redirect=true";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to create company account!"));
            return "index.xhtml?faces-redirect=true";
        }
    }

    public String createPost() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            setSelectedCompany(companySB.getCompany(caMB.getCompanyId()));
            Post p = new Post(postTitle, postDescription, postDeadline, postSalary, minFollowers);
            Long createdPostId = postSB.createPost(p);
            postSB.addCompanyToPost(createdPostId, selectedCompany.getId());
            return "posts.xhtml";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to create company account!"));
            return "managePost.xhtml";
        }
    }

    public String postAction() {
        return this.createPost();
    }

    public void loadcompanyPosts() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (searchTerm == null) {
            System.out.print("reloading");
            try {
                setSelectedCompany(companySB.getCompany(caMB.getCompanyId()));
                setPosts(selectedCompany.getPosts());

            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load posts"));
            }
        } else {
            posts = postSB.searchPostsFromCompany(searchTerm, caMB.getCompanyId());
            setSearchTerm(null);
        }
    }

    public void loadSelectedCompany() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            selectedCompany = companySB.getCompany(cId);
            balance = selectedCompany.getBalance();
            name = selectedCompany.getName();
            posts = selectedCompany.getPosts();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load user"));
        }
    }

    public void loadSelectedPost() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (pId != null) {
            try {
                selectedPost = postSB.getPost(pId);
                applications = selectedPost.getApplications();
            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load post"));
            }
        }

    }

    public String searchPosts() {
        FacesContext context = FacesContext.getCurrentInstance();
        return "posts.xhtml?searchTerm=" + searchTerm + "&faces-redirect=true";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Company getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Post getSelectedPost() {
        return selectedPost;
    }

    public void setSelectedPost(Post selectedPost) {
        this.selectedPost = selectedPost;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public Date getPostDeadline() {
        return postDeadline;
    }

    public void setPostDeadline(Date postDeadline) {
        this.postDeadline = postDeadline;
    }

    public double getPostSalary() {
        return postSalary;
    }

    public void setPostSalary(double postSalary) {
        this.postSalary = postSalary;
    }

    public int getMinFollowers() {
        return minFollowers;
    }

    public void setMinFollowers(int minFollowers) {
        this.minFollowers = minFollowers;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

}
