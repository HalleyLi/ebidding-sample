package xyz.ebidding.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CusipValidator.class)
public @interface Cusip {
    Class<?>[] groups() default {};
    String message() default "length of CUSIP should be exactly 9";
    Class<? extends Payload>[] payload() default {};
}