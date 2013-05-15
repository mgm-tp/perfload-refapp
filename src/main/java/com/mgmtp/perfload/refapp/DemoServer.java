/*
 * Copyright (c) 2013 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mgmtp.perfload.refapp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;

public class DemoServer {
	private static int PORT = 8080;

	public static void main(final String[] args) throws Exception {
		int i = 0;
		while (i < args.length && args[i].startsWith("-")) {
			String arg = args[i++];

			if (arg.equals("-port")) {
				if (i < args.length) {
					PORT = Integer.parseInt(args[i++]);
				} else {
					System.out.println("-port requires an integer port (standard port is 8080)");
				}
			}
		}

		Server server = new Server(PORT);

		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);

		// Add our Guice listener that includes our bindings
		handler.addEventListener(new RefAppContextListener());
		//handler.setMaxFormContentSize(500);

		// Then add GuiceFilter and configure the server to 
		// reroute all requests through this filter. 
		handler.addFilter(GuiceFilter.class, "/*", null);

		// Must add DefaultServlet for embedded Jetty. 
		// Failing to do this will cause 404 errors.
		// This is not needed if web.xml is used instead.
		handler.addServlet(DefaultServlet.class, "/");

		server.setHandler(handler);

		server.start();
		server.join();
	}
}