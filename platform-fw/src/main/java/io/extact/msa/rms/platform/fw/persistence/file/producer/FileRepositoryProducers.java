package io.extact.msa.rms.platform.fw.persistence.file.producer;

import java.io.IOException;

import io.extact.msa.rms.platform.fw.persistence.file.io.FileAccessor;

public interface FileRepositoryProducers<T> {
    FileAccessor creteFileAccessor() throws IOException;
    EntityArrayConverter<T> createRentalItemConverter();
}
