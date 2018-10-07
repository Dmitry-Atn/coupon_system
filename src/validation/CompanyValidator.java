
package validation;

import entities.Company;
import exceptions.CompanyValidationException;

public interface CompanyValidator {
	public void validate(Company company) throws CompanyValidationException;
}
