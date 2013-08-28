package com.ctrip.sysdev.das.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReflectionUtil.class);
	private static final Class<?>[] EMPTY_ARRAY = new Class[] {};
	private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap<Class<?>, Constructor<?>>();

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> theClass) {
		T result;
		try {
			Constructor<T> method = (Constructor<T>) CONSTRUCTOR_CACHE
					.get(theClass);
			if (method == null) {
				method = theClass.getDeclaredConstructor(EMPTY_ARRAY);
				method.setAccessible(true);
				CONSTRUCTOR_CACHE.put(theClass, method);
			}
			result = method.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T newInstance(String theClass) {
		T result;
		try {
			Class clazz = Class.forName(theClass);
			Constructor method = clazz.getDeclaredConstructor(EMPTY_ARRAY);
			method.setAccessible(true);
			result = (T) method.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public static <T> Set<Class<? extends T>> scannerSubTypeFromPackage(
			String packageName, Class<T> subType) {
		Reflections reflections = new Reflections(packageName);
		return reflections.getSubTypesOf(subType);
	}

	public static <T> Set<T> newInstanceFromPackage(String packageName,
			Class<T> subType) {
		Set<Class<? extends T>> classSet = (Set<Class<? extends T>>) scannerSubTypeFromPackage(
				packageName, subType);
		Set<T> instanceSet = new HashSet<T>();
		if (classSet == null || classSet.size() <= 0) {
			return instanceSet;
		}
		for (Class<? extends T> tClass : classSet) {
			try {
				instanceSet.add(tClass.newInstance());
			} catch (Exception e) {
				LOGGER.warn("Cannot new an instance for " + tClass.getName(), e);
			}
		}
		return instanceSet;
	}

	@SuppressWarnings({ "rawtypes" })
	public static Class getSuperClassGenericType(Class clazz) {
		Type type = clazz.getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
		} else if (type instanceof Class) {
			return getSuperClassGenericType((Class) type);
		} else {
			return Object.class;
		}
	}
}
