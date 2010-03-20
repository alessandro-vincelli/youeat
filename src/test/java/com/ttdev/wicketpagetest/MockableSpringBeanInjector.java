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

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.ISpringContextLocator;
import org.apache.wicket.spring.injection.annot.AnnotProxyFieldValueFactory;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Use this class to install an injector that can inject both mocked objects and
 * Spring beans (tried in this order). This is usually done in
 * {@link Application#init()}. Then you can provide mocked objects by calling
 * {@link #mockBean(String, Object)} usually in your unit tests.
 * 
 * @author Kent Tong
 * 
 */
public class MockableSpringBeanInjector extends MockableBeanInjector {
	// It is important that this object doesn't refer to non-serializable
	// objects such as the webapp.
	private static class SpringContextLoader implements ISpringContextLocator {
		private static final long serialVersionUID = 1L;

		public ApplicationContext getSpringContext() {
			return WebApplicationContextUtils
					.getRequiredWebApplicationContext(WebApplication.get()
							.getServletContext());
		}
	}

	public static void installInjector(final WebApplication webapp) {
		MockableBeanInjector.installInjector(webapp, SpringBean.class,
				new AnnotProxyFieldValueFactory(new SpringContextLoader()));
	}

}
