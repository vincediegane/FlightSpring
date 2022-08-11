/**
 * 
 */
package com.flight.validators;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.passay.AlphabeticalSequenceRule;
import org.passay.DigitCharacterRule;
import org.passay.LengthRule;
import org.passay.NumericalSequenceRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.QwertySequenceRule;
import org.passay.RuleResult;
import org.passay.SpecialCharacterRule;
import org.passay.UppercaseCharacterRule;
import org.passay.WhitespaceRule;

import com.google.common.base.Joiner;

/**
 * @author Vincent
 *
 */
public class PasswordValidatorCustom implements ConstraintValidator<Password, String>{

	public PasswordValidatorCustom() {
		super();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		PasswordValidator validator = new PasswordValidator(Arrays.asList(
				new LengthRule(7, 35),
				new UppercaseCharacterRule(1),
				new DigitCharacterRule(1),
				new SpecialCharacterRule(1),
				new NumericalSequenceRule(3, false),
				new AlphabeticalSequenceRule(3, false),
				new QwertySequenceRule(3, false),
				new WhitespaceRule()
			)
		);
		final RuleResult result = validator.validate(new PasswordData(value));
		if(result.isValid()) return true;
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(Joiner.on(",").join(validator.getMessages(result))).addConstraintViolation();
		return false;
	}

}
