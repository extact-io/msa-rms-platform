package io.extact.msa.rms.platform.fw.stub.auth.server1_server2;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * ClientとSerer間のWebApiインタフェース。
 * ・Server1側ではMicroProfile ReserClientのインターフェースに
 * ・Server2側ではRESTリソースが実装するインタフェースとして
 * 利用する。
 */
public interface ClientServer2Api {
    @GET
    @Path("/not-login")
    @Produces(MediaType.APPLICATION_JSON)
    boolean notLoginApi();

    @GET
    @Path("/member-login")
    @Produces(MediaType.APPLICATION_JSON)
    boolean memberLoginApi();

    @GET
    @Path("/admin-login")
    @Produces(MediaType.APPLICATION_JSON)
    boolean adminLoginApi();
}
