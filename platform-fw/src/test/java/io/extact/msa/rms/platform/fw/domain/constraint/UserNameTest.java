package io.extact.msa.rms.platform.fw.domain.constraint;

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
class UserNameTest {

    @Test
    void testValidate(Validator validator) {
        var OK= new Data("レンタル太郎");
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // ユーザ名(null)
        var NG= new Data(null);
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("NotBlank.message");
    }

    @AllArgsConstructor
    @Getter @Setter
    static class Data {
        @UserName
        private String value;
    }
}
