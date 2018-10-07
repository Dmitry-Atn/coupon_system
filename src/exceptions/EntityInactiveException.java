
package exceptions;

public class EntityInactiveException extends CouponSystemException {

	public EntityInactiveException(String message) {
		super(message);
	}

	public EntityInactiveException(Throwable e) {
		super(e);
	}

}
