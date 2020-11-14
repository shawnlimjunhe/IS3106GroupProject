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
import entity.Influencer;
import error.NoResultException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import session.ApplicationSessionBeanLocal;
import session.CompanySessionBeanLocal;
import session.ContractSessionBeanLocal;
import session.InfluencerSessionBeanLocal;
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
    @EJB
    private ApplicationSessionBeanLocal appSB;
    @EJB
    private InfluencerSessionBeanLocal influencerSB;
    @EJB
    private ContractSessionBeanLocal contractSB;

    private String name;
    private String searchTerm;
    private String rejectionReason;
    private String influencerName;

    private Company selectedCompany;

    private List<Post> posts;
    private List<Contract> contracts;
    private List<Application> applications;

    private Post selectedPost;
    private Application selectedApplication;
    private Influencer selectedInfluencer;
    private Contract selectedContract;

    private Long pId;
    private Long cId;
    private Long appId;
    private Long contractId;

    private double balance;

    private String password;
    private String username;

    // variables for creating posts
    private String postTitle;
    private String postDescription;
    private Date postDeadline;
    private double postSalary;
    private int minFollowers;
    private double topup = 50.0;

    public CompanyManagedBean() {
    }

    public String createCompany() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            Company c = new Company();
            c.setUsername(username);
            c.setName(name);
            c.setPassword(password);
            companySB.createCompany(c);
            return "companyLogin.xhtml?faces-redirect=true";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Account Username is taken!", ""));
            return "registerCompany.xhtml?faces-redirect=true";
        }
    }

    public void topup() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (topup < 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sorry Top up cannot be negative", ""));
        }
        try {
            companySB.topup(caMB.getCompanyId(), topup);
            topup = 50.0;
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Top up failed", ""));
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

    // LOADING METHODS
    public void loadSelectedApplication() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            selectedApplication = appSB.getApplication(appId);
            selectedInfluencer = influencerSB.getInfluencer(selectedApplication.getInfluencerId());
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load posts"));
        }
    }

    public void loadSelectedContract() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            setSelectedContract(contractSB.getContract(getContractId()));
            selectedInfluencer = influencerSB.getInfluencer(selectedContract.getInfluencerId());
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load posts"));
        }
    }

    // Returns true if no duplicates
    public boolean checkDuplicate() {
        return companySB.checkDuplicate(username);
    }

    public void loadcompanyPosts() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (searchTerm == null) {
            System.out.print("reloading");
            try {
                setSelectedCompany(companySB.getCompany(caMB.getCompanyId()));
                setPosts(selectedCompany.getPosts());

            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load application"));
            }
        } else {
            posts = postSB.searchPostsFromCompany(searchTerm, caMB.getCompanyId());
            setSearchTerm(null);
        }
    }

    public void loadcompanyContracts() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            setSelectedCompany(companySB.getCompany(caMB.getCompanyId()));
            setContracts(selectedCompany.getContracts());

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load application"));
        }

    }

    public void loadCompany() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            selectedCompany = companySB.getCompany(caMB.getCompanyId());
            balance = selectedCompany.getBalance();
            name = selectedCompany.getName();
            username = selectedCompany.getUsername();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load user"));
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

    public String acceptApplication() {
        try {
            appSB.setApplicationAccepted(selectedApplication.getId(), "accepted");
            List<String> links = new ArrayList();
            Date today = new Date();
            long ltime = today.getTime() + 5 * 24 * 60 * 60 * 1000;
            Date end = new Date(ltime);
            double salary = postSB.getPost(selectedApplication.getPostId()).getSalary();
            Contract c = new Contract(today, end, salary);
            contractSB.createContract(c);
            Long contractId = c.getId();
            Long iId = selectedInfluencer.getId();
            Long compId = caMB.getCompanyId();
            contractSB.setContractCompanyandInfluencer(contractId, compId, iId);
            companySB.addContract(compId, contractId);
            influencerSB.addContract(iId, contractId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/companySecret/viewApplication.xhtml?appId=" + selectedApplication.getId() + "&faces-redirect=true";
    }

    public String acceptContract() {
        // set contract to accepted
        try {
            contractSB.acceptContract(selectedContract.getId());
            influencerSB.acceptContract(selectedInfluencer.getId());
            influencerSB.creditInfluencer(selectedInfluencer.getId(), selectedContract.getSalary());
            companySB.debitInfluencer(caMB.getCompanyId(), selectedContract.getSalary());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/companySecret/viewContract.xhtml?contractId=" + selectedContract.getId();
    }

    public String rejectApplication() {
        try {
            appSB.setApplicationAccepted(selectedApplication.getId(), "rejected");
            appSB.setApplicationRejectionReason(selectedApplication.getId(), rejectionReason);
            rejectionReason = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/companySecret/viewApplication.xhtml?appId=" + selectedApplication.getId();
    }

    public String getCompanyName(Long cId) throws NoResultException {
        Company comp = companySB.getCompany(cId);
        if (comp != null) {
            return comp.getName();
        } else {
            throw new NoResultException("Not found");
        }
    }

    public String endPost() {
        try {
            selectedPost = postSB.getPost(selectedPost.getId());
            postSB.endPost(selectedPost.getId());
            List<Application> apps = selectedPost.getApplications();
            for (Application a : apps) {
                if (a.getAccepted().equals("pending")) {
                    appSB.setApplicationAccepted(a.getId(), "rejected");
                    appSB.setApplicationRejectionReason(a.getId(), "Company has manually ended the Job");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "posts.xhtml?faces-redirect=true";
    }

    public String updateCompany() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            companySB.updateProfile(caMB.getCompanyId(), name);
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage("Failed to update profile!", ""));
        }

        context.addMessage(null, new FacesMessage("Successfully updated profile!", ""));
        return "/companySecret/viewCompanyProfile.xhtml?cId=" + caMB.getCompanyId() + "&faces-redirect=true";
    }

    public String searchPosts() {
        return "posts.xhtml?searchTerm=" + searchTerm + "&faces-redirect=true";
    }

    public String searchApplications() {
        return "viewPost.xhtml?pId=" + pId + "&searchTerm=" + searchTerm + "&faces-redirect=true";
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

    public Application getSelectedApplication() {
        return selectedApplication;
    }

    public void setSelectedApplication(Application selectedApplication) {
        this.selectedApplication = selectedApplication;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Influencer getSelectedInfluencer() {
        return selectedInfluencer;
    }

    public void setSelectedInfluencer(Influencer selectedInfluencer) {
        this.selectedInfluencer = selectedInfluencer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Contract getSelectedContract() {
        return selectedContract;
    }

    public void setSelectedContract(Contract selectedContract) {
        this.selectedContract = selectedContract;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getInfluencerName() {
        return influencerName;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public double getTopup() {
        return topup;
    }

    public void setTopup(double topup) {
        this.topup = topup;
    }

}
