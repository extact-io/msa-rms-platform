package io.extact.msa.rms.platform.fw.stub.application.server.persistence.file;

import java.io.IOException;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import io.extact.msa.rms.platform.fw.persistence.file.io.FileAccessor;
import io.extact.msa.rms.platform.fw.persistence.file.producer.EntityArrayConverter;
import io.extact.msa.rms.platform.fw.persistence.file.producer.FileOpenPathDeriver;
import io.extact.msa.rms.platform.fw.persistence.file.producer.FileRepositoryProducers;
import io.extact.msa.rms.platform.fw.stub.application.server.domain.Person;

@Dependent
public class PersonFileRepositoryProducers implements FileRepositoryProducers<Person> {

    // ファイルパスが定義されている設定ファイルキー(csv.%s.fileName.%s)の2個目の%sの値
    private static final String FILE_NAME_TYPE_CONFIG_KEY = "person";
    private FileOpenPathDeriver pathDeriver;

    @Inject
    public PersonFileRepositoryProducers(FileOpenPathDeriver pathDeriver) {
        this.pathDeriver = pathDeriver;
    }

    @Produces
    public FileAccessor creteFileAccessor() throws IOException {
        return new FileAccessor(pathDeriver.derive(FILE_NAME_TYPE_CONFIG_KEY));
    }

    @Produces
    public EntityArrayConverter<Person> createRentalItemConverter() {
        return PersonalArrayConverter.INSTANCE;
    }
}
