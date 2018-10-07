
package validation;

import entities.Customer;
import exceptions.CustomerValidationException;

public interface CustomerValidator {
	public void validate(Customer customer) throws CustomerValidationException;
}
