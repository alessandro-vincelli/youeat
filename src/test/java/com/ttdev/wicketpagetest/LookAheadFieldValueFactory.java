package com.ttdev.wicketpagetest;

import org.apache.wicket.injection.IFieldValueFactory;

/**
 * This interface is intended to be a mix-in for the {@link IFieldValueFactory}
 * for those who can look ahead to tell if it really has a non-null value for
 * the field.
 * 
 * @author Kent Tong
 * 
 */
public interface LookAheadFieldValueFactory {
	boolean hasValueForField(String fieldName);
}
