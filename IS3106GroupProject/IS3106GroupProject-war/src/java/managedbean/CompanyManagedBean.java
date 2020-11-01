/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Company;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import session.CompanySessionBeanLocal;

/**
 *
 * @author laurentiakky
 */
@Named(value = "companyManagedBean")
@ViewScoped
public class CompanyManagedBean implements Serializable {

    @EJB
    private CompanySessionBeanLocal companySessionBeanLocal;
    
    private String name;
    
    private Company selectedCompany;
    
    private Long cId;
    
    private double balance;
    
    private String password;
    
    
    public CompanyManagedBean() {
    }
    
    public String createCompany() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            Company c = new Company();
            c.setName(name);
            c.setPassword(password);
            companySessionBeanLocal.createCompany(c);
            return "companyLogin.xhtml?faces-redirect=true";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to create company account!"));
            return "index.xhtml?faces-redirect=true";
        }
    }
    
    public void loadSelectedCompany() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            selectedCompany = companySessionBeanLocal.getCompany(cId);
            balance = selectedCompany.getBalance();
            name = selectedCompany.getName();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load user"));
        }
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
    
}
