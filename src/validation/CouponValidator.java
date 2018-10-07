
package validation;

import entities.Coupon;
import exceptions.CouponValidationException;

public interface CouponValidator {
	public void validate(Coupon coupon) throws CouponValidationException;
}
