package facades;

import java.util.Collection;

import dao.CompanyDAO;
import dao.CompanyDBDAO;
import dao.CustomerDAO;
import dao.CustomerDBDAO;
import entities.Company;
import entities.Customer;
import exceptions.AuthenticationException;
import exceptions.CompanyValidationException;
import exceptions.CouponSystemException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityInactiveException;
import exceptions.EntityNotExistException;
import exceptions.NonUniqueDataException;
import validation.CompanyValidator;
import validation.CustomerValidator;
import validation.SimpleCompanyValidator;
import validation.SimpleCustomerValidator;

public class AdminFacade implements CouponClientFacade {
	
	private CompanyDAO companyDAO;
	private CustomerDAO customerDAO;
	private CompanyValidator companyValidator;
	private CustomerValidator customerValidator;
	
	public AdminFacade() {
		this.companyDAO = new CompanyDBDAO();
		this.customerDAO = new CustomerDBDAO();
		this.companyValidator = new SimpleCompanyValidator();
		this.customerValidator = new SimpleCustomerValidator();
	}
	
	@Override
	public CouponClientFacade login(String email, String password) throws CouponSystemException{
		if (email.equals("admin@couponsystem.com") && password.equals("1234")) {
			return this;
		} else {
			throw new AuthenticationException("Wrong pair email password");
		}
	}
	
	public long createCompany(Company company) throws EntityAlreadyExistsException, CompanyValidationException, NonUniqueDataException, CouponSystemException {
		companyValidator.validate(company);
		return companyDAO.createCompany(company);
	}
	
	public void removeCompany(long companyId) throws EntityNotExistException, EntityInactiveException, CouponSystemException {
		companyDAO.removeCompany(companyId);
	}
	
	public void updateCompany(Company company) throws EntityNotExistException, NonUniqueDataException, EntityInactiveException, CouponSystemException {
		companyValidator.validate(company);
		companyDAO.updateCompany(company);
	}
	
	public Company getCompany(long companyId) throws EntityNotExistException, CouponSystemException {
		return companyDAO.getCompany(companyId);
	}
	
	public Collection<Company> getAllCompanies() throws CouponSystemException{
		return companyDAO.getAllCompanies();
	}
	
	public void createCustomer(Customer customer) throws NonUniqueDataException, CouponSystemException {
		customerValidator.validate(customer);
		customerDAO.createCustomer(customer);
	}
	
	public void removeCustomer(long customerId) throws EntityNotExistException, EntityInactiveException, CouponSystemException {
		
		customerDAO.removeCustomer(customerId);
	}
	
	public void updateCustomer(Customer customer) throws EntityAlreadyExistsException, EntityInactiveException, EntityNotExistException, CouponSystemException {
		customerValidator.validate(customer);
		customerDAO.updateCustomer(customer);
	}
	
	public Customer getCustomer(long customerId) throws EntityNotExistException, CouponSystemException {
		return customerDAO.getCustomer(customerId);
	}
	
	public Collection<Customer> getAllCustomers() throws CouponSystemException{
		return customerDAO.getAllCustomers();
	}
			
}
