package io.extact.msa.rms.platform.fw.webapi.openapi;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASModelReader;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme.In;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme.Type;

public class HeaderAuthDecoratedModelReader implements OASModelReader {

    @Override
    public OpenAPI buildModel() {

        OpenAPI opneApi = new CommonOpenApiModelReader().buildModel();
        var components = opneApi.getComponents()
                .addSecurityScheme("RmsHeaderAuthn", createRmsHeaderAuthn())
                .addSecurityScheme("RmsHeaderAuthz", createRmsHeaderAuthz());

        opneApi.setComponents(components);

        return opneApi;
    }

    private SecurityScheme createRmsHeaderAuthn() {
        return OASFactory.createSecurityScheme()
                .description("閉域ネットワーク内での利用を前提に認証情報をヘッダーで連携する")
                .type(Type.APIKEY)
                .in(In.HEADER)
                .name("rms-userId");
    }

    private SecurityScheme createRmsHeaderAuthz() {
        return OASFactory.createSecurityScheme()
                .description("閉域ネットワーク内での利用を前提に認可情報をヘッダーで連携する")
                .type(Type.APIKEY)
                .in(In.HEADER)
                .name("rms-roles");
    }
}