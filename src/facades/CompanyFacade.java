
package facades;

import dao.CompanyDAO;
import dao.CompanyDBDAO;
import dao.CouponDAO;
import dao.CouponDBDAO;
import dao.CustomerDAO;
import dao.CustomerDBDAO;
import entities.Coupon;
import exceptions.AuthenticationException;
import exceptions.CouponSystemException;
import exceptions.CouponValidationException;
import exceptions.EntityAlreadyExistsException;
import validation.CompanyValidator;
import validation.CouponValidator;
import validation.CustomerValidator;
import validation.SimpleCompanyValidator;
import validation.SimpleCouponValidator;
import validation.SimpleCustomerValidator;

public class CompanyFacade implements CouponClientFacade {
	
	private CompanyDAO companyDAO;
	private CustomerDAO customerDAO;
	private CouponDAO couponDAO;
	private CompanyValidator companyValidator;
	private CouponValidator couponValidator;
	private CustomerValidator customerValidator;
	
	public CompanyFacade() {
		this.companyDAO = new CompanyDBDAO();
		this.customerDAO = new CustomerDBDAO();
		this.couponDAO = new CouponDBDAO();
		this.companyValidator = new SimpleCompanyValidator();
		this.customerValidator = new SimpleCustomerValidator();
		this.couponValidator = new SimpleCouponValidator();
	}
	
	public CouponClientFacade login(String email, String password) throws CouponSystemException {
		if (companyDAO.login(email, password)) {
			return this;
		} else {
			throw new AuthenticationException("Wrong pair email password");
		} 
	}
	
	public void createCoupon(Coupon coupon) throws EntityAlreadyExistsException, CouponSystemException {
		couponValidator.validate(coupon);
		couponDAO.createCoupon(coupon);		
	}
	


}
