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

/**
 * This class allows you to specify options to control the behavior of
 * {@link WebPageTestContext}. For example, by default it assumes that your web
 * content folder is src/main/webapp. If it is not the case, you must set the
 * {@link #docBase} property.
 * 
 * @author Kent Tong
 * 
 */
public class Configuration extends WebAppJettyConfiguration {
	/**
	 * The URL prefix that maps to the Wicket filter (default: app).
	 */
	private String wicketFilterPrefix = "app";
	/**
	 * The Selenium command to launch the browser (default: *firefox).
	 */
	private String seleniumBrowserLaunchCmd = "*firefox";
	/**
	 * The port of the Selenium server.
	 */
	private int seleniumServerPort = 4444;
	/**
	 * If we launched the Selenium server process, should we kill it at the end?
	 */
	private boolean killSeleniumAtEnd = false;
 
	public boolean isKillSeleniumAtEnd() {
		return killSeleniumAtEnd;
	}

	public void setKillSeleniumAtEnd(boolean killSeleniumAtEnd) {
		this.killSeleniumAtEnd = killSeleniumAtEnd;
	}

	public String getSeleniumBrowserLaunchCmd() {
		return seleniumBrowserLaunchCmd;
	}

	public void setSeleniumBrowserLaunchCmd(String seleniumBrowserLaunchCmd) {
		this.seleniumBrowserLaunchCmd = seleniumBrowserLaunchCmd;
	}

	public int getSeleniumServerPort() {
		return seleniumServerPort;
	}

	public void setSeleniumServerPort(int seleniumServerPort) {
		this.seleniumServerPort = seleniumServerPort;
	}
	public String getWicketFilterPrefix() {
		return wicketFilterPrefix;
	}

	public void setWicketFilterPrefix(String wicketFilterPrefix) {
		this.wicketFilterPrefix = wicketFilterPrefix;
	}

}
