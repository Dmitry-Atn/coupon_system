package exceptions;

public class CouponSystemException extends Exception {
	public CouponSystemException(String message) {
		super(message);
	}

	public CouponSystemException(Throwable e) {
		super(e);
	}
}
