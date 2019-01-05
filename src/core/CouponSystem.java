package core;

import dao.ConnectionPool;
import exceptions.ClientTypeException;
import exceptions.CouponSystemException;
import facades.AdminFacade;
import facades.ClientType;
import facades.CompanyFacade;
import facades.CouponClientFacade;
import facades.CustomerFacade;

public class CouponSystem {
	
	private static CouponSystem instance;
	private Thread dailyTask;
	
	
	private CouponSystem() {
		dailyTask = new Thread(new DailyCouponExpirationTask());
		dailyTask.setDaemon(false);
		dailyTask.start();
		
	}
	
	public static CouponSystem getInstance() {
		if (CouponSystem.instance == null) {
			instance = new CouponSystem();
		}
		return instance;
	}
	
	public CouponClientFacade login(String email, String password, ClientType type) throws CouponSystemException{
		if (type == ClientType.ADMINISTRATOR) {
			AdminFacade adminFacade = new AdminFacade();
			return adminFacade.login(email, password);
		} else if(type == ClientType.COMPANY) {
			CompanyFacade companyFacade = new CompanyFacade();
			return companyFacade.login(email, password);
		} else if (type == ClientType.CUSTOMER){
			CustomerFacade customerFacade = new CustomerFacade();
			return customerFacade.login(email, password);
		} else {
			throw new ClientTypeException("Access denied. This client type has no permissions.");
		}
	}
	
	public void shutDown() {
		ConnectionPool cp = ConnectionPool.getInstance();
		cp.closeAll();
		System.exit(0);
	}
	
}
