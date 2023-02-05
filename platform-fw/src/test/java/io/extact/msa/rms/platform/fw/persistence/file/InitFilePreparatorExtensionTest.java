package io.extact.msa.rms.platform.fw.persistence.file;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;
import io.extact.msa.rms.platform.fw.persistence.GenericRepository.ApiType;
import io.extact.msa.rms.platform.fw.persistence.file.InitFilePreparatorExtensionTest.InitFilePreparatorNotifire;
import io.extact.msa.rms.platform.fw.persistence.file.io.IoSystemException;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

/**
 * CDIコンテナ起動時にInitFilePreparatorExtensionにより行われる以下を確認するテスト。
 * ・InitFilePreparatorExtensionによりApiTypeがFILEのBeanが存在する場合で、かつ<br>
 * ・fileTypeがpermanentだがマスタデータ格納フォルダが存在しない場合に初期データが投入される
 */
@HelidonTest
@AddConfig(key = "rms.persistence.apiType", value = "file")
@AddConfig(key = "rms.persistence.csv.type", value = "permanent")
@AddConfig(key = "rms.persistence.csv.type.permanent.init.data", value = "./target/test-classes/preparetor-test")
@AddConfig(key = "rms.persistence.csv.permanent.directory", value = InitFilePreparatorExtensionTest.TEST_PERMANENT_DIR)
@AddBean(InitFilePreparatorNotifire.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
class InitFilePreparatorExtensionTest {

    public static final String TEST_PERMANENT_DIR = "./target/temp-integrationtest";

    @AfterAll
    static void teardownAfterAll() throws Exception {
        var targetDir = Paths.get(TEST_PERMANENT_DIR);
        Files.list(targetDir).forEach(InitFilePreparatorExtensionTest::deleteQuietly); // ファイル削除
        Files.deleteIfExists(targetDir); // ディレクトリ削除
    }

    @Test
    void testAssertCopyFiles() throws Exception {

        var expected = List.of(
                "InitFilePreparatorExtensionTest1.txt",
                "InitFilePreparatorExtensionTest2.txt"
                );

        List<String> fileNames = Files.list(Paths.get(TEST_PERMANENT_DIR))
                .map(path -> path.getFileName().toString())
                .toList();

        assertThat(fileNames).containsExactlyInAnyOrderElementsOf(expected);
    }

    static boolean deleteQuietly(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

    // ApiTypeがFILEのBeanを定義してInitFilePreparatorExtensionを起動させる
    @EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.FILE)
    static class InitFilePreparatorNotifire {
    }
}
