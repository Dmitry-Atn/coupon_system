
package exceptions;

public class NonUniqueDataException extends CouponSystemException {

	public NonUniqueDataException(String message) {
		super(message);
	}

	public NonUniqueDataException(Throwable e) {
		super(e);
	}
	

}
