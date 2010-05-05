package com.ttdev.wicketpagetest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.wicket.Page;

import com.thoughtworks.selenium.Selenium;

/**
 * This class is a wrapper around {@link Selenium} that adds some handy
 * functions for Wicket-generated HTML pages. In particular, it allows you to
 * wait for Ajax processing to be completed. This way, you don't need to use
 * {@link Selenium#waitForCondition(String, String)} which is more complicated
 * and error-prone.
 * <p>
 * In addition, it registers a wicket locator for Selenium. For example, Using
 * wicket=//myform[2]//name it will first locate the 3rd element (breadth-first
 * search) with wicket:id="myform" and then the first element in it with
 * wicket:id="name". If it must be an immediate child, use / instead of //.
 * However, due to Wicket-1174, an element will lose its wicket:id after it is
 * refreshed by AJAX. See Wicket-2832 for a suggested solution.
 * 
 * @author Kent Tong
 * 
 */
public class WicketSelenium {
	private static final String AJAX_TIMEOUT = "3000";
	private Selenium selenium;

	public WicketSelenium(Selenium selenium) {
		this.selenium = selenium;
		registerWicketLocator();
	}

	/**
	 * It waits until the Wicket Ajax processing has completed. It does that by
	 * waiting until all the Wicket Ajax channels to become idle.
	 * 
	 * This method heavily depends on the Wicket internal Ajax implementation
	 * and thus is subject to changes.
	 */
	public void waitUntilAjaxDone() {
		String waitTarget = String.format("wicketAjaxBusy = function () {"
				+ "for (var channelName in %1$s) {"
				+ "if (%1$s[channelName].busy) { return true; }"
				+ "return false;}};" + "!wicketAjaxBusy();",
				qualify("Wicket.channelManager.channels"));
		selenium.waitForCondition(waitTarget, AJAX_TIMEOUT);
	}

	private void registerWicketLocator() {
		String js = loadJavaScript();
		selenium.getEval(js);
	}

	private String loadJavaScript() {
		String js = "";
		try {
			InputStream in = getClass().getResourceAsStream("wpt.js");
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			try {
				while (true) {
					String line = r.readLine();
					if (line == null) {
						break;
					}
					js += line + "\n";
				}
			} finally {
				r.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return js;
	}

	/**
	 * Javascript and DOM objects in the test object is contained in an iframe.
	 * To refer to them in Selenium, you need to qualify their names.
	 * 
	 * @param nameInTestObject
	 *            the unqualified js/DOM name in the test object
	 * @return the qualified name
	 */
	private static String qualify(String nameInTestObject) {
		return "selenium.browserbot.getCurrentWindow()." + nameInTestObject;
	}

	/**
	 * Open a bookmarkable Wicket page. That is, it has a no-arg constructor.
	 * 
	 * @param pageClass
	 *            the class of the page
	 */
	public void openBookmarkablePage(Class<? extends Page> pageClass) {
		selenium.open(String.format("?wicket:bookmarkablePage=:%s", pageClass
				.getName()));

	}

	/**
	 * Open a non-bookmarkable Wicket page. That is, its constructor takes one
	 * or more arguments.
	 * 
	 * @param pageClass
	 *            the class of the page
	 * @param constructorArgs
	 *            the constructor agruments
	 */
	public void openNonBookmarkablePage(Class<? extends Page> pageClass,
			Object... constructorArgs) {
		MockableBeanInjector.mockBean(LauncherPage.PAGE_FACTORY_FIELD_NAME,
				new DefaultPageFactory(pageClass, constructorArgs));
		openBookmarkablePage(LauncherPage.class);
	}

}
