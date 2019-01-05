package exceptions;

public class AuthenticationException extends CouponSystemException {

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(Throwable e) {
		super(e);
	}

}
