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

import javax.servlet.ServletContext;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.mortbay.jetty.servlet.FilterHolder;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * To unit test your Wicket pages, you should include this class into your
 * TestNG test suite. Then it will launch Jetty, your webapp and a Selenium
 * client before running any tests in that suite and shut them down afterwards.
 * In your test cases, you can obtain the global Selenium client by
 * {@link #getSelenium()}.
 * <p>
 * Currently, you need to start the Selenium server yourself, probably in a
 * command prompt.
 * <p>
 * If you'd like to customize the behaviors of {@link WebPageTestContext}, you
 * can create a {@link Configuration} object and then pass it to
 * {@link #beforePageTests(Configuration)}. To do that, just create a wrapper
 * class with a {@link BeforeSuite} method and a {@link AfterSuite} method.
 * These methods should call {@link #beforePageTests(Configuration)} and
 * {@link #afterPageTests()} respectively.
 * 
 * @see Configuration
 * 
 * @author Kent Tong
 * 
 */
public class WebPageTestContext {
	private static WebPageTestContext instance;

	private DefaultSelenium selenium;
	private WebAppJettyLauncher jettyLauncher;

	public static void beforePageTests() throws Exception {
		beforePageTests(new Configuration());
	}

	public static void beforePageTests(Configuration cfg) throws Exception {
		instance = new WebPageTestContext();
		instance.start(cfg);
	}

	private void start(Configuration cfg) {
		startWebAppInJetty(cfg);
		startSeleniumClient(cfg);
	}

	private void startWebAppInJetty(Configuration cfg) {
		jettyLauncher = new WebAppJettyLauncher();
		jettyLauncher.startAppInJetty(cfg);
	}

	private void startSeleniumClient(Configuration cfg) {
		String url = String.format("http://localhost:%d/%s", cfg
				.getJettyServerPort(), cfg.getWicketFilterPrefix());
		if (!url.endsWith("/")) {
			url += "/";
		}
		selenium = new DefaultSelenium("localhost",
				cfg.getSeleniumServerPort(), cfg.getSeleniumBrowserLaunchCmd(),
				url);
		selenium.start();
		// So that the test cases can access the app and the injector
		waitForApplication();
		// Need to use the wicketpath attribute instead of wicket:id if it
		// is refreshed by AJAX (see WICKET-2832)
		enableOutputWicketPath();
	}

	private void enableOutputWicketPath() {
		getApplication().getDebugSettings().setOutputComponentPath(true);
	}

	private void waitForApplication() {
		for (;;) {
			WebApplication app = getApplication();
			if (app != null) {
				Application.set(app);
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void afterPageTests() throws Exception {
		instance.stop();
	}

	private void stop() {
		stopSeleniumClient();
		stopJetty();
	}

	private void stopJetty() {
		if (jettyLauncher != null) {
			jettyLauncher.stopJetty();
		}
	}

	private void stopSeleniumClient() {
		if (selenium != null) {
			selenium.stop();
		}
	}

	public WebApplication getApplication() {
		ServletContext servletContext = jettyLauncher.getServletContext();
		return (WebApplication) servletContext.getAttribute("wicket:"
				+ getWicketFilterName());
	}

	private String getWicketFilterName() {
		for (FilterHolder h : jettyLauncher.getFilters()) {
			if (h.getHeldClass().equals(WicketFilter.class)) {
				return h.getName();
			}
		}
		throw new RuntimeException("WicketFilter not found");
	}

	public static DefaultSelenium getSelenium() {
		return instance.selenium;
	}

	public static WebApplication getWebApplication() {
		return instance.getApplication();
	}

}
