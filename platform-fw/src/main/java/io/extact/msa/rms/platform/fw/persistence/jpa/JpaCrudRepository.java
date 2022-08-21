package io.extact.msa.rms.platform.fw.persistence.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import io.extact.msa.rms.platform.fw.domain.IdProperty;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Update;
import io.extact.msa.rms.platform.fw.persistence.GenericRepository;
import io.extact.msa.rms.platform.core.validate.ValidateGroup;
import io.extact.msa.rms.platform.core.validate.ValidateParam;

public interface JpaCrudRepository<T extends IdProperty> extends GenericRepository<T> {

    @Override
    default T get(int id) {
        return this.getEntityManage().find(this.getTargetClass(), id);
    }

    @Override
    default List<T> findAll() {
        var jpql = "select e from " + this.getTargetClass().getSimpleName() + " e order by e.id";
        return this.getEntityManage().createQuery(jpql, this.getTargetClass())
                .getResultList();
    }

    @ValidateParam
    @ValidateGroup(groups = Add.class)
    @Override
    default void add(@Valid T entity) {
        this.getEntityManage().persist(entity);
        this.getEntityManage().flush();
    }

    @ValidateParam
    @ValidateGroup(groups = Update.class)
    @Override
    default T update(@Valid T entity) {
        if (!this.getEntityManage().contains(entity) && get(entity.getId()) == null) {
            return null;
        }
        var updated = this.getEntityManage().merge(entity);
        this.getEntityManage().flush();
        return updated;
    }

    @Override
    default void delete(T entity) {
        this.getEntityManage().remove(entity);
        this.getEntityManage().flush();
    }

    EntityManager getEntityManage();

    Class<T> getTargetClass();
}
