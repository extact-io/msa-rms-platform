package io.extact.msa.rms.platform.fw.exception.webapi;

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
public class ValidationErrorInfoImpl extends GenericErrorInfo implements ValidationErrorInfo {

    private List<ValidationErrorItemImpl> errorItems;

    public ValidationErrorInfoImpl(String errorReason, String errorMessage, List<ValidationErrorItemImpl> errorItems) {
        super(errorReason, errorMessage);
        this.errorItems = errorItems;
    }

    @Override
    public List<ValidationErrorItem> getErrorItems() {
        return errorItems.stream()
                .map(i -> (ValidationErrorItem) i)
                .toList();
    }

    public void setErrorItems(List<ValidationErrorItemImpl> errorItems) {
        this.errorItems = errorItems;
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
}

