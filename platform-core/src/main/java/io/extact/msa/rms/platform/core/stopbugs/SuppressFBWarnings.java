package io.extact.msa.rms.platform.core.stopbugs;

public @interface SuppressFBWarnings {

    String[] value() default {};

    String justification() default "";
}
