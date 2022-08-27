package io.extact.msa.rms.platform.fw.domain.constraint;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.test.assertj.ConstraintViolationSetAssert;
import io.extact.msa.rms.test.junit5.ValidatorParameterExtension;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@ExtendWith(ValidatorParameterExtension.class)
class ReserveEndDateTimeTest {

    @Test
    void testValidate(Validator validator) {
        var OK= new Data(LocalDateTime.now());
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // 利用終了日エラー(null)
        var NG= new Data(null);
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("NotNull.message");
    }

    @AllArgsConstructor
    @Getter @Setter
    static class Data {
        @ReserveEndDateTime
        private LocalDateTime value;
    }
}
