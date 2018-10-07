
package validation;

import entities.Company;
import exceptions.CMSValidationException;
import exceptions.CompanyValidationException;

public class SimpleCompanyValidator implements CompanyValidator{

	@Override
	public void validate(Company company) throws CompanyValidationException{
		try {
			SimpleValidator.validateName(company.getCompanyName());
		} catch (CMSValidationException e) {
			throw new CompanyValidationException(e.getMessage());	
		}
		try {
			SimpleValidator.validateEmail(company.getEmail());
		} catch (CMSValidationException e) {
			throw new CompanyValidationException(e.getMessage());
		}
		try {
			SimpleValidator.validatePassword(company.getPassword());
		} catch (CMSValidationException e) {
			throw new CompanyValidationException(e.getMessage());
		}
	}

}
