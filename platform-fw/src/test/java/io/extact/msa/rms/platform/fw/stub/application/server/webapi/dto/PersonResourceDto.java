package io.extact.msa.rms.platform.fw.stub.application.server.webapi.dto;

import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
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
public class PersonResourceDto {

    @RmsId
    private Integer id;
    @PersonName
    private String name;

    public static PersonResourceDto from(Person entity) {
        if (entity == null) {
            return null;
        }
        var dto = new PersonResourceDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    public Person toEntity() {
        return Person.of(id, name);
    }
}
