package core;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import dao.CouponDAO;
import dao.CouponDBDAO;
import entities.Coupon;
import exceptions.CouponSystemException;

public class DailyCouponExpirationTask implements Runnable {
	
	private CouponDAO couponDAO;
	
	public DailyCouponExpirationTask() {
		couponDAO = new CouponDBDAO();
	}

	@Override
	public void run(){
		while(true) {
			//remove old coupons to prepare system to work at its startup
			removeOldCoupons(new Timestamp(System.currentTimeMillis()));
			
			// today
			Calendar midnight = new GregorianCalendar();
			
			// reset hour, minutes, seconds and milliseconds
			midnight.set(Calendar.HOUR_OF_DAY, 0);
			midnight.set(Calendar.MINUTE, 0);
			midnight.set(Calendar.SECOND, 0);
			midnight.set(Calendar.MILLISECOND, 0);
			
			// next day
			midnight.add(Calendar.DAY_OF_MONTH, 1);
			
			//calculate time to the next midnight
			Timestamp midnight_ts = new Timestamp(midnight.getTimeInMillis());
			Timestamp current = new Timestamp(System.currentTimeMillis());
			System.out.println(current.getTime());
			System.out.println(midnight.getTimeInMillis());
			System.out.println((midnight.getTimeInMillis()-current.getTime())/3600);
			try {
				TimeUnit.MILLISECONDS.sleep(midnight.getTimeInMillis()-current.getTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			removeOldCoupons(midnight_ts);
		}
	}
	
	private boolean removeOldCoupons(Timestamp midnight) {
		try {
			Collection<Coupon> allCoupons = couponDAO.getAllCoupons();
			for (Coupon coupon : allCoupons) {
				if (!coupon.getCouponEndtDate().before(midnight)) {
					couponDAO.removeCoupon(coupon.getCouponID());
				}
			} 
		} catch (CouponSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
