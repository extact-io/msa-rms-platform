package io.extact.msa.rms.platform.fw.persistence.file.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.StreamSupport;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.config.ConfigProvider;

import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;
import io.extact.msa.rms.platform.fw.persistence.GenericRepository.ApiType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitFilePreparatorExtension implements Extension {

    private static final String PROP_PREFIX = "rms.persistence";

    /**
     * ApiTypeがFILEのBeanが存在する場合に初期データの投入処理を行うイベントハンドラ
     * <p>
     * @param event イベント
     */
    public void onItinitDataIfAbsent(
            @Observes @WithAnnotations(EnabledIfRuntimeConfig.class) ProcessAnnotatedType<?> event) {
        EnabledIfRuntimeConfig annotation = event.getAnnotatedType().getAnnotation(EnabledIfRuntimeConfig.class);
        if (annotation.propertyName().equals(ApiType.PROP_NAME) && annotation.value().equals(ApiType.FILE)) {
            initPermanentDataIfAbsent();
        }
    }

    /**
     * fileTypeがpermanentだがマスタデータ格納フォルダが存在しない場合に初期データを投入する。
     * <code>rms.persistence.csv.type.permanent.init.data</code>で初期データフォルダが指定されている場合はそのデータを投入、
     * 指定されていない場合は<code>rms.persistence.csv.temporary.fileName</code>に指定されているリソースファイルを投入する。
     */
    private void initPermanentDataIfAbsent() {
        var config = ConfigProvider.getConfig();
        if (config.getValue(PROP_PREFIX + ".csv.type", String.class).equals("temporary")) {
            return;
        }

        PathResolver pathEnv = new PathResolver.FixedDirPathResolver();
        if (Files.exists(pathEnv.getBaseDir())) {
            return;
        }

        String initDataPath = config
                .getOptionalValue(PROP_PREFIX + ".csv.type.permanent.init.data", String.class)
                .orElse(null);
        if (initDataPath != null) {
            var from = Paths.get(initDataPath);
            RecursiveCopyCommand.from(from).to(pathEnv.getBaseDir()).copy();
            log.info("初期データを作成しました。" + from + "=>" + pathEnv.getBaseDir());
            return;
        }

        // csv.temporary.fileName.$1 のvalueに指定されているリソースファイルを読み込み
        // csv.permanent.fileName.$1 のvalueのファイル名でデータ格納フォルダに出力する
        String temporaryFileConfigPrefix = PROP_PREFIX + ".csv.temporary.fileName";
        StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                .filter(configKey -> configKey.startsWith(temporaryFileConfigPrefix))
                .map(configKey -> {
                    var fileNameInConfigKey = configKey.substring((temporaryFileConfigPrefix + ".").length()); // csv.temporary.fileName.$1の$1部分を取得
                    var resoucePath = config.getValue(configKey, String.class);
                    var outputFileName = config
                            .getValue(PROP_PREFIX + ".csv.permanent.fileName." + fileNameInConfigKey, String.class);
                    return Pair.of(resoucePath, outputFileName);
                })
                .forEach(fileNames -> FileAccessor.copyResourceToRealPath(fileNames.getLeft(), pathEnv,
                        fileNames.getRight()));
        log.info("初期データを作成しました。${" + PROP_PREFIX + ".csv.type.temporary.fileName}" + "=>"
                        + pathEnv.getBaseDir());
    }

    /**
     * ディレクトリをfromからtoにコピーする。
     * サブディレクトリの中身も全てコピーする<br>
     * コピー先が存在する場合は例外を送出する<br>
     */
    static class RecursiveCopyCommand {

        private Path from;
        private Path to;

        private RecursiveCopyCommand() {
            // nop
        }

        public static RecursiveCopyCommand from(Path from) {
            var command = new RecursiveCopyCommand();
            command.from = from;
            return command;
        }

        public RecursiveCopyCommandFinsher to(Path to) {
            this.to = to;
            return new RecursiveCopyCommandFinsher();
        }

        public class RecursiveCopyCommandFinsher {

            public void copy() {
                //コピー元
                final Path fromPath = from;
                //コピー先
                final Path toPath = to;

                //FileVisitorの定義
                FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        //ディレクトリをコピーする
                        Files.copy(dir, toPath.resolve(fromPath.relativize(dir)), StandardCopyOption.COPY_ATTRIBUTES);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        //ファイルをコピーする
                        Files.copy(file, toPath.resolve(fromPath.relativize(file)), StandardCopyOption.COPY_ATTRIBUTES);
                        return FileVisitResult.CONTINUE;
                    }
                };

                //ファイルツリーを辿ってFileVisitorの動作をさせる
                try {
                    Files.walkFileTree(fromPath, visitor);
                } catch (IOException e) {
                    throw new IoSystemException(e);
                }
            }
        }
    }
}
