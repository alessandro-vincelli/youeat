package com.ttdev.wicketpagetest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.standard.Fidelity;

import org.apache.wicket.injection.ConfigurableInjector;
import org.apache.wicket.injection.IFieldValueFactory;
import org.apache.wicket.injection.Injector;

/**
 * An {@link ConfigurableInjector} that uses a provided
 * {@link IFieldValueFactory} and clears the target field before injection
 * 
 * @author Kent Tong
 * @author Andy Chu
 */
public class DefaultConfigurableInjector extends ConfigurableInjector {
	private IFieldValueFactory factory;

	public DefaultConfigurableInjector(IFieldValueFactory factory) {
		this.factory = factory;
	}

	@Override
	protected IFieldValueFactory getFieldValueFactory() {
		return factory;
	}

	public Object inject(Object object) {
		try {
			Field[] fields = findFields(object.getClass(), factory);
			for (Field field : fields) {
				clearField(object, field);
			}
			return super.inject(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private Field[] findFields(Class<?> clazz, IFieldValueFactory factory) {
		List<Field> matched = new ArrayList<Field>();
		while (clazz != null && !isBoundaryClass(clazz)) {
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				final Field field = fields[i];
				if (factory.supportsField(field)) {
					matched.add(field);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return matched.toArray(new Field[matched.size()]);
	}

	private void clearField(Object object, Field field) {
		try {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			field.set(object, null);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
