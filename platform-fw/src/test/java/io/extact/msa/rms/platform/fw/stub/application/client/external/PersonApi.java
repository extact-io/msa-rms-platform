package io.extact.msa.rms.platform.fw.stub.application.client.external;

import java.util.List;
import java.util.Optional;

import io.extact.msa.rms.platform.fw.stub.application.common.dto.AddPersonEventDto;
import io.extact.msa.rms.platform.fw.stub.application.common.dto.PersonResourceDto;

public interface PersonApi {
    List<PersonResourceDto> getAll();

    Optional<PersonResourceDto> get(int id);

    PersonResourceDto add(AddPersonEventDto dto);

    PersonResourceDto update(PersonResourceDto dto);

    void delete(int id);
}
