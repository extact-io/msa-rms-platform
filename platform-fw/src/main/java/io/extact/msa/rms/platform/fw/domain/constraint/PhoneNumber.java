package io.extact.msa.rms.platform.fw.domain.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 電話番号チェックアノテーション。
 * <pre>
 * ・半角数字ハイフンであること
 * ・14文字以内であること
 * </pre>
 */
@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Pattern(regexp = "[0-9\\-]*", message = "{io.extact.msa.rms.user.domain.constraint.PhoneNumberCharacter.message}")
@Size(max = 14)
public @interface PhoneNumber {

    String message() default "{io.extact.msa.rms.platform.fw.domain.constraint.Generic.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        PhoneNumber[] value();
    }
}
