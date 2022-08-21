package io.extact.msa.rms.platform.fw.stub.auth.client_sever1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.extact.msa.rms.platform.fw.domain.constraint.LoginId;
import io.extact.msa.rms.platform.fw.domain.constraint.Passowrd;

/**
 * ClientとSerer間のWebApiインタフェース。
 * ・Client側ではMicroProfile ReserClientのインターフェースに
 * ・Server側ではRESTリソースが実装するインタフェースとして
 * 利用する。
 */
public interface ClientServer1Api {
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    AuthData authenticate(@LoginId @QueryParam("loginId") String loginId,
            @Passowrd @QueryParam("password") String password);

    @GET
    @Path("/member")
    @Produces(MediaType.APPLICATION_JSON)
    boolean memeberApi();

    @GET
    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    boolean adminApi();

    @GET
    @Path("/guest")
    @Produces(MediaType.APPLICATION_JSON)
    boolean guestApi();

    @GET
    @Path("/guest-with-login")
    @Produces(MediaType.APPLICATION_JSON)
    boolean guestApiWithLogin();
}
