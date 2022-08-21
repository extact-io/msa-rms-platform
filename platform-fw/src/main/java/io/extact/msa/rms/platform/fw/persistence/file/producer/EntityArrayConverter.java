package io.extact.msa.rms.platform.fw.persistence.file.producer;

import io.extact.msa.rms.platform.fw.exception.RmsSystemException;

public interface EntityArrayConverter<T> {

    T toEntity(String[] attributes) throws RmsSystemException;

    String[] toArray(T entity);
}
