/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Application;
import error.NoResultException;
import java.util.List;
import javax.ejb.Local;

@Local
public interface ApplicationSessionBeanLocal {

    public void createApplication(Application a);

    public void updateApplication(Application a) throws NoResultException;

    public void deleteApplication(Long aId) throws NoResultException;

    public List<Application> searchApplicationsByInfluencer(Long iId) throws NoResultException;

    public Application getApplication(Long aId) throws NoResultException;

    public List<Application> getApplicationsWithStatus(Long iId, String accepted) throws NoResultException;

    public void setApplicationAccepted(Long aId, String status);

    public void setApplicationRejectionReason(Long aId, String rejectionReason);
}
