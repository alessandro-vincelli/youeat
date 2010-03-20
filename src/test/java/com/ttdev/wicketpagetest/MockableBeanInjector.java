package com.ttdev.wicketpagetest;

import java.lang.annotation.Annotation;

import org.apache.wicket.Application;
import org.apache.wicket.injection.ComponentInjector;
import org.apache.wicket.injection.IFieldValueFactory;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Use this class to install an injector that can inject mocked objects before
 * considering the real beans. This is usually done in
 * {@link Application#init()}. Then you can provide mocked objects by calling
 * {@link #mockBean(String, Object)} usually in your unit tests.
 * 
 * @author Kent Tong
 * 
 */
public class MockableBeanInjector {
	/**
	 * Install an injector that has a chain of two {@link IFieldValueFactory}'s.
	 * The first one will inject mocked objects. The second is the original
	 * factory to be used in production (e.g., to look up Spring beans).
	 * 
	 * @param webapp
	 * @param injectionAnnotClass
	 *            The {@link IFieldValueFactory}'s will handle fields with this
	 *            annotation. Pass null if the real beans are not injected using
	 *            a {@link IFieldValueFactory}, which is the case for Guice
	 *            beans.
	 * @param originalFactory
	 */
	public static void installInjector(final WebApplication webapp,
			Class<? extends Annotation> injectionAnnotClass,
			IFieldValueFactory originalFactory) {
		FieldValueFactoryChain factoryChain = new FieldValueFactoryChain();
		MockedBeanFieldValueFactory mockedBeansFactory = new MockedBeanFieldValueFactory(
				injectionAnnotClass);
		factoryChain.addFactory(mockedBeansFactory);
		if (originalFactory != null) {
			factoryChain.addFactory(originalFactory);
		}
		DefaultConfigurableInjector injector = new DefaultConfigurableInjector(
				factoryChain);
		InjectorHolder.setInjector(injector);
		webapp.addComponentInstantiationListener(new ComponentInjector());
	}

	private static MockedBeanFieldValueFactory getMockedBeanFactory() {
		DefaultConfigurableInjector injector = (DefaultConfigurableInjector) InjectorHolder
				.getInjector();
		FieldValueFactoryChain factoryChain = (FieldValueFactoryChain) injector
				.getFieldValueFactory();
		for (IFieldValueFactory factory : factoryChain.getFactories()) {
			if (factory instanceof MockedBeanFieldValueFactory) {
				MockedBeanFieldValueFactory mockedBeanFactory = (MockedBeanFieldValueFactory) factory;
				return mockedBeanFactory;
			}
		}
		throw new IllegalStateException(
				"Mocked bean field value factory not found");
	}

	public static void mockBean(String fieldName, Object mockedBean) {
		getMockedBeanFactory().mockBean(fieldName, mockedBean);
	}

	public static void clearMockBeans() {
		getMockedBeanFactory().clearMockedBeans();
	}
}
