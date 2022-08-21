package io.extact.msa.rms.platform.fw.stub.application.common.dto;

import io.extact.msa.rms.platform.fw.stub.application.server.domain.Person;
import io.extact.msa.rms.platform.fw.stub.application.server.domain.PersonName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter @ToString
public class AddPersonEventDto {
    @PersonName
    private String name;
    public Person toEntity() {
        return Person.ofTransient(name);
    }
}
