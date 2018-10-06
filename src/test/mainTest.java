package test;
import java.sql.Connection;
import java.util.Collection;

import dao.CompanyDAO;
import dao.CompanyDBDAO;
import dao.ConnectionPool;
import dao.CustomerDAO;
import dao.CustomerDBDAO;
import entities.Company;
import entities.Customer;
import exceptions.CouponSystemException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityInactiveException;
import exceptions.EntityNotExistException;
import exceptions.NonUniqueDataException;

public class mainTest {
	private static CompanyDAO companyDbdao;
	private static CustomerDAO customerDbdao;
	
    public static void runTest(){
    	companyDbdao = new CompanyDBDAO();
    	customerDbdao = new CustomerDBDAO();
    	TestConnectionPool();
        createCompanyTest();
        removeCompanyTest();
        updateCompanyTest();
        getAllCompaniesTest();
        createCustomerTest();
        removeCustomerTest();
        updateCustomerTest();
        getAllCustomersTest();
        finishWork();
    }

    private static void TestConnectionPool(){
        try{
            ConnectionPool cp = ConnectionPool.getInstance();
            Connection c = cp.getConnection();
            cp.returnConnection(c);
            System.out.println("Connection Pool test passed succesfully!");
        }
        catch (Exception e){
            System.out.print(e.getMessage());
            System.out.print("TestConnectionPool FAILED!");
        }
    }
    
    private static void createCompanyTest() {
    	boolean res = true;
    	try {
    		companyDbdao.createCompany(new Company("Duplicated Company", "123", "13@asd.zxc"));
    		companyDbdao.createCompany(new Company("Duplicated Company", "123", "133@asd.zxc"));
    		res = false;
    		System.out.println("createCompanyTtest failed: duplicated company names availiable");
    	}
    	catch (EntityAlreadyExistsException e) {
    		System.out.println(e.getMessage());
    		System.out.println("EntityAlreadyExistsException caught!");
		}
    	catch (CouponSystemException e) {
    		System.out.println(e.getMessage());
    		System.out.println("Unexpected CouponSystemException");
    		res = false;
    	}
    	try {
    		companyDbdao.createCompany(new Company("Duplicated EMail Company","123","13@asd.zxc"));
    		res = false;
    		System.out.println("nonunique company email available");
    	}
    	catch (NonUniqueDataException e){
    		System.out.println(e.getMessage());
    		System.out.println("NonUniqueDataException caught!");
    	}
    	catch (CouponSystemException e) {
    		System.out.println("Unexpected CouponSystemException");
    		res = false;
    	}
    	if (res) {
    		System.out.println("createCompanyTest passed successfully");
    	} else {
    		System.out.println("createCompanyTest failed");
    	}
    	try {
    		new CompanyDBDAO().clearDB();
    	} catch (CouponSystemException e) {
    		System.out.println("Clean-up after updateCompanyTest: " + e.getMessage());
  		}
    }
    
    private static void removeCompanyTest() {
		boolean res = true;
    	long companyId = 0;
		try {
			companyId = companyDbdao.createCompany(new Company("CompanyToRemove", "123", "135@asd.zxc"));
    		companyDbdao.removeCompany(companyId);
    		companyDbdao.removeCompany(companyId);
    		res = false;
    	}
    	catch (EntityInactiveException e){
    		System.out.println("EntityInactiveException caught!");
    	}
    	catch (CouponSystemException e) {
    		System.out.println(e.toString());
    		res = false;
    	}
    	try {
    		companyDbdao.removeCompany(companyId + 1);
    		res = false;
    	}
    	catch (EntityNotExistException e) {
			System.out.println("EntityNotExistException caught!");
		}
    	catch (CouponSystemException e) {
    		System.out.println(e.toString());
    		res = false;
    	}
    	if (res) {
    		System.out.println("removeCompanyTest passed successfully!");
    	} else {
    		System.out.println("removeCompanyTest failed!");
    	}
    	try {
    		new CompanyDBDAO().clearDB();
    	} catch (Exception e) {
    		System.out.println("Clean-up after removeCompanyTest: " + e.getMessage());
    	}
    }
    
    private static void updateCompanyTest() {
    	boolean res = true;
    	try {
    		companyDbdao.updateCompany(new Company(0, "0-id company", "password", "email"));
			res = false;
		} catch (EntityNotExistException e) {
			System.out.println("EntityNotExistException caught!");
		} catch (CouponSystemException e) {
			System.out.println(e.getMessage());
			res = false;
		}
    	long companyId=0;
    	try {
    		companyId = companyDbdao.createCompany(new Company("Test Company", "123", "email"));
    		companyDbdao.createCompany(new Company("Test Company1", "123", "email1"));
    		companyDbdao.updateCompany(new Company(companyId, "Test Company1", "123", "email"));
			res = false;
		} catch (EntityAlreadyExistsException e) {
			System.out.println(e.getMessage());
			System.out.println("EntityAlreadyExistsException caught!");
			// TODO: handle exception
		} catch (CouponSystemException e){
			System.out.println(e.getMessage());
			res = false;
		}
    	try {
    		companyDbdao.updateCompany(new Company(companyId, "Test Company", "123", "email1"));
    	} catch (NonUniqueDataException e) {
			// TODO: handle exception
    		System.out.println(e.getMessage());
    		System.out.println("NonUniqueDataException caught!");	
		} catch (CouponSystemException e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
    	if (res) {
    		System.out.println("updateCompanyTest passed successfully!");
    	} else {
    		System.out.println("updateCompanyTest failed!");
    	}
    	try {
    		new CompanyDBDAO().clearDB();
    	} catch (CouponSystemException e) {
    		System.out.println("Clean-up after updateCompanyTest: " + e.getMessage());
  		}
    }
    
    private static void getAllCompaniesTest() {
    	boolean res = true;
    	try {
			Collection<Company> companies = companyDbdao.getAllCompanies();
			if (companies.size() != 0) {
				res = false;
			}
		} catch (CouponSystemException e) {
			System.out.println("Making sure that there are no companies getAllCompaniesTest:" + e.getMessage());
		}
    	try {
    		companyDbdao.createCompany(new Company("one", "password", "email"));
    		companyDbdao.createCompany(new Company("one1", "password1", "email1"));
    	}catch (CouponSystemException e) {
			System.out.println("Companies creation getAllCompaniesTest: " + e.getMessage());
			res = false;
    	}
    	try {
			Collection<Company> companies = companyDbdao.getAllCompanies();
			if (companies.size() != 2) {
				res = false;
			}
		} catch (CouponSystemException e) {
			System.out.println("Making sure that there are companies after their creation getAllCompaniesTest:" + e.getMessage());
			e.printStackTrace();
			res = false;
		}
    	if (res) {
    		System.out.println("getAllCompaniesTest() passed successfully!");
    	} else {
    		System.out.println("getAllCompaniesTest() failed!");
    	}    	
    	try {
    		new CompanyDBDAO().clearDB();
    	} catch (CouponSystemException e) {
    		System.out.println("Clean-up after updateCompanyTest: " + e.getMessage());
  		}
    }
    
    
    
    
    
    
    private static void createCustomerTest() {
    	boolean res = true;
    	try {
    		customerDbdao.createCustomer(new Customer("Duplicated EMail Customer","123","13@asd.zxc"));
    		customerDbdao.createCustomer(new Customer("Duplicated EMail Customer","123","13@asd.zxc"));
    		res = false;
    		System.out.println("nonunique customer email available");
    	}
    	catch (NonUniqueDataException e){
    		System.out.println(e.getMessage());
    		System.out.println("NonUniqueDataException caught!");
    	}
    	catch (CouponSystemException e) {
    		System.out.println("Unexpected CouponSystemException");
    		res = false;
    	}
    	if (res) {
    		System.out.println("createCustomerTest passed successfully");
    	} else {
    		System.out.println("createCustomerTest failed");
    	}
    	try {
    		customerDbdao.clearDB();
    	} catch (CouponSystemException e) {
    		System.out.println("Clean-up after updateCompanyTest: " + e.getMessage());
  		}
    }
    
    
    private static void removeCustomerTest() {
		boolean res = true;
    	long CustomerId = 0;
		try {
			customerDbdao.createCustomer(new Customer("CustomerToRemove", "123", "135@asd.zxc"));
    		CustomerId = customerDbdao.getCustomerIdByName("CustomerToRemove");
    		customerDbdao.removeCustomer(CustomerId);
    		customerDbdao.removeCustomer(CustomerId);
    		res = false;
    	}
    	catch (EntityInactiveException e){
    		System.out.println("EntityInactiveException caught!");
    	}
    	catch (CouponSystemException e) {
    		System.out.println(e.toString());
    		res = false;
    	}
    	try {
    		customerDbdao.removeCustomer(CustomerId + 1);
    		res = false;
    	}
    	catch (EntityNotExistException e) {
			System.out.println("EntityNotExistException caught!");
		}
    	catch (CouponSystemException e) {
    		System.out.println(e.toString());
    		res = false;
    	}
    	if (res) {
    		System.out.println("removeCustomerTest passed successfully!");
    	} else {
    		System.out.println("removeCustomerTest failed!");
    	}
    }
    
    private static void updateCustomerTest() {
    	boolean res = true;
    	try {
    		customerDbdao.updateCustomer(new Customer(0, "0-id customer", "password", "email"));
			res = false;
		} catch (EntityNotExistException e) {
			System.out.println("EntityNotExistException caught!");
		} catch (CouponSystemException e) {
			System.out.println(e.getMessage());
			res = false;
		}
    	try {
    		customerDbdao.createCustomer(new Customer("Test Customer", "123", "email"));
    		customerDbdao.createCustomer(new Customer("Test Customer1", "123", "email1"));
    		customerDbdao.updateCustomer(new Customer(customerDbdao.getCustomerIdByName("Test Customer1"), "Test Customer1", "123", "email"));
			res = false;
		} catch (NonUniqueDataException e) {
			System.out.println(e.getMessage());
			System.out.println("EntityAlreadyExistsException caught!");
			// TODO: handle exception
		} catch (CouponSystemException e){
			System.out.println(e.getMessage());
			res = false;
		}
    	try {
    		customerDbdao.updateCustomer(new Customer(new CustomerDBDAO().getCustomerIdByName("Test Customer"), "Test Customer", "123", "email1"));
    	} catch (NonUniqueDataException e) {
			// TODO: handle exception
    		System.out.println(e.getMessage());
    		System.out.println("NonUniqueDataException caught!");	
		} catch (CouponSystemException e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
    	if (res) {
    		System.out.println("updateCustomerTest passed successfully!");
    	} else {
    		System.out.println("updateCustomerTest failed!");
    	}
    	try {
    		new CustomerDBDAO().clearDB();
    	} catch (CouponSystemException e) {
    		System.out.println("Clean-up after updateCustomerTest: " + e.getMessage());
  		}
    }
    
    
    private static void getAllCustomersTest() {
    	boolean res = true;
    	try {
			Collection<Customer> customers = customerDbdao.getAllCustomers();
			if (customers.size() != 0) {
				res = false;
			}
		} catch (CouponSystemException e) {
			System.out.println("Making sure that there are no customers getAllCustomersTest:" + e.getMessage());
		}
    	try {
    		customerDbdao.createCustomer(new Customer("one", "password", "email"));
    		customerDbdao.createCustomer(new Customer("one1", "password1", "email1"));
    	}catch (CouponSystemException e) {
			System.out.println("Customers creation getAllCustomersTest: " + e.getMessage());
			res = false;
    	}
    	try {
			Collection<Customer> customers = customerDbdao.getAllCustomers();
			if (customers.size() != 2) {
				res = false;
			}
		} catch (CouponSystemException e) {
			System.out.println("Making sure that there are customers after their creation getAllCustomersTest:" + e.getMessage());
			e.printStackTrace();
			res = false;
		}
    	if (res) {
    		System.out.println("getAllCustomersTest() passed successfully!");
    	} else {
    		System.out.println("getAllCustomersTest() failed!");
    	}    	
    	try {
    		new CustomerDBDAO().clearDB();
    	} catch (CouponSystemException e) {
    		System.out.println("Clean-up after updateCustomerTest: " + e.getMessage());
  		}
    }
    
    
    private static void finishWork() {
    	try {
			ConnectionPool cp = ConnectionPool.getInstance();
			cp.closeAll();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			// TODO: handle exception
		}
    }
    
    
}
