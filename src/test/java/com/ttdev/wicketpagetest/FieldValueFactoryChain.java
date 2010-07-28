package com.ttdev.wicketpagetest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.IFieldValueFactory;

/**
 * An {@link IFieldValueFactory} that delegates to a chain of
 * {@link IFieldValueFactory}s. The first one that is applicable is used.
 * 
 * @author Kent Tong
 * 
 */
public class FieldValueFactoryChain implements IFieldValueFactory,
		LookAheadFieldValueFactory {
	private List<IFieldValueFactory> factories;

	public FieldValueFactoryChain() {
		factories = new ArrayList<IFieldValueFactory>();
	}

	public void addFactory(IFieldValueFactory factory) {
		factories.add(factory);
	}

	public List<IFieldValueFactory> getFactories() {
		return factories;
	}

	public Object getFieldValue(Field field, Object fieldOwner) {
		for (IFieldValueFactory factory : factories) {
			if (factory.supportsField(field)) {
				Object value = factory.getFieldValue(field, fieldOwner);
				if (value != null) {
					return value;
				}
			}
		}
		return null;
	}

	public boolean supportsField(Field field) {
		for (IFieldValueFactory factory : factories) {
			if (factory.supportsField(field)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasValueForField(String fieldName) {
		for (IFieldValueFactory factory : factories) {
			if (factory instanceof LookAheadFieldValueFactory) {
				LookAheadFieldValueFactory overrider = (LookAheadFieldValueFactory) factory;
				if (overrider.hasValueForField(fieldName)) {
					return true;
				}
			}
		}
		return false;
	}

}
