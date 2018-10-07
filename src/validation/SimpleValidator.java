
package validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.CMSValidationException;

public class SimpleValidator {
	public static void validateName(String name) throws CMSValidationException{
		if (name.isEmpty() || name == null)
			throw new CMSValidationException("name couldn't be empty");
		if (name.length() < 2 || name.length() > 255)
			throw new CMSValidationException("name length must be from 3 to 255 characters");
	}
	
	public static void validateEmail(String email) throws CMSValidationException{
		final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile(EMAIL_REGEX);
		matcher = pattern.matcher(email);
		if (!matcher.matches())
			throw new CMSValidationException("email is not valid");
	}
	
	public static void validatePassword(String password) throws CMSValidationException{
		//only letters digits and !@#... symbols permitted
		final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile(PASSWORD_REGEX);
		matcher = pattern.matcher(password);
		if (!matcher.matches())
			throw new CMSValidationException("password is not valid");
	}
	
	
	
	
}
