package io.extact.msa.rms.test.utils;

import java.lang.reflect.Field;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;


/**
 * プロパティのsetter/getterのテストを行うテストクラスの基底クラス
 */
public abstract class PropertyTest {

    /**
     * テストのセットアップ。
     * テスト対象クラスの{@link Field}を解析する
     * @throws Exception エラーが発生した場合
     */
    @BeforeEach
    protected void setUp() throws Exception {
        TestUtils.inspectFieldToCache(this.getTargetClass());
    }

    /**
     * テスト対象クラスを取得する。
     * @return テスト対象クラス
     */
    protected abstract Class<?> getTargetClass();

    /**
     * 引数でしたテスト対象クラスのフィードを取得する。
     * @param fieldName フィールド名
     * @return フィールドインスタンス
     * @see #getTargetClass()
     */
    protected Field getField(String fieldName) {
        return TestUtils.getField(this.getTargetClass(), fieldName);
    }

    /**
     * テスト対象クラスのフィールドに付与されているアノテーションをすべて取得する
     *
     * @return アノテーション
     */
    protected Set<Class<?>> getFieldAnnotations() {
        return TestUtils.getFieldAnnotations(this.getTargetClass());
    }
}
