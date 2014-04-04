package com.rcg.admin.runner;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyRunner {

	public static final int PORT = 8081;

	public static Server runJetty() throws Exception {
		return runJetty(PORT);
	}

	public static Server runJetty(int port) throws Exception {
		Server server = new Server(port);
		WebAppContext context = new WebAppContext();
		context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
		context.setResourceBase("src/main/webapp");
		context.setContextPath("/");
		context.setParentLoaderPriority(true);
		server.setHandler(context);
		server.start();
		return server;
	}
	
	public static void main(String[] args) throws Exception {
		runJetty();
	}
}
