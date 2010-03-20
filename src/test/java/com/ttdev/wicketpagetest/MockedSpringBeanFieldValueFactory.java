/**
 * Copyright (C) 2009 Kent Tong <freemant2000@yahoo.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * Free Software Foundation version 3.
 *
 * program is distributed in the hope that it will be useful,
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ttdev.wicketpagetest;

import org.apache.wicket.injection.IFieldValueFactory;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * An {@link IFieldValueFactory} that provides mocked objects to
 * {@link SpringBean} annotated fields.
 * 
 * @author Kent Tong
 * 
 */
public class MockedSpringBeanFieldValueFactory extends
		MockedBeanFieldValueFactory {
	private static final long serialVersionUID = 1L;

	public MockedSpringBeanFieldValueFactory() {
		super(SpringBean.class);
	}
}
