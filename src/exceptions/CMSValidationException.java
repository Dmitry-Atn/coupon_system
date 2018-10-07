
package exceptions;

public class CMSValidationException extends CouponSystemException {

	public CMSValidationException(String message) {
		super(message);
	}

	public CMSValidationException(Throwable e) {
		super(e);
	}

}
