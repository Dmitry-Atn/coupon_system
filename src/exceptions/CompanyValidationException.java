package exceptions;

public class CompanyValidationException extends CouponSystemValidationException {

	public CompanyValidationException(String message) {
		super(message);
	}

	public CompanyValidationException(Throwable e) {
		super(e);
	}

}
