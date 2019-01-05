package dao;

import java.sql.Date;
import java.util.Collection;
import exceptions.*;
import entities.*;

// interface to define the operation needed for a Coupon object,
// decoupling functionality from implementation.
// implementation will have to deal with DB driver and SQL commands

public interface CouponDAO {

	// throw exceptions by void type

	
	public long createCoupon(Coupon coupon) throws CouponSystemException, EntityAlreadyExistsException;

	public void removeCoupon(long id) throws CouponSystemException;

	public void updateCoupon(Coupon coupon) throws CouponSystemException;

	public Coupon getCoupon(long id) throws CouponSystemException;

	public Collection<Coupon> getAllCoupons() throws CouponSystemException;

	public Collection<Coupon> getCouponsByType(CouponType couponType) throws CouponSystemException;

	public Collection<Coupon> getOldCoupons() throws CouponSystemException;
	
	public Collection<Coupon> getCouponsByCompany(long companyId) throws CouponSystemException;

	public Collection<Coupon> getCouponsByCompanyAndType(long companyId, CouponType couponType) throws CouponSystemException;

	public Collection<Coupon> getCouponsUpToPrice(long companyId, int price) throws CouponSystemException;

	public Collection<Coupon> getCouponsBeforeDate(long companyId, Date date) throws CouponSystemException;
	
	public void assignCoupon(long couponId, long customerId) throws CouponSystemException;

}