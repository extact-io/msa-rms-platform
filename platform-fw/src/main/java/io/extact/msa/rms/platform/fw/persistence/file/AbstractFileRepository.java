package io.extact.msa.rms.platform.fw.persistence.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import io.extact.msa.rms.platform.fw.domain.IdProperty;
import io.extact.msa.rms.platform.fw.domain.Transformable;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Update;
import io.extact.msa.rms.platform.fw.persistence.GenericRepository;
import io.extact.msa.rms.platform.fw.persistence.file.io.FileAccessor;
import io.extact.msa.rms.platform.fw.persistence.file.io.IoSystemException;
import io.extact.msa.rms.platform.fw.persistence.file.producer.EntityArrayConverter;
import io.extact.msa.rms.platform.core.validate.ValidateGroup;
import io.extact.msa.rms.platform.core.validate.ValidateParam;

public class AbstractFileRepository<T extends Transformable & IdProperty> implements GenericRepository<T>, FileRepository {

    private FileAccessor fileAccessor;
    private EntityArrayConverter<T> entityConverter;


    // ----------------------------------------------------- constructor methods

    public AbstractFileRepository(FileAccessor fileAccessor, EntityArrayConverter<T> entityConverter) {
        this.fileAccessor = fileAccessor;
        this.entityConverter = entityConverter;
    }


    // ----------------------------------------------------- implement methods

    @Override
    public T get(int id) {
        return load().stream()
                .filter(items -> Integer.parseInt(items[0]) == id) // numberはpos:0は共通
                .map(entityConverter::toEntity)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<T> findAll() {
        return load().stream()
                .map(entityConverter::toEntity)
                .toList();
    }

    @ValidateParam
    @ValidateGroup(groups = Add.class)
    @Override
    public void add(@Valid T entity) {
        var nextSeq = this.getNextSequence();
        entity.setId(nextSeq);
        save(entity.transform(entityConverter::toArray));
    }

    @ValidateParam
    @ValidateGroup(groups = Update.class)
    public T update(@Valid T entity) {
        var replaced = new AtomicBoolean(false);
        var lines = load().stream()
                .map(items -> {
                    if (items[0].equals(String.valueOf(entity.getId()))) {
                        replaced.set(true);
                        return getConverter().toArray(entity);
                    }
                    return items;
                })
                .toList();
        if (!replaced.get()) {
            return null;
        }
        this.saveAll(lines);
        return entity;
    }

    public void delete(T entity) {
        this.delete(entity.getId());
    }

    @Override
    public Path getStoragePath() {
        return fileAccessor.getFilePath();
    }


    // ----------------------------------------------------- specific methods

    public int getNextSequence() {
        return load().stream()
                .map(items -> Integer.parseInt(items[0]))
                .collect(Collectors.maxBy(Integer::compareTo))
                .orElse(0)
                + 1;
    }

    public void delete(Integer id) {
        var excludedData = load().stream()
                .filter(items -> Integer.parseInt(items[0])  != id) // numberはpos:0は共通
                .toList();
        saveAll(excludedData);
    }



    protected EntityArrayConverter<T> getConverter() {
        return entityConverter;
    }

    protected List<String[]> load() {
        try {
            List<String[]> dataList = new ArrayList<>();
            fileAccessor.load(dataList);
            return dataList;
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

    // ----------------------------------------------------- package private methods

    void save(String[] arrayData) {
        try {
            fileAccessor.save(arrayData);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

    void saveAll(List<String[]> allData) {
        try {
            fileAccessor.saveAll(allData);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }
}
