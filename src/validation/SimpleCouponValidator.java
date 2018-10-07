
package validation;

import entities.Coupon;
import exceptions.CMSValidationException;
import exceptions.CouponValidationException;

public class SimpleCouponValidator implements CouponValidator {

	@Override
	public void validate(Coupon coupon) throws CouponValidationException {
		try {
			SimpleValidator.validateName(coupon.getTitle());
		} catch (CMSValidationException e) {
			throw new CouponValidationException(e.getMessage());	
		}
	}

}
