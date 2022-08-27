package io.extact.msa.rms.platform.test;


import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.fw.webapi.GenericErrorInfo;
import io.extact.msa.rms.platform.fw.webapi.ValidationErrorInfoImpl;

/**
 * Platformモジュール関連のテストユーティルクラス。
 */
public class PlatformTestUtils {

    /**
     * ファイルパスで指定されたファイルの全レコードを取得する。
     *
     * @param filePath ファイルパス
     * @return 全レコード
     * @throws IOException エラーが発生した場合
     */
    public static List<String[]> getAllRecords(Path filePath) throws IOException {
        try (CSVParser parser = CSVParser.parse(filePath, StandardCharsets.UTF_8, CSVFormat.RFC4180)) {
            return parser.getRecords().stream()
                    .map(record -> StreamSupport.stream(record.spliterator(), false).toList())
                    .map(values -> {
                        var array = new String[values.size()];
                        values.toArray(array);
                        return array;
                    })
                    .toList();
        }
    }

    public static void assertGenericErrorInfo(Throwable thrown, Status expectedStatus, Class<? extends BusinessFlowException> expectedCauseClass, CauseType causeType) {
        assertThat(thrown).isInstanceOf(WebApplicationException.class);
        WebApplicationException actual = (WebApplicationException) thrown;
        assertThat(actual.getResponse().getStatus()).isEqualTo(expectedStatus.getStatusCode());
        assertThat(actual.getResponse().getHeaderString("Rms-Exception")).isEqualTo(expectedCauseClass.getSimpleName());

        GenericErrorInfo errorInfo = actual.getResponse().readEntity(GenericErrorInfo.class);
        assertThat(errorInfo.getErrorMessage()).isNotEmpty();
        assertThat(errorInfo.getErrorReason()).isEqualTo(causeType.name());
    }

    public static void assertValidationErrorInfo(Throwable thrown, int expectedErrorSize) {
        assertThat(thrown).isInstanceOf(WebApplicationException.class);
        WebApplicationException actual = (WebApplicationException) thrown;
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        assertThat(actual.getResponse().getHeaderString("Rms-Exception")).isEqualTo(ConstraintViolationException.class.getSimpleName());

        ValidationErrorInfoImpl errorInfo = actual.getResponse().readEntity(ValidationErrorInfoImpl.class);
        assertThat(errorInfo.getErrorMessage()).isNotEmpty();
        assertThat(errorInfo.getErrorReason()).isEqualTo(ConstraintViolationException.class.getSimpleName());
        assertThat(errorInfo.getErrorItems()).hasSize(expectedErrorSize);
    }
}
