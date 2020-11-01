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

    public Company login(String companyName, String password) throws NoResultException;
}
