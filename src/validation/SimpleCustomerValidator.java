package validation;

import entities.Customer;
import exceptions.CMSValidationException;
import exceptions.CustomerValidationException;

public class SimpleCustomerValidator implements CustomerValidator {

	@Override
	public void validate(Customer customer) throws CustomerValidationException {
		try {
			SimpleValidator.validateName(customer.getName());
		} catch (CMSValidationException e) {
			throw new CustomerValidationException(e.getMessage());	
		}
		try {
			SimpleValidator.validateEmail(customer.getEmail());
		} catch (CMSValidationException e) {
			throw new CustomerValidationException(e.getMessage());
		}
		try {
			SimpleValidator.validatePassword(customer.getPassword());
		} catch (CMSValidationException e) {
			throw new CustomerValidationException(e.getMessage());
		}

	}

}
