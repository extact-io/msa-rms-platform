package io.extact.msa.rms.platform.fw.stub.application.common;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
