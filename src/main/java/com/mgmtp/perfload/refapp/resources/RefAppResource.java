/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.perfload.refapp.resources;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mgmtp.perfload.refapp.model.AppInfo;

/**
 * @author rnaegele
 */
@Singleton
@Path("rest")
public class RefAppResource {

	private final AppInfo appInfo;

	@Inject
	public RefAppResource(final AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/appinfo")
	public AppInfo getAppInfo() {
		return appInfo;
	}
}