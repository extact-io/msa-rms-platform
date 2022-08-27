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
class SerialNoTest {

    @Test
    void testValidate(Validator validator) {
        var OK= new Data("123456789012345"); // 境界値:OK
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // シリアル番号エラー(null)
        var NG= new Data(null);
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("NotBlank.message");

        // シリアル番号エラー(空文字列)
        NG= new Data("");
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("NotBlank.message");

        // シリアル番号エラー(使用可能文字以外)
        NG= new Data("A_0001");
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("SerialNoCharacter.message");

        // シリアル番号エラー(15文字より大きい)
        NG= new Data("1234567890123456"); // 境界値:NG
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("Size.message");
    }

    @AllArgsConstructor
    @Getter @Setter
    static class Data {
        @SerialNo
        private String value;
    }
}
