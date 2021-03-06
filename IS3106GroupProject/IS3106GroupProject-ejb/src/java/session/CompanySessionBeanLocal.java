/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Company;
import error.NoResultException;
import java.util.List;
import javax.ejb.Local;

@Local
public interface CompanySessionBeanLocal {

    public void createCompany(Company c);

    public List<Company> searchCompanies(String name);

    public void updateCompany(Company c) throws NoResultException;

    public void deleteCompany(Long cId) throws NoResultException;

    public Company getCompany(Long cId) throws NoResultException;

    public Company login(String companyUsername, String password) throws NoResultException;

    public boolean checkDuplicate(String username);

    public void addContract(Long compId, Long contractId) throws NoResultException;

    public void debitInfluencer(Long cId, double salary) throws NoResultException;

    public void updateProfile(Long cId, String name) throws NoResultException;

    public void topup(Long companyId, double topup) throws NoResultException;

}
