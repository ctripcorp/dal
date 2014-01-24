package com.ctrip.sysdev.das;

import java.util.Set;

import com.ctrip.platform.dal.common.util.ReflectionUtil;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

public class GuiceObjectFactory {

	private static final String GUICE_MODEL_PACKAGES = "com.ctrip.sysdev.das.guice";
	private Injector injector;

	public GuiceObjectFactory() {
		Set<Module> modulesInterFaceImpl = ReflectionUtil
				.newInstanceFromPackage(
						System.getProperty("guicemodule", GUICE_MODEL_PACKAGES),
						Module.class);
		Set<AbstractModule> modulesAbstractExtends = ReflectionUtil
				.newInstanceFromPackage(
						System.getProperty("guicemodule", GUICE_MODEL_PACKAGES),
						AbstractModule.class);

		SetView<Module> union = Sets.union(modulesInterFaceImpl,
				modulesAbstractExtends);
		injector = Guice.createInjector(Stage.PRODUCTION,
				union.toArray(new Module[union.size()]));
	}

	public <T> T getInstance(Class<T> clazz) throws Exception {
		return injector.getInstance(clazz);
	}
}
