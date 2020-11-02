package session;

import error.NoResultException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;

@Singleton
@LocalBean
public class DataInitSessionBean {

	@EJB
	private CompanySessionBeanLocal companySB;

	public DataInitSessionBean() {
	}

	@PostConstruct
	public void postConstruct() {
		try {
			companySB.getCompany(new Long(1));
		} catch (NoResultException ex) {
			initData();
		}
	}

	private void initData() {
		// Add the data init things here
	}
}
