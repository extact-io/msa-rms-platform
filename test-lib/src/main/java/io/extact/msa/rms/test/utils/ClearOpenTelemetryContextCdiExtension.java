package io.extact.msa.rms.test.utils;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Extension;

import io.helidon.microprofile.cdi.RuntimeStart;
import io.opentelemetry.api.GlobalOpenTelemetry;

/**
 * OpenTelemetryのGlobalコンテキストを強制的にクリアするCDI Extension。
 * @HelidonTestのコンテナ起動ごとにTracingCdiExtensionでContextが設定されて
 * エラーが発生するのでコンテナ起動時に強制的に先にContextをクリアする。
 */
public class ClearOpenTelemetryContextCdiExtension implements Extension {
    void observeRuntimeStart(@Observes @RuntimeStart Object event) {
        GlobalOpenTelemetry.resetForTest();
    }
}
