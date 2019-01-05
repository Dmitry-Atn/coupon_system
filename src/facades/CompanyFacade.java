package facades;

import java.util.Collection;
import java.sql.Date;

import dao.CompanyDAO;
import dao.CompanyDBDAO;
import dao.CouponDAO;
import dao.CouponDBDAO;
import dao.CustomerDAO;
import dao.CustomerDBDAO;
import entities.Coupon;
import entities.CouponType;
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
	
	public CouponClientFacade login(String email, String password) throws AuthenticationException, CouponSystemException {
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
	
	public void removeCoupon(long couponId) throws CouponSystemException {
		couponDAO.removeCoupon(couponId);
		//TODO: remove purchased coupons
	}
	
	public void updateCoupon(Coupon coupon) throws CouponValidationException, CouponSystemException {
		couponValidator.validate(coupon);
		couponDAO.updateCoupon(coupon);
	}
	
	public Coupon getCoupon(long couponId) throws CouponSystemException {
		return couponDAO.getCoupon(couponId);
	}

	public Collection<Coupon> getAllCoupons(long companyId) throws CouponSystemException{
		return couponDAO.getCouponsByCompany(companyId);
	}
	
	public Collection<Coupon> getCouponsByType(long companyId, CouponType couponType) throws CouponSystemException{
		return couponDAO.getCouponsByCompanyAndType(companyId, couponType);
	}
	
	public Collection<Coupon> getCouponsUpToPrice(long companyId, int price) throws CouponSystemException{
		return couponDAO.getCouponsUpToPrice(companyId, price);
	}
	
	public Collection<Coupon> getCouponsBeforeDate(long companyId, Date date) throws CouponSystemException{
		return couponDAO.getCouponsBeforeDate(companyId, date);
	}
	
	
	
}
