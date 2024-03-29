package io.extact.msa.rms.platform.fw.stub.application.server.webapi;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;

import io.extact.msa.rms.platform.core.validate.ValidateParam;
import io.extact.msa.rms.platform.fw.stub.application.common.ClientServerPersonApi;
import io.extact.msa.rms.platform.fw.stub.application.common.dto.AddPersonEventDto;
import io.extact.msa.rms.platform.fw.stub.application.common.dto.PersonResourceDto;
import io.extact.msa.rms.platform.fw.stub.application.server.service.PersonService;

@Path("/persons")
@ApplicationScoped
@ValidateParam
public class PersonResource implements ClientServerPersonApi {

    private PersonService service;

    @Inject
    public PersonResource(PersonService service) {
        this.service = service;
    }

    @Override
    public List<PersonResourceDto> getAll() {
        return service.findAll().stream()
                .map(PersonResourceDto::from)
                .toList();
    }

    @Override
    public PersonResourceDto get(Integer itemId) {
        return service.get(itemId)
                .map(PersonResourceDto::from)
                .orElse(null);
    }

    @Override
    public PersonResourceDto add(@Valid AddPersonEventDto dto) {
        return service.add(dto.toEntity())
                .transform(PersonResourceDto::from);
    }

    @Override
    public PersonResourceDto update(@Valid PersonResourceDto dto) {
        return service.update(dto.toEntity())
                .transform(PersonResourceDto::from);
    }

    @Override
    public void delete(Integer itemId) {
        service.delete(itemId);
    }
}
