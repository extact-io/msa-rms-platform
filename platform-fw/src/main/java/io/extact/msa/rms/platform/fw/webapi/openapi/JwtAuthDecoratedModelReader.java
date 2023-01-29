package io.extact.msa.rms.platform.fw.webapi.openapi;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASModelReader;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme.In;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme.Type;

public class JwtAuthDecoratedModelReader implements OASModelReader {

    @Override
    public OpenAPI buildModel() {
        OpenAPI opneApi = new CommonOpenApiModelReader().buildModel();
        var components = opneApi.getComponents()
                .addSecurityScheme("RmsJwtAuth", createRmsJwtAuth());
        opneApi.setComponents(components);
        return opneApi;
    }

    private SecurityScheme createRmsJwtAuth() {
        return OASFactory.createSecurityScheme()
                .description("認証と認可はMicroProfile JWT RBAC Securityの仕様をもとに行い認証エラーの場合は401を認可エラーの場合は403を返す")
                .type(Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(In.HEADER);
    }
}