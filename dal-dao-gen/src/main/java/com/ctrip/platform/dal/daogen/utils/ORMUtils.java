package com.ctrip.platform.dal.daogen.utils;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;

/**
 * Auto-ORM using reflection.
 * Note the filed name must be same as the defined column name in database.
 *
 * @author wcyuan
 */
public class ORMUtils {
    public static <T> T map(ResultSet rs, Class<T> clazz) throws Exception {
        Constructor<T> constructor = clazz.getConstructor();
        T instance = constructor.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        Field field = null;
        Method setMethod = null;
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            setMethod = clazz.getMethod("set" + StringUtils.capitalize(field.getName()), field.getType());

            if (setMethod == null) {
                field.setAccessible(true);
                field.set(instance, load(field.getType(), rs.getObject(field.getName())));
            } else {
                try {
                    setMethod.invoke(instance, load(field.getType(), rs.getObject(field.getName())));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    System.out.println("Name: " + field.getName());
                }
            }

        }
        return instance;
    }

    private static Object load(Class<?> clz, Object val) {
        if (val == null)
            return null;
        if (clz.equals(Integer.class) || clz.equals(int.class)) {
            return ((Number) val).intValue();
        }
        if (clz.equals(Long.class) || clz.equals(long.class)) {
            return ((Number) val).longValue();
        }
        if (clz.equals(Short.class) || clz.equals(short.class)) {
            return ((Number) val).shortValue();
        }
        if (clz.equals(Float.class) || clz.equals(float.class)) {
            return ((Number) val).floatValue();
        }
        if (clz.equals(Double.class) || clz.equals(double.class)) {
            return ((Number) val).doubleValue();
        }

        return val;
    }
}
