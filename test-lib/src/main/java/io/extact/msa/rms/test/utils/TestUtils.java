package io.extact.msa.rms.test.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * テストケースで利用するユーティルクラス。
 */
public class TestUtils {

    // ----------------------------------------------------- field cache methods

    /** フィールドキャッシュ */
    private static Map<Class<?>, Map<String, Field>> fieldCache = new HashMap<>();

    /**
     * 引数で渡されたクラスのフィールドを解析しキャッシュする。
     *
     * @param clazz 解析対象クラス
     */
    public static synchronized void inspectFieldToCache(Class<?> clazz) {
        if (fieldCache.containsKey(clazz)) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        Map<String, Field> fieldMap = Stream.of(fields).collect(Collectors.toMap(Field::getName, f -> f));
        fieldCache.put(clazz, fieldMap);
    }

    /**
     * 引数で指定されたフィールドをキャッシュから取得する。
     *
     * @param clazz フィールドを取得する対象のクラス
     * @param fieldName 取得するフィールド名
     * @return フィールド。該当がない場合は<code>null</code>を返す
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        return fieldCache.get(clazz).get(fieldName);
    }

    /**
     * フィールドキャッシュをクリアする。
     */
    public static synchronized void clearFieldCache() {
        fieldCache.clear();
    }

    /**
     * 指定されたクラスのフィールドに付与されているアノテーションをすべて取得する
     *
     * @param clazz アノテーションを取得する対象のクラス
     * @return アノテーション
     */
    public static Set<Class<?>> getFieldAnnotations(Class<?> clazz) {
        Map<String, Field> fieldMap = fieldCache.get(clazz);
        var fields = fieldMap.values();
        return fields.stream()
                .map(Field::getAnnotations)
                .flatMap(Stream::of)
                .map(Annotation::annotationType)
                .collect(Collectors.toSet());
    }

    // ----------------------------------------------------- field accessor methods

    /**
     * フィールドに値を設定する。
     *
     * @param target 値を設定するオブジェクト
     * @param fieldName フィール名
     * @param value 設定する値
     */
    public static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = getNamedField(target, fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * フィールドの値を取得する。
     *
     * @param target 値を取得するオブジェクト
     * @param fieldName フィールド名
     * @return フィールドの値
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String fieldName) {
        try {
            Field field = getNamedField(target, fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * フィールドオブジェクトを取得する。
     *
     * @param target フィールドを取得するオブジェクト
     * @param fieldName フィールド名
     * @return フィールドオブジェクト
     * @throws Exception エラーが発生した場合
     */
    public static Field getNamedField(Object target, String fieldName) throws Exception {
        return target.getClass().getDeclaredField(fieldName);
    }
}
