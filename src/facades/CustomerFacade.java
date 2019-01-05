package facades;

import java.util.Collection;

import dao.CouponDAO;
import dao.CouponDBDAO;
import dao.CustomerDAO;
import dao.CustomerDBDAO;
import entities.Coupon;
import exceptions.AuthenticationException;
import exceptions.CouponSystemException;


public class CustomerFacade implements CouponClientFacade {

	private CustomerDAO customerDAO;
	private CouponDAO couponDAO;
	
	public CustomerFacade() {
		
		this.customerDAO = new CustomerDBDAO();
		this.couponDAO = new CouponDBDAO();
		
	}

	public CouponClientFacade login(String email, String password)
			throws CouponSystemException {
		// TODO Auto-generated method stub
		if (customerDAO.login(email, password)) {
			return this;
		} else {
			throw new AuthenticationException("Wrong pair email password");
		}
		
	}
	
	public void purchaseCoupon(long couponId, long customerId) throws CouponSystemException {
		couponDAO.assignCoupon(couponId, customerId);
	}
	
	public Collection<Coupon> getPurchaseHistory(long customerId) {
		return customerDAO.getPurchaseHistory(customerId);
	}

}
