package exceptions;

public class CouponSystemValidationException extends CouponSystemException {

	public CouponSystemValidationException(String message) {
		super(message);
	}

	public CouponSystemValidationException(Throwable e) {
		super(e);
	}

}
