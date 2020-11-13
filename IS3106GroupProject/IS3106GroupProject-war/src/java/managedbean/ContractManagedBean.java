/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Contract;
import error.NoResultException;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import session.ContractSessionBeanLocal;

/**
 *
 * @author Cze_J
 */
@Named(value = "contractManagedBean")
@ViewScoped
public class ContractManagedBean implements Serializable {

    @EJB
    private ContractSessionBeanLocal contractSessionBeanLocal;

    @Inject
    private InfluencerAuthenticationManagedBean influencerAuthenticationManagedBean;

    public ContractManagedBean() {
    }

    public List<Contract> getInfPastContracts() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            List<Contract> contracts = contractSessionBeanLocal.getInfPastContracts(influencerAuthenticationManagedBean.getInfluencerId());
            return contracts;
        } catch (NoResultException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Unable to load contracts", ""));
            return null;
        }
    }

    public List<Contract> getInfOngoingContracts() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            List<Contract> contracts = contractSessionBeanLocal.getInfOngoingContracts(influencerAuthenticationManagedBean.getInfluencerId());
            return contracts;
        } catch (NoResultException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Unable to load contracts", ""));
            return null;
        }
    }

    public ContractSessionBeanLocal getContractSessionBeanLocal() {
        return contractSessionBeanLocal;
    }

    public void setContractSessionBeanLocal(ContractSessionBeanLocal contractSessionBeanLocal) {
        this.contractSessionBeanLocal = contractSessionBeanLocal;
    }

    public InfluencerAuthenticationManagedBean getInfluencerAuthenticationManagedBean() {
        return influencerAuthenticationManagedBean;
    }

    public void setInfluencerAuthenticationManagedBean(InfluencerAuthenticationManagedBean influencerAuthenticationManagedBean) {
        this.influencerAuthenticationManagedBean = influencerAuthenticationManagedBean;
    }

}
