package entities;

import java.util.Collection;

public class Customer {
	private long customerID; 
	private String name;
	private String password; 
	private String email;
	private Collection<Coupon> coupons;
	
	

	public Customer(String name, String password, String email) {
		this.name = name;
		this.password = password;
		this.email = email;
	}
	
	public Customer(long id, String name, String password, String email, Collection<Coupon> coupons) {
		this(name, password, email);
		this.customerID = id;
		this.coupons = coupons;
	}
	
	public Customer(long id, String name, String password, String email) {
		this(name, password, email);
		this.customerID = id;
	}

	public long getCustomerID() {
		return customerID;
	}

	public void setCustomerID(long customerID) {
		this.customerID = customerID;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	@Override
	public String toString() {
		return "Customer [customerID=" + customerID + ", customerName=" + name + ", email=" + email + ", password=" + password + ", email=" + email + "]";
	} 
	
}
