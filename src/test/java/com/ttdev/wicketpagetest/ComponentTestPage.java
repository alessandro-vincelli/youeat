package com.ttdev.wicketpagetest;

import org.apache.wicket.markup.html.WebPage;

public class ComponentTestPage extends WebPage {

	public ComponentTestPage(ComponentFactory factory) {
		add(factory.createComponent("testComp"));
	}
}
