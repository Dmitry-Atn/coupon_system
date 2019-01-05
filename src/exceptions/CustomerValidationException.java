package exceptions;

public class CustomerValidationException extends CouponSystemValidationException {

	public CustomerValidationException(String message) {
		super(message);
	}

	public CustomerValidationException(Throwable e) {
		super(e);
	}

}
