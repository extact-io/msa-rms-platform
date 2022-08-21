package io.extact.msa.rms.platform.fw.domain.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDateTime;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidator;

@Documented
@Constraint(validatedBy = { BeforeAfterDateTimeValidator.class })
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface BeforeAfterDateTime {

    String message() default "{io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String from() default "start datetime";

    String to() default "end datetime";

    @Target({ TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        BeforeAfterDateTime[] value();
    }

    public static class BeforeAfterDateTimeValidator implements ConstraintValidator<BeforeAfterDateTime, BeforeAfterDateTimeValidatable> {
        public boolean isValid(BeforeAfterDateTimeValidatable bean, ConstraintValidatorContext context) {
            if (bean.getStartDateTime() == null || bean.getEndDateTime() == null) {
                return true; // チェックしない
            }
            return bean.getStartDateTime().isBefore(bean.getEndDateTime());
        }
    }

    public interface BeforeAfterDateTimeValidatable {
        public LocalDateTime getStartDateTime();
        public LocalDateTime getEndDateTime();
    }
}
