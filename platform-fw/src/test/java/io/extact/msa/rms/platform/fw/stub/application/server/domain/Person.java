package io.extact.msa.rms.platform.fw.stub.application.server.domain;

import static jakarta.persistence.AccessType.*;

import jakarta.persistence.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import io.extact.msa.rms.platform.fw.domain.IdProperty;
import io.extact.msa.rms.platform.fw.domain.Transformable;
import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Update;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Access(FIELD)
@Entity
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class Person implements Transformable, IdProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RmsId(groups = Update.class)
    private Integer id;
    @PersonName
    private String name;

    public static Person ofTransient(String name) {
        return Person.of(null, name);
    }
}
