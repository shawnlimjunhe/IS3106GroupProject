/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Contract;
import error.NoResultException;
import javax.ejb.Local;

@Local
public interface ContractSessionBeanLocal {

    public void createContract(Contract c);

    public void deleteContract(Long cId);

    public void searchContractsByInfluencer(Long iId) throws NoResultException;

    public void searchContractsByCompany(Long cId) throws NoResultException;
}
