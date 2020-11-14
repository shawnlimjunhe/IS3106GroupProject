/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Contract;
import error.NoResultException;
import java.util.List;
import javax.ejb.Local;

@Local
public interface ContractSessionBeanLocal {

    public void createContract(Contract c);

    public void deleteContract(Long cId) throws NoResultException;

    public List<Contract> searchContractsByInfluencer(Long iId) throws NoResultException;

    public List<Contract> searchContractsByCompany(Long cId) throws NoResultException;

    public List<Contract> getInfPastContracts(Long iId) throws NoResultException;

    public List<Contract> getInfOngoingContracts(Long iId) throws NoResultException;

    public void updateContract(Contract c) throws NoResultException;

    public void setContractCompanyandInfluencer(Long contractId, Long cId, Long iId, Long postId);

    public Contract getContract(Long cId) throws NoResultException;

    public void addLink(Long contractId, String link) throws NoResultException;

    public void acceptContract(Long cId) throws NoResultException;
}
