package io.extact.msa.rms.platform.fw.stub.application.common;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import io.extact.msa.rms.platform.fw.stub.application.common.dto.AddPersonEventDto;
import io.extact.msa.rms.platform.fw.stub.application.common.dto.PersonResourceDto;

/**
 * ClientとSerer間のWebApiインタフェース。
 * ・Client側ではMicroProfile ReserClientのインターフェースに
 * ・Server側ではRESTリソースが実装するインタフェースとして
 * 利用する。
 */
public interface ClientServerPersonApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonResourceDto> getAll();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonResourceDto get(@RmsId @PathParam("id") Integer id);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PersonResourceDto add(@Valid AddPersonEventDto dto);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PersonResourceDto update(@Valid PersonResourceDto dto);

    @DELETE
    @Path("/{id}")
    void delete(@RmsId @PathParam("id") Integer itemId);
}
