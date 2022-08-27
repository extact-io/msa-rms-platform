package io.extact.msa.rms.platform.fw.stub.application.server.persistence.file;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;
import io.extact.msa.rms.platform.fw.persistence.GenericRepository.ApiType;
import io.extact.msa.rms.platform.fw.persistence.file.AbstractFileRepository;
import io.extact.msa.rms.platform.fw.persistence.file.io.FileAccessor;
import io.extact.msa.rms.platform.fw.persistence.file.producer.EntityArrayConverter;
import io.extact.msa.rms.platform.fw.stub.application.server.domain.Person;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.FILE)
public class PersonFileRepository extends AbstractFileRepository<Person> {

    @Inject
    public PersonFileRepository(FileAccessor fileAccessor, EntityArrayConverter<Person> converter) {
        super(fileAccessor, converter);
    }
}
