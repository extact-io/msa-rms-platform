package io.extact.msa.rms.platform.fw.stub.application.client.external.bridge;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.platform.fw.stub.application.client.external.PersonApi;
import io.extact.msa.rms.platform.fw.stub.application.client.external.restclient.PersonApiRestClient;
import io.extact.msa.rms.platform.fw.stub.application.common.dto.AddPersonEventDto;
import io.extact.msa.rms.platform.fw.stub.application.common.dto.PersonResourceDto;

@ApplicationScoped
public class PersaonApiRestBridge implements PersonApi {

    private PersonApiRestClient client;

    @Inject
    public PersaonApiRestBridge(@RestClient PersonApiRestClient client) {
        this.client = client;
    }

    @Override
    public List<PersonResourceDto> getAll() {
        return client.getAll();
    }

    @Override
    public Optional<PersonResourceDto> get(int itemId) {
        return Optional.ofNullable(client.get(itemId));
    }

    @Override
    public PersonResourceDto add(AddPersonEventDto dto) {
        return client.add(dto);
    }

    @Override
    public PersonResourceDto update(PersonResourceDto dto) {
        return client.update(dto);
    }

    @Override
    public void delete(int itemId) {
        client.delete(itemId);
    }
}
