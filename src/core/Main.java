package core;

import exceptions.CouponSystemException;
import facades.AdminFacade;
import facades.ClientType;

public class Main {
    
	public static void main(String args[]){       
		
		CouponSystem cs = CouponSystem.getInstance();
		try {
			AdminFacade af = (AdminFacade)cs.login("admin@couponsystem.com", "1234", ClientType.ADMINISTRATOR);
		} catch (CouponSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    }
}