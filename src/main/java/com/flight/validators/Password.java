/**
 * 
 */
package com.flight.validators;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = PasswordValidatorCustom.class)
@Retention(RUNTIME)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
/**
 * @author Vincent
 *
 */
public @interface Password {
	String message() default "Invalid Password";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}

