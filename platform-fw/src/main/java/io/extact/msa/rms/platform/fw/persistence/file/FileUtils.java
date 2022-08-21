package io.extact.msa.rms.platform.fw.persistence.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.extact.msa.rms.platform.fw.persistence.file.io.IoSystemException;

public class FileUtils {

    public static void deleteDirectoryUnderFiles(String directoryPath) throws Exception {
        var targetDir = Paths.get(directoryPath);
        Files.list(targetDir).forEach(FileUtils::deleteQuietly); // ファイル削除
        Files.deleteIfExists(targetDir); // ディレクトリ削除
    }

    private static boolean deleteQuietly(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }
}
