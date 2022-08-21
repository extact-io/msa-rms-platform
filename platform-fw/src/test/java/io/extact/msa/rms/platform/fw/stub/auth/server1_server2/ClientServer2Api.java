package io.extact.msa.rms.platform.fw.stub.auth.server1_server2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
