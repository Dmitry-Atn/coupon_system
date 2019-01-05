package exceptions;

public class EntityNotExistException extends CouponSystemException {

	public EntityNotExistException(String message) {
		super(message);
	}

	public EntityNotExistException(Throwable e) {
		super(e);
	}

}
