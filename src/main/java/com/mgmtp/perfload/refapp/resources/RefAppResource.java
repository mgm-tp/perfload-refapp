/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.perfload.refapp.resources;

import java.io.InputStream;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mgmtp.perfload.refapp.model.AppInfo;

/**
 * @author rnaegele
 */
@Singleton
@Path("rest")
public class RefAppResource {

	private static final String XMLSTRING = "<element attribute=\"xyz\"/>";
	private static final String JSONSTRING = "{\"element\":{\"attribute\":\"xyz\"}}";
	private static final int SLEEPTIME_VARIANCE = 750;
	private static final int SLEEPTIME_BASE = 250;
	private final AppInfo appInfo;
	private final Random rng = new Random();

	@Inject
	public RefAppResource(final AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	/**
	 * Get basic infos for this application.
	 * 
	 * @return The object holding the data.
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/appinfo")
	public AppInfo getAppInfo() {
		return appInfo;
	}

	/**
	 * Tests put and sends the body content back to the client.
	 * 
	 * @param is
	 *            The body.
	 * @return The reflected body.
	 */
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/test")
	public InputStream putData(final InputStream is) {
		return is;
	}

	/**
	 * Tests get and sends a test string back to the client.
	 * 
	 * @param accept
	 *            The header string of the "Accept" attribute.
	 * @return A JSON or a XML string based on the requested Accept configuration.
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/test")
	public String getData(@HeaderParam("Accept") final String accept) {
		if (accept.equals(MediaType.APPLICATION_JSON)) {
			return JSONSTRING;
		} else if (accept.equals(MediaType.APPLICATION_XML)) {
			return XMLSTRING;
		}

		return "";
	}

	/**
	 * Tests post by simply returning a response.
	 * 
	 * @return Empty response.
	 */
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/test")
	public Response postData() {
		return Response.noContent().build();
	}

	/**
	 * Tests delete by simply returning a response.
	 * 
	 * @return Empty response.
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/test")
	public Response deleteData() {
		return Response.noContent().build();
	}

	/**
	 * Simulates different response times by sending the thread to sleep for a random amount of time
	 * and responding afterwards.
	 * 
	 * @return Empty response on success, server error response on {@link InterruptedException}
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/time")
	public Response delayResponse() {
		try {
			Thread.sleep(rng.nextInt(SLEEPTIME_VARIANCE) + SLEEPTIME_BASE);
		} catch (InterruptedException ex) {
			return Response.serverError().build();
		}

		return Response.noContent().build();
	}

	/**
	 * Triggers a loop of calculations to simulate heavy CPU load on the server. The loop count can
	 * be adjusted by specifying a value in the URI.
	 * 
	 * @param loops
	 *            The amount of loops the calculation should run.
	 * @return The "Terminated" string after successfully executing the loop.
	 */
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/cpuload/{loops}")
	public String generateCPULoad(@PathParam("loops") final int loops) {
		for (int i = 0; i < loops; i++) {
			Math.atan(Math.sqrt(Math.pow(10, 10)));
		}

		return "Terminated";
	}

	/**
	 * Triggers the creation BufferedImages to temporarily generate garbage to start and test the
	 * garbage collection of the server. The loop count can be adjusted by specifying a value in the
	 * URI.
	 * 
	 * @param amount
	 *            The amount of loops the garbage generation should run.
	 * @return The "Terminated" string after successfully executing the loop.
	 * @throws WebApplicationException
	 *             If the generation triggered an {@link OutOfMemoryError}.
	 */
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/garbage/{amount}")
	public String generateGarbage(@PathParam("amount") final int amount) {
		Object[] garbage = new Object[amount];

		try {
			for (int i = 0; i < amount; i++) {
				garbage[i] = new byte[1000];
			}
		} catch (OutOfMemoryError ex) {
			throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
		}

		return "Terminated";
	}

}