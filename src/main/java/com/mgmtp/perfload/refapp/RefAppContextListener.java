/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.perfload.refapp;

import javax.servlet.ServletContextListener;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * {@link ServletContextListener} implementation for <a
 * href="http://code.google.com/p/google-guice/wiki/Servlets">Guice Servlet</a> providing access to
 * the {@link Injector}.
 * 
 * @author rnaegele
 */
public class RefAppContextListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new RefAppModule());
	}
}