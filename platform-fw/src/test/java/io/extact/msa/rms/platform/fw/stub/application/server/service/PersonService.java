package io.extact.msa.rms.platform.fw.stub.application.server.service;

import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import org.apache.commons.lang3.StringUtils;

import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.fw.persistence.GenericRepository;
import io.extact.msa.rms.platform.fw.service.GenericService;
import io.extact.msa.rms.platform.fw.stub.application.server.domain.Person;

@Transactional(TxType.REQUIRED)
@ApplicationScoped
public class PersonService implements GenericService<Person> {

    private GenericRepository<Person> repository;

    @Inject
    public PersonService(GenericRepository<Person> repository) {
        this.repository = repository;
    }

    @Override
    public GenericRepository<Person> getRepository() {
        return this.repository;
    }

    @Override
    public Consumer<Person> getDuplicateChecker() {
        return target -> {
            findAll().stream().forEach(saved -> this.checkDuplicate(target, saved));
        };
    }

    private void checkDuplicate(Person target, Person saved) {
        if (target.isSameId(saved)) { // 自分自身
            return;
        }
        if (StringUtils.isEmpty(target.getName()) || StringUtils.isEmpty(saved.getName())) {
            return;
        }
        if (!target.getName().equals(saved.getName())) {
            return;
        }
        throw new BusinessFlowException("The serialNo is already registered.", CauseType.DUPRICATE);
    }
}
