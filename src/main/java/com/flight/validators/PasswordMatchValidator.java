/**
 * 
 */
package com.flight.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.flight.dto.AccountDto;

/**
 * @author Vincent
 *
 */
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatches, Object> {

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		AccountDto accountDto = (AccountDto) value;
		return accountDto.getPassword().equals(accountDto.getConfPassword());
	}

}
