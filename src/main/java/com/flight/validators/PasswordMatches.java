/**
 * 
 */
package com.flight.validators;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Retention(RUNTIME)
@Target({TYPE, ANNOTATION_TYPE})
/**
 * @author Vincent
 *
 */
public @interface PasswordMatches {
	String message() default "Password must match";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
