package com.ttdev.wicketpagetest;

import javax.servlet.ServletContext;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebAppJettyLauncher {
	private WebAppContext context;
	private Server server;

	public void startAppInJetty(Configuration cfg) {
		server = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(cfg.getJettyServerPort());
		server.addConnector(connector);
		context = new WebAppContext();
		context.setContextPath("/");
		context.setWar(cfg.getDocBase());
		context.setOverrideDescriptor(cfg.getOverrideWebXml());
		server.setHandler(context);
		try {
			server.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stopJetty() {
		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ServletContext getServletContext() {
		return context.getServletHandler().getServletContext();
	}

	public FilterHolder[] getFilters() {
		return context.getServletHandler().getFilters();
	}
}
