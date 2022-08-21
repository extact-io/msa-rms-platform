package io.extact.msa.rms.platform.fw.webapi;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.extact.msa.rms.platform.fw.exception.RmsValidationException.ValidationErrorInfo;
import io.extact.msa.rms.platform.fw.exception.RmsValidationException.ValidationErrorItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "パラメータチェックエラー情報")
@NoArgsConstructor // for JSON Deserialize
@Getter @Setter
public class ValidationErrorInfoImpl extends GenericErrorInfo implements ValidationErrorInfo {

    private List<ValidationErrorItemImpl> errorItemImpls;

    public ValidationErrorInfoImpl(String errorReason, String errorMessage, List<ValidationErrorItemImpl> errorItems) {
        super(errorReason, errorMessage);
        this.errorItemImpls = errorItems;
    }

    // ----------------------------------------------------- inner classes

    @Schema(description = "1件ごとのチェックエラー情報")
    @NoArgsConstructor // for JSON Deserialize
    @AllArgsConstructor
    @Getter @Setter
    public static class ValidationErrorItemImpl implements ValidationErrorItem {

        @Schema(description = "エラーとなった項目")
        private String fieldName;

        @Schema(description = "エラーメッセージ")
        private String message;
    }

    @Override
    public List<ValidationErrorItem> getErrorItems() {
        return errorItemImpls.stream()
                .map(i -> (ValidationErrorItem) i)
                .toList();
    }
}

