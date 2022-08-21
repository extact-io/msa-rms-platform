package io.extact.msa.rms.platform.fw.stub.application.server.persistence.file;

import io.extact.msa.rms.platform.fw.exception.RmsSystemException;
import io.extact.msa.rms.platform.fw.persistence.file.producer.EntityArrayConverter;
import io.extact.msa.rms.platform.fw.stub.application.server.domain.Person;

public class PersonalArrayConverter implements EntityArrayConverter<Person> {

    public static final PersonalArrayConverter INSTANCE = new PersonalArrayConverter();

    @Override
    public Person toEntity(String[] attributes) throws RmsSystemException {
        var id = Integer.parseInt(attributes[0]);
        var name = attributes[1];
        return Person.of(id, name);
    }

    @Override
    public String[] toArray(Person person) {
        var attributes = new String[3];
        attributes[0] = String.valueOf(person.getId());
        attributes[1] = person.getName();
        return attributes;
    }
}
