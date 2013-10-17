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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.inject.servlet.GuiceFilter;

/**
 * This class starts an embedded Jetty server on a (given) port for local testing purposes. The
 * servers functionality is provided by attaching a GuiceServletContextListener.
 * 
 * @author jsievers
 */
public class DemoServer {
	/**
	 * Starts the embedded Jetty server.
	 * 
	 * @param args
	 *            With -port XX you can specify the port on which the server is running.
	 * @throws Exception
	 *             Throws an exception if the Jetty server fails to start.
	 */
	public static void main(final String[] args) throws Exception {
		JCommander jCmd = null;
		try {
			ServerArgs serverArgs = new ServerArgs();

			jCmd = new JCommander(serverArgs);
			jCmd.parse(args);

			// Create a new server and set the servlet context
			Server server = new Server();
			SocketConnector connector = new SocketConnector();
			connector.setPort(serverArgs.port);
			if (serverArgs.maxIdleTime > 0) {
				connector.setMaxIdleTime(serverArgs.maxIdleTime);
			}
			if (serverArgs.soLingerTime > 0) {
				connector.setSoLingerTime(serverArgs.soLingerTime);
			}
			if (serverArgs.acceptQueueSize > 0) {
				connector.setAcceptQueueSize(serverArgs.acceptQueueSize);
			}
			server.setConnectors(new Connector[] { connector });

			ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);

			// Add the Guice listener that includes all bindings
			handler.addEventListener(new RefAppContextListener());
			//handler.setMaxFormContentSize(500);

			// Then add the GuiceFilter and configure the server to
			// reroute all requests through this filter.
			handler.addFilter(GuiceFilter.class, "/*", null);

			// Must add DefaultServlet for embedded Jetty.
			// Failing to do this will cause 404 errors.
			// This is not needed if web.xml is used instead.
			handler.addServlet(DefaultServlet.class, "/");

			server.setHandler(handler);

			QueuedThreadPool threadPool = new QueuedThreadPool();
			if (serverArgs.minThreads > 0) {
				threadPool.setMinThreads(serverArgs.minThreads);
			}
			if (serverArgs.maxThreads > 0) {
				threadPool.setMaxThreads(serverArgs.maxThreads);
			}
			server.setThreadPool(threadPool);

			// Start the server. Exceptions and not handled here.
			server.start();
			server.join();
		} catch (ParameterException ex) {
			jCmd.usage();
		}
	}
}