package test;

import java.util.Collection;

import core.CouponSystem;
import entities.Company;
import exceptions.AuthenticationException;
import exceptions.CompanyValidationException;
import exceptions.CouponSystemException;
import exceptions.EntityAlreadyExistsException;
import exceptions.NonUniqueDataException;
import facades.AdminFacade;
import facades.ClientType;

public class AdminFacadeTest {

	public AdminFacadeTest() {
		
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		AdminFacade af;
		CouponSystem cs = CouponSystem.getInstance();
		//login test
		try {
			af = (AdminFacade) cs.login("admin@couponsystem.com", "1", ClientType.ADMINISTRATOR);
		} catch (AuthenticationException e) {
			System.out.println("Success: " + e.getMessage());
		} catch (CouponSystemException e1) {
			System.out.println(e1.getMessage());
		}
		try {
			af = (AdminFacade) cs.login("admin@couponsystem.com", "1234", ClientType.ADMINISTRATOR);
		} catch (Exception e) {
			System.out.println("Fail: " + e.getMessage());
			System.exit(1);
		}
		
		//create company test
		long company1, company2, company3;
		
		try {
			af = (AdminFacade) cs.login("admin@couponsystem.com", "1234", ClientType.ADMINISTRATOR);
			//validation test
			try {
				company1 = af.createCompany(new Company("1", "12345678aB", "email@email.com"));
			} catch (CompanyValidationException e) {
				System.out.println("Success: "+ e.getMessage());
			} catch(EntityAlreadyExistsException e1) {
				System.out.println("fail: "+ e1.getMessage());			
			} catch(NonUniqueDataException e2) {
				System.out.println("fail: "+ e2.getMessage());
			} catch(CouponSystemException e3) {
				System.out.println("fail: "+ e3.getMessage());			
			}
			
			//duplicate company test
			try {
				af.createCompany(new Company("CompanyName", "Passwo1@", "email@gmail.com"));
			} catch (CompanyValidationException e) {
				System.out.println("fail: "+ e.getMessage());
			} catch(EntityAlreadyExistsException e1) {
				System.out.println("fail: "+ e1.getMessage());		
			} catch(NonUniqueDataException e2) {
				System.out.println("fail: "+ e2.getMessage());			
			} catch(CouponSystemException e3) {
				System.out.println("fail: "+ e3.getMessage());			
			} 
			try {
				af.createCompany(new Company("CompanyName", "Passwo1@", "email@gmail.com"));
			} catch (CompanyValidationException e) {
				System.out.println("fail: "+ e.getMessage());
			} catch(EntityAlreadyExistsException e1) {
				System.out.println("Success: "+ e1.getMessage());		
			} catch(NonUniqueDataException e2) {
				System.out.println("fail: "+ e2.getMessage());			
			} catch(CouponSystemException e3) {
				System.out.println("fail: "+ e3.getMessage());			
			} 
			try {
				company1 = af.createCompany(new Company("CompanyName1", "Passwo1@", "email1@gmail.com"));
			} catch (CompanyValidationException e) {
				System.out.println("fail: "+ e.getMessage());
			} catch(EntityAlreadyExistsException e1) {
				System.out.println("fail: "+ e1.getMessage());		
			} catch(NonUniqueDataException e2) {
				System.out.println("fail: "+ e2.getMessage());			
			} catch(CouponSystemException e3) {
				System.out.println("fail: "+ e3.getMessage());		
			} 
			
			
			// duplicate email test
			try {
				af.createCompany(new Company("CompanyName2", "Passwo1@", "email1@gmail.com"));
			} catch (CompanyValidationException e) {
				System.out.println("fail: "+ e.getMessage());
			} catch(EntityAlreadyExistsException e1) {
				System.out.println("fail: "+ e1.getMessage());		
			} catch(NonUniqueDataException e2) {
				System.out.println("Success: "+ e2.getMessage());
			} catch(CouponSystemException e3) {
				System.out.println("fail: "+ e3.getMessage());			
			} 
			
			// update company
			
			
		} catch (Exception e) {
			System.out.println("Fail: " + e.getMessage());
			System.exit(1);
		}
		
		
	}	


}
