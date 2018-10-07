
package exceptions;

public class CouponValidationException extends CouponSystemValidationException {

	public CouponValidationException(String message) {
		super(message);
	}

	public CouponValidationException(Throwable e) {
		super(e);
	}

}
