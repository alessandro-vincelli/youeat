package com.ttdev.wicketpagetest;

import com.thoughtworks.selenium.Selenium;

/**
 * This class is a wrapper around {@link Selenium} that adds some handy
 * functions for Wicket-generated HTML pages. In particular, it allows you to
 * wait for Ajax processing to be completed. This way, you don't need to use
 * {@link Selenium#waitForCondition(String, String)} which is more complicated
 * and error-prone.
 * 
 * @author Kent Tong
 * 
 */
public class WicketSelenium {
	private static final String AJAX_TIMEOUT = "3000";
	private Selenium selenium;

	public WicketSelenium(Selenium selenium) {
		this.selenium = selenium;
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

}
