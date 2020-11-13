/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Company;
import error.NoResultException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import session.CompanySessionBeanLocal;

/**
 *
 * @author laurentiakky
 */
@Named(value = "companyAuthenticationManagedBean")
@SessionScoped
public class CompanyAuthenticationManagedBean implements Serializable {

    private String companyName = null;
    private String companyUsername = null;
    private String password = null;
    private Long companyId = new Long(-1);

    private Company company;

    @EJB
    private CompanySessionBeanLocal companySessionBeanLocal;

    public CompanyAuthenticationManagedBean() {
    }

    public String login() throws NoResultException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        company = companySessionBeanLocal.login(companyUsername, password);

        if (company == null) {
            //login failed
            companyName = null;
            companyUsername = null;
            password = null;
            companyId = new Long(-1);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong login information", ""));
            return "companyLogin.xhtml?faces-redirect=true";
        } else {
            companyId = company.getId();
            return "/companySecret/posts.xhtml?faces-redirect=true";
        }
    }

    public String logout() {
        companyUsername = null;
        companyName = null;
        password = null;
        companyId = new Long(-1);

        return "/companyLogin.xhtml?faces-redirect=true";
    } //end logout

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyUsername() {
        return companyUsername;
    }

    public void setCompanyUsername(String companyUsername) {
        this.companyUsername = companyUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

}
