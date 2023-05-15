package io.extact.msa.rms.platform.fw.webapi.openapi;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASModelReader;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;

import io.extact.msa.rms.platform.core.env.Environment;
import io.extact.msa.rms.platform.fw.exception.webapi.GenericErrorInfo;
import io.extact.msa.rms.platform.fw.exception.webapi.ValidationErrorInfoImpl;

public class CommonOpenApiModelReader implements OASModelReader {

    @Override
    public OpenAPI buildModel() {

        var components = OASFactory.createComponents()
                .addResponse("NoContent", OASFactory.createAPIResponse()
                        .description("該当データなしの場合。1件取得で戻り値にnullを返すことが妥当な場合は正常のため404ではなくボディなしの204を返す"))
                .addResponse("NotFound", OASFactory.createAPIResponse()
                        .description("該当データがない場合")
                        .addHeader("Rms-Exception", createExceptionHeader())
                        .content(createErrorInfoContent(GenericErrorInfo.class)))
                .addResponse("ServiceUnavailable", OASFactory.createAPIResponse()
                        .description("バックエンドのサービスが停止している")
                        .addHeader("Rms-Exception", createExceptionHeader("発生例外のRmsSystemExceptionが設定される"))
                        .content(createErrorInfoContent(GenericErrorInfo.class)))
                .addResponse("ServerError", OASFactory.createAPIResponse()
                        .description("アプリケーション内部でエラーが発生した場合")
                        .addHeader("Rms-Exception", createExceptionHeader("発生例外のRmsSystemExceptionが設定される"))
                        .content(createErrorInfoContent(GenericErrorInfo.class)))
                .addResponse("Forbidden", OASFactory.createAPIResponse()
                        .description("対象データに対する操作権限がない場合")
                        .addHeader("Rms-Exception", createExceptionHeader())
                        .content(createErrorInfoContent(GenericErrorInfo.class)))
                .addResponse("UnknownData", OASFactory.createAPIResponse()
                        .description("処理対象データが存在しない場合")
                        .addHeader("Rms-Exception", createExceptionHeader())
                        .content(createErrorInfoContent(GenericErrorInfo.class)))
                .addResponse("DataDupricate", OASFactory.createAPIResponse()
                        .description("登録データが既に登録されている")
                        .addHeader("Rms-Exception", createExceptionHeader())
                        .content(createErrorInfoContent(GenericErrorInfo.class)))
                .addResponse("DataRefered", OASFactory.createAPIResponse()
                        .description("操作対象を参照するデータが存在する")
                        .addHeader("Rms-Exception", createExceptionHeader())
                        .content(createErrorInfoContent(GenericErrorInfo.class)))
                .addResponse("ParameterError", OASFactory.createAPIResponse()
                        .description("パラメータエラーの場合")
                        .addHeader("Rms-Exception", createExceptionHeader("発生例外のConstraintViolationExceptionが設定される"))
                        .content(createErrorInfoContent(ValidationErrorInfoImpl.class)));

        OpenAPI openAPI = OASFactory.createOpenAPI();
        openAPI.info(createInfo());
        openAPI.components(components);

        return openAPI;
    }

    private Info createInfo() {
        var info = CDI.current().select(InfoConfig.class, ConfigProperties.Literal.NO_PREFIX).get();
        return OASFactory.createInfo()
                .title(info.title)
                .version(Environment.getMainJarInfo().getVersion())
                .contact(OASFactory.createContact()
                        .name(info.name)
                        .url(info.url));
    }

    private Header createExceptionHeader() {
        return createExceptionHeader("発生例外のBusinessFlowExceptionが設定される");
    }

    private Header createExceptionHeader(String desc) {
        return OASFactory.createHeader()
                .description(desc)
                .required(true)
                .schema(OASFactory.createSchema().type(SchemaType.STRING));
    }

    private Content createErrorInfoContent(Class<?> errorInfoClass) {
        return OASFactory.createContent()
                .addMediaType("application/json", OASFactory.createMediaType()
                        .schema(OASFactory.createSchema()
                                .ref("#/components/schemas/" + errorInfoClass.getSimpleName())));
    }

    @ConfigProperties(prefix = "rms.openapi")
    @Dependent
    public static class InfoConfig {
        private String title;
        @ConfigProperty(name = "contact.name")
        private String name;
        @ConfigProperty(name = "contact.url")
        private String url;
    }
}