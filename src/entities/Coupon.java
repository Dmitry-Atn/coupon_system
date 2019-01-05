package entities;

import java.sql.Date;

public class Coupon {
	private long couponID; 
	private String title; 
	private Date couponStartDate; 
	private Date couponEndtDate;
	private int amount; 
	private CouponType couponType;
	private String description; 
	private double couponPrice; 
	private String image;
	private long companyId;
	
	public Coupon() {
		super();
	}
	
	public Coupon(long couponID, String title, Date couponStartDate, Date couponEndtDate, int amount,
			CouponType couponType, String description, double couponPrice, String image, long companyId) {
		super();
		this.couponID = couponID;
		this.title = title;
		this.couponStartDate = couponStartDate;
		this.couponEndtDate = couponEndtDate;
		this.amount = amount;
		this.couponType = couponType;
		this.description = description;
		this.couponPrice = couponPrice;
		this.image = image;
		this.companyId = companyId;
	}
	public long getCouponID() {
		return couponID;
	}
	public void setCouponID(long couponID) {
		this.couponID = couponID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getCouponStartDate() {
		return couponStartDate;
	}
	public void setCouponStartDate(Date couponStartDate) {
		this.couponStartDate = couponStartDate;
	}
	public Date getCouponEndtDate() {
		return couponEndtDate;
	}
	public void setCouponEndtDate(Date couponEndtDate) {
		this.couponEndtDate = couponEndtDate;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public CouponType getCouponType() {
		return couponType;
	}
	public void setCouponType(CouponType couponType) {
		this.couponType = couponType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getCouponPrice() {
		return couponPrice;
	}
	public void setCouponPrice(double couponPrice) {
		this.couponPrice = couponPrice;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	@Override
	public String toString() {
		return "Coupon [couponID=" + couponID + ", title=" + title + ", couponStartDate=" + couponStartDate
				+ ", couponEndtDate=" + couponEndtDate + ", amount=" + amount + ", couponType=" + couponType
				+ ", description=" + description + ", couponPrice=" + couponPrice + ", image=" + image + "]";
	} 
	
	
}
