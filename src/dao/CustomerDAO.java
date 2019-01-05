package dao;

import java.util.Collection;

import entities.Coupon;
import entities.Customer;
import exceptions.CouponSystemException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityInactiveException;
import exceptions.EntityNotExistException;
import exceptions.NonUniqueDataException;

public interface CustomerDAO {
	public void createCustomer(Customer customer) throws NonUniqueDataException, CouponSystemException;
	
	public void removeCustomer(long customerId) throws EntityNotExistException, EntityInactiveException, CouponSystemException;
	
	public void updateCustomer(Customer customer) throws CouponSystemException, EntityAlreadyExistsException, EntityInactiveException, EntityNotExistException;
	
	public Customer getCustomer(long customerId) throws EntityNotExistException, CouponSystemException;
	
	public Collection<Customer> getAllCustomers() throws CouponSystemException;
	
	public Collection<Coupon> getCoupons(long customerId) throws EntityNotExistException, CouponSystemException;
	
	public boolean login(String email, String password) throws CouponSystemException;

	public void clearDB() throws CouponSystemException;

	public long getCustomerIdByName(String string) throws EntityNotExistException, CouponSystemException;
	
	public Collection<Coupon> getPurchaseHistory(long customerId);
	
}
