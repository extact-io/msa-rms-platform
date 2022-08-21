package io.extact.msa.rms.platform.fw.it;

import static io.extact.msa.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import javax.inject.Inject;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.fw.exception.RmsValidationException;
import io.extact.msa.rms.platform.fw.stub.application.client.external.PersonApi;
import io.extact.msa.rms.platform.fw.stub.application.client.external.bridge.PersaonApiRestBridge;
import io.extact.msa.rms.platform.fw.stub.application.client.external.restclient.PersonApiRestClient;
import io.extact.msa.rms.platform.fw.stub.application.common.dto.AddPersonEventDto;
import io.extact.msa.rms.platform.fw.stub.application.common.dto.PersonResourceDto;
import io.extact.msa.rms.platform.fw.stub.application.server.persistence.file.PersonFileRepository;
import io.extact.msa.rms.platform.fw.stub.application.server.persistence.file.PersonFileRepositoryProducers;
import io.extact.msa.rms.platform.fw.stub.application.server.persistence.jpa.PersonJpaRepository;
import io.extact.msa.rms.platform.fw.stub.application.server.service.PersonService;
import io.extact.msa.rms.platform.fw.stub.application.server.webapi.PersonApplication;
import io.extact.msa.rms.platform.fw.stub.application.server.webapi.PersonResource;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

/**
 * スタブのPersonアプリを使ってplatform.fwクラスをテストする。
 * <pre>
 * ・スタブアプリ：CID Bean(PersonApi)
 * ・スタブアプリ：CID Bean(PersaonApiRestBridge)
 * ・スタブアプリ：RestClient(PersonApiRestClient)
 *     ↓ HTTP
 * ・スタブアプリ：RestResource(PersonApplication)
 * ・スタブアプリ：CID Bean(PersonService)
 * ・スタブアプリ：CID Bean(PersonJpaRepository) or (PersonFileRepository)
 * ※ JPAかFileかどちらの実装を使うかはこのクラスのサブクラスで決定
 * </pre>
 */
@HelidonTest
// for RESTResrouce Beans
@AddBean(PersonResource.class)
@AddBean(PersonApplication.class)
@AddBean(PersonService.class)
@AddBean(PersonJpaRepository.class)
@AddBean(PersonFileRepository.class)
@AddBean(PersonFileRepositoryProducers.class)
@AddConfig(key = "jwt.filter.enable", value = "false") // 認証認可OFF
@AddConfig(key = "server.port", value = "7001") // for PersonResource Server port
//for RESTClient Beans
@AddBean(PersaonApiRestBridge.class)
@AddBean(PersonApiRestClient.class)
@AddConfig(key = "configuredCdi.register.0.class", value = "io.extact.msa.rms.platform.core.jwt.client.PropagateLoginClientHeadersFactory")
@AddConfig(key = "web-api/mp-rest/url", value = "http://localhost:7001") // for REST Client
@ExtendWith(JulToSLF4DelegateExtension.class)
@TestMethodOrder(OrderAnnotation.class)
abstract class AbstractApplicationIntegrationTest {

    @Inject
    private PersonApi api;

    @Test
    @Order(1)
    void testGet() {
        var expected = PersonResourceDto.of(1, "name1");
        var actual = api.get(1);
        assertThat(actual).isPresent();
        assertThatToString(actual.get()).isEqualTo(expected);

        actual = api.get(999);
        assertThat(actual).isNotPresent();
    }

    @Test
    @Order(2)
    void testGetAll() {
        var actual = api.getAll();
        assertThat(actual).hasSize(4);
    }

    @Test
    @Order(3)
    void testUpdate() {
        var expected = PersonResourceDto.of(4, "UP");
        var actual = api.update(PersonResourceDto.of(4, "UP"));
        assertThatToString(actual).isEqualTo(expected);
    }

    @Test
    @Order(4)
    void testUpdateOnValidationError() {
        var thrown = catchThrowable(() -> api.update(PersonResourceDto.of(4, "123456"))); // 5文字より大きい
        assertThat(thrown).isInstanceOf(RmsValidationException.class);
    }

    @Test
    @Order(5)
    void testUpdateOnDuplicateError() {
        var thrown = catchThrowable(() -> api.update(PersonResourceDto.of(2, "name3")));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.DUPRICATE);
    }

    @Test
    @Order(6)
    void testUpdateOnNotFound() {
        var thrown = catchThrowable(() -> api.update(PersonResourceDto.of(999, "UP")));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    @Order(7)
    void testAdd() {
        var expected = PersonResourceDto.of(5, "ADD");
        var actual = api.add(AddPersonEventDto.of("ADD"));
        assertThatToString(actual).isEqualTo(expected);
    }

    @Test
    @Order(8)
    void testAddOnValidationError() {
        var thrown = catchThrowable(() -> api.add(AddPersonEventDto.of("123456"))); // 5文字より大きい
        assertThat(thrown).isInstanceOf(RmsValidationException.class);
    }

    @Test
    @Order(9)
    void testAddOnDuplicateError() {
        var thrown = catchThrowable(() -> api.add(AddPersonEventDto.of("name3")));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.DUPRICATE);
    }

    @Test
    @Order(10)
    void testDelete() {
        var expected = api.getAll().size() - 1;
        api.delete(1);
        var actual = api.getAll().size();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Order(11)
    void testDeleteOnNotFound() {
        var thrown = catchThrowable(() -> api.delete(999));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }
}
