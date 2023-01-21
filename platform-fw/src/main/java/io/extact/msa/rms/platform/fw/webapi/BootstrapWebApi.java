package io.extact.msa.rms.platform.fw.webapi;

import java.util.logging.LogManager;

import org.slf4j.bridge.SLF4JBridgeHandler;

import io.extact.msa.rms.platform.core.env.Environment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BootstrapWebApi {

    public static void start(String[] args) throws Exception {
        try {
            startContainer(args);
            startupLog();
        } catch (Exception e) {
            log.error("startup failed.", e);
            throw e;
        }
    }

    private static void startContainer(String[] args) throws Exception {
        // java.util.loggingの出力をSLF4Jへdelegate
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        io.helidon.microprofile.cdi.Main.main(args);
    }

    private static void startupLog() {
        var mainJarInfo = Environment.getMainJarInfo();
        log.info("Main Jar Information=>" + System.lineSeparator() +
                "\tStartup-Module:" + mainJarInfo.startupModuleInfo() + System.lineSeparator() +
                "\tVersion:" + mainJarInfo.getVersion() + System.lineSeparator() +
                "\tBuild-Time:" + mainJarInfo.getBuildtimeInfo()
                );
    }
}
