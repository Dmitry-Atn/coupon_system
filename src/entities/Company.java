
package entities;

import java.util.Collection;

public class Company {
	private long companyID;
	private String companyName; 
	private String password; 
	private String email;
	private Collection<Coupon> coupons;
	
	public Company(long companyID, String companyName, String password, String email, Collection<Coupon> coupons) {
		this(companyName, password, email);
		this.companyID = companyID;
		this.coupons = coupons;
	}
	
	public Company(String companyName, String password, String email) {
		this.companyName = companyName;
		this.password = password;
		this.email = email;
	}
	
	public Company(long companyID, String companyName, String password, String email) {
		this(companyName, password, email);
		this.companyID = companyID;
	}

	public long getCompanyID() {
		return companyID;
	}

	public void setCompanyID(long companyID) {
		this.companyID = companyID;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
		return "Company [companyID=" + companyID + ", companyName=" + companyName + ", password=" + password
				+ ", email=" + email + "]";
	}
	
}
