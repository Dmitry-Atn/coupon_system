
package facades;

import exceptions.CouponSystemException;

public interface CouponClientFacade {
	public CouponClientFacade login(String email, String password) throws CouponSystemException;
}
