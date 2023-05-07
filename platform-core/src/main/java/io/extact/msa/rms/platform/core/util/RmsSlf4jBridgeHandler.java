package io.extact.msa.rms.platform.core.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.spi.LocationAwareLogger;

public class RmsSlf4jBridgeHandler extends SLF4JBridgeHandler {
    
    // The caller is java.util.logging.Logger
    private static final String FQCN = java.util.logging.Logger.class.getName();
    
    private static final int TRACE_LEVEL_THRESHOLD = Level.FINEST.intValue();
    private static final int DEBUG_LEVEL_THRESHOLD = Level.FINE.intValue();
    private static final int INFO_LEVEL_THRESHOLD = Level.INFO.intValue();
    private static final int WARN_LEVEL_THRESHOLD = Level.WARNING.intValue();

    public static void installToJul() {
        // SLF4Jに委譲するJULのLoggerブリッジを設定
        java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger(""); 
        rootLogger.addHandler(new RmsSlf4jBridgeHandler());

        // SLF4Jにすべてのメッセージが渡るようにRootLoggerのログレベルをALLに設定
        // -- 注意 ---
        // Logger#setLevelを明示的にしなかった場合、LoggerのログレベルはハードコードされたJULの
        // デフォルトログレベルのINFOとなり、SLF4JにはINFO未満のログが委譲されないので注意すること
        // -----------
        rootLogger.setLevel(Level.ALL);
    }

    protected void callPlainSLF4JLogger(Logger slf4jLogger, LogRecord record) {
        String i18nMessage = getMessageI18N(record);
        int julLevelValue = record.getLevel().intValue();
        if (julLevelValue <= TRACE_LEVEL_THRESHOLD) {
            slf4jLogger.trace(i18nMessage, record.getThrown());
        } else if (julLevelValue <= DEBUG_LEVEL_THRESHOLD 
                || julLevelValue == Level.CONFIG.intValue()) { // CONFIGはDEBUGにする
            slf4jLogger.debug(i18nMessage, record.getThrown());
        } else if (julLevelValue <= INFO_LEVEL_THRESHOLD) {
            slf4jLogger.info(i18nMessage, record.getThrown());
        } else if (julLevelValue <= WARN_LEVEL_THRESHOLD) {
            slf4jLogger.warn(i18nMessage, record.getThrown());
        } else {
            slf4jLogger.error(i18nMessage, record.getThrown());
        }
    }
    
    protected void callLocationAwareLogger(LocationAwareLogger lal, LogRecord record) {
        int julLevelValue = record.getLevel().intValue();
        int slf4jLevel;

        if (julLevelValue <= TRACE_LEVEL_THRESHOLD) {
            slf4jLevel = LocationAwareLogger.TRACE_INT;
        } else if (julLevelValue <= DEBUG_LEVEL_THRESHOLD
                || julLevelValue == Level.CONFIG.intValue()) { // CONFIGはDEBUGにする
            slf4jLevel = LocationAwareLogger.DEBUG_INT;
        } else if (julLevelValue <= INFO_LEVEL_THRESHOLD) {
            slf4jLevel = LocationAwareLogger.INFO_INT;
        } else if (julLevelValue <= WARN_LEVEL_THRESHOLD) {
            slf4jLevel = LocationAwareLogger.WARN_INT;
        } else {
            slf4jLevel = LocationAwareLogger.ERROR_INT;
        }
        String i18nMessage = getMessageI18N(record);
        lal.log(null, FQCN, slf4jLevel, i18nMessage, null, record.getThrown());
    }


    // copy from SLF4JBridgeHandler
    private String getMessageI18N(LogRecord record) {
        String message = record.getMessage();

        if (message == null) {
            return null;
        }

        ResourceBundle bundle = record.getResourceBundle();
        if (bundle != null) {
            try {
                message = bundle.getString(message);
            } catch (MissingResourceException e) {
            }
        }
        Object[] params = record.getParameters();
        // avoid formatting when there are no or 0 parameters. see also
        // http://jira.qos.ch/browse/SLF4J-203
        if (params != null && params.length > 0) {
            try {
                message = MessageFormat.format(message, params);
            } catch (IllegalArgumentException e) {
                // default to the same behavior as in java.util.logging.Formatter.formatMessage(LogRecord)
                // see also http://jira.qos.ch/browse/SLF4J-337
                return message;
            }
        }
        return message;
    }

}
