package dao;

import java.util.Collection;

import entities.Company;
import entities.Coupon;
import exceptions.CouponSystemException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityInactiveException;
import exceptions.EntityNotExistException;
import exceptions.NonUniqueDataException;

public interface CompanyDAO {
	public long createCompany(Company c) throws EntityAlreadyExistsException, CouponSystemException, NonUniqueDataException, CouponSystemException;

	public void removeCompany(long companyId) throws CouponSystemException, EntityNotExistException, EntityInactiveException;

	public void updateCompany(Company c) throws EntityNotExistException, NonUniqueDataException, EntityInactiveException, CouponSystemException;

	public Company getCompany(long id) throws CouponSystemException, EntityNotExistException;

	public Collection<Company> getAllCompanies() throws CouponSystemException;

	public Collection<Coupon> getCoupons(long companyId) throws EntityNotExistException, CouponSystemException;

	public boolean login(String companyEmail, String password) throws CouponSystemException;
}
