package com.chinarewards.posnet.ext.sample;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

import com.chinarewards.posnet.ext.main.EmptyServlet;
import com.google.inject.servlet.GuiceFilter;

/**
 * Starts an embedded Jetty server.
 */
public class Main {
	public static void main(String[] args) throws Exception {
		Server server = new Server(8081);
		Context root = new Context(server, "/", Context.SESSIONS);

		root.addFilter(GuiceFilter.class, "/*", 0);
		root.addEventListener(new GuiceServletConfig());

		root.addServlet(EmptyServlet.class, "/*");

		server.start();
	}
}
