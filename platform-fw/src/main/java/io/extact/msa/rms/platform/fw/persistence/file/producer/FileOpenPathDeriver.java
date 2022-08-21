package io.extact.msa.rms.platform.fw.persistence.file.producer;

import java.nio.file.Path;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;

import io.extact.msa.rms.platform.fw.persistence.file.io.FileAccessor;
import io.extact.msa.rms.platform.fw.persistence.file.io.PathResolver;
import lombok.extern.slf4j.Slf4j;

@Dependent
@Slf4j
public class FileOpenPathDeriver {

    // config value is "permanent" or "temporary"
    private static final String FILE_TYPE_CONFIG_KEY = "csv.type";
    // samaple key is "csv-file.permanent.file_name.reservation = reservation.csv"
    private static final String FILE_NAME_CONFIG_KEY_FORMAT = "csv.%s.fileName.%s";

    private Config config;

    @Inject
    public FileOpenPathDeriver(Config config) {
        this.config = config;
    }

    public Path derive(String fileNameTypeConfigKey) {
        // 1%sの文字列取得
        var fileType = config.getValue(FILE_TYPE_CONFIG_KEY, String.class);

        // ファイル名のConfigKeyの決定
        var fileNameConfigKey = String.format(FILE_NAME_CONFIG_KEY_FORMAT, fileType, fileNameTypeConfigKey);
        var fileName = config.getValue(fileNameConfigKey, String.class);

        // フィルパスの取得
        Path filePath = switch (fileType) {
            case "permanent" -> new PathResolver.FixedDirPathResolver().resolve(fileName);
            case "temporary" -> FileAccessor.copyResourceToRealPath(fileName, new PathResolver.TempDirPathResolver());
            default -> throw new IllegalArgumentException("unknown fileType -> " + fileType);
        };

        log.info("[{}]モードのファイルオープンするパスを導出。PATH={}", fileType, filePath);
        return filePath;
    }
}
