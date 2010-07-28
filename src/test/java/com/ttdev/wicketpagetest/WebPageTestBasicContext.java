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

import org.apache.wicket.protocol.http.WebApplication;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * To unit test your Wicket pages, before running the tests, you should call
 * {@link #beforePageTests()}. Then it will launch Jetty, your webapp and a
 * Selenium client before running any tests in that suite. To shut them down
 * afterwards, run {@link #afterPageTests()}.
 * 
 * If you're using TestNG, use the {@link WebPageTestContext} subclass.
 * 
 * In your test cases, you can obtain the global Selenium client by
 * {@link #getSelenium()}.
 * <p>
 * Currently, you need to start the Selenium server yourself, probably in a
 * command prompt.
 * <p>
 * If you'd like to customize the behaviors of {@link WebPageTestBasicContext},
 * you can create a {@link Configuration} object and then pass it to
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
public class WebPageTestBasicContext {
	private static WebPageTestBasicContext instance;

	private DefaultSelenium selenium;
	private WicketAppJettyLauncher jettyLauncher;
	private SeleniumServerProcessLauncher seleniumServerlauncher;
	private Configuration cfg;

	public static void beforePageTests() throws Exception {
		beforePageTests(new Configuration());
	}

	public static void beforePageTests(Configuration cfg) throws Exception {
		instance = new WebPageTestBasicContext();
		instance.start(cfg);
	}

	private void start(Configuration cfg) {
		this.cfg = cfg;
		startWebAppInJetty();
		if (!connectToSeleniumServer()) {
			startSeleniumServer();
		}
	}

	private void startSeleniumServer() {
		seleniumServerlauncher = new SeleniumServerProcessLauncher();
		seleniumServerlauncher.start(cfg);
		waitForSeleniumServer(3000L);
	}

	private void startWebAppInJetty() {
		jettyLauncher = new WicketAppJettyLauncher();
		jettyLauncher.startAppInJetty(cfg);
	}

	private void waitForSeleniumServer(long timeout) {
		long startTime = System.currentTimeMillis();
		for (;;) {
			if (connectToSeleniumServer()) {
				return;
			}
			if (System.currentTimeMillis() - startTime > timeout) {
				throw new RuntimeException(
						"Did you start the Selenium server or specify its path in <home>/wpt.properties?");
			}
		}
	}

	private boolean connectToSeleniumServer() {
		String url = String.format("http://localhost:%d/%s", cfg
				.getJettyServerPort(), cfg.getWicketFilterPrefix());
		if (!url.endsWith("/")) {
			url += "/";
		}
		try {
			selenium = new DefaultSelenium("localhost", cfg
					.getSeleniumServerPort(),
					cfg.getSeleniumBrowserLaunchCmd(), url);
			selenium.start();
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	public static void afterPageTests() throws Exception {
		instance.stop();
	}

	private void stop() {
		stopSelenium();
		stopJetty();
	}

	private void stopJetty() {
		if (jettyLauncher != null) {
			jettyLauncher.stopJetty();
		}
	}

	private void stopSelenium() {
		if (seleniumServerlauncher != null && cfg.isKillSeleniumAtEnd()) {
			selenium.shutDownSeleniumServer();
			seleniumServerlauncher.stop();
		}
		if (selenium != null) {
			selenium.stop();
		}
	}

	public WebApplication getApplication() {
		return jettyLauncher.getApplication();
	}

	public static DefaultSelenium getSelenium() {
		return instance.selenium;
	}

	public static WebApplication getWebApplication() {
		return instance.getApplication();
	}

}
