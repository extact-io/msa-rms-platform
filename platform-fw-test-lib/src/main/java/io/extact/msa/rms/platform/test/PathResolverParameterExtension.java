package io.extact.msa.rms.platform.test;

import java.lang.annotation.Inherited;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import io.extact.msa.rms.platform.fw.persistence.file.io.PathResolver;

/**
 * テストクラスのメソッド引数で{@link PathResolver}を指定可能するJUnit5拡張クラス実装。
 * {@link PathResolver}の実装には{@link PathResolver.TempDirPathResolver}インスタンスを返す。
 */
public class PathResolverParameterExtension implements ParameterResolver {
    /**
     * {@link Inherited}e
     */
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == PathResolver.class;
    }
    /**
     * {@link Inherited}e
     */
    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return new PathResolver.TempDirPathResolver();
    }
}
