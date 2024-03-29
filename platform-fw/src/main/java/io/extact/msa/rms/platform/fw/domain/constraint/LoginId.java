package io.extact.msa.rms.platform.fw.domain.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ログインIDチェックアノテーション。
 * <pre>
 * ・未入力でないこと
 * ・5桁以上10桁以下であること
 * </pre>
 */
@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@NotNull // 空文字列はSizeで2重にエラーに掛かるのでNotBlankではなくNotNullにしている
@Size(min = 5, max = 10)
public @interface LoginId {
    String message() default "{io.extact.msa.rms.platform.fw.domain.constraint.Generic.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        LoginId[] value();
    }
}
