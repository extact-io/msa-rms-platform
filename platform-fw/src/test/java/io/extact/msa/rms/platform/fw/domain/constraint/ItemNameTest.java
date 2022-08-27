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
class ItemNameTest {

    @Test
    void testValidate(Validator validator) {
        // レンタル品(15文字以内)
        var OK= new Data("１２３４５６７８９０１２３４５"); // 境界値:OK
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // レンタル品(15文字より大きい)
        var NG= new Data("1234567890123456"); // 境界値:NG
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("Size.message");
    }

    @AllArgsConstructor
    @Getter @Setter
    static class Data {
        @ItemName
        private String value;
    }
}
