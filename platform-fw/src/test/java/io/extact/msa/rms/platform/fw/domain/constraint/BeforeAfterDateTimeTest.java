package io.extact.msa.rms.platform.fw.domain.constraint;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import io.extact.msa.rms.test.assertj.ConstraintViolationSetAssert;
import io.extact.msa.rms.test.junit5.ValidatorParameterExtension;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@ExtendWith(ValidatorParameterExtension.class)
class BeforeAfterDateTimeTest {

    @Test
    void testValidate(Validator validator) {
        var OK = new Data(LocalDateTime.now().minusHours(1), LocalDateTime.now());
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        var NG = new Data(LocalDateTime.now(), LocalDateTime.now().minusHours(1));
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasMessageEndingWith("BeforeAfterDateTime.message");
    }

    @AllArgsConstructor
    @Getter @Setter
    @BeforeAfterDateTime(from = "利用開始日時", to = "利用終了日時")
    static class Data implements BeforeAfterDateTimeValidatable {
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
    }
}
