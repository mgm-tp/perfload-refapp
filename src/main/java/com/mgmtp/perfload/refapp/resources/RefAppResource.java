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
package com.mgmtp.perfload.refapp.resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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

import com.google.common.base.Strings;
import com.mgmtp.perfload.refapp.model.AppInfo;

/**
 * @author rnaegele
 */
@Singleton
@Path("rest")
public class RefAppResource {

	/** Test XML string */
	private static final String XMLSTRING = "<element value=\"%d\"/>";
	/** Test JSON string */
	private static final String JSONSTRING = "{\"element\":{\"attribute\":\"%d\"}}";
	/** Variance of sleep time of /time request */
	private static final int SLEEPTIME_VARIANCE = 750;
	/** Base sleep time of /time request */
	private static final int SLEEPTIME_BASE = 250;
	/** Synchronized list for persistently storing garbage. */
	private static final List<Object> persistentGarbage = new ArrayList<Object>();

	private final AppInfo appInfo;
	private final Random rng = new Random();

	@Inject
	public RefAppResource(final AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	/**
	 * Get basic info about this application.
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
	@Consumes({ MediaType.TEXT_PLAIN })
	@Produces({ MediaType.TEXT_PLAIN })
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
		Strings.nullToEmpty(accept);

		if (accept.equals(MediaType.APPLICATION_JSON)) {
			return String.format(JSONSTRING, rng.nextInt(10000));
		} else if (accept.equals(MediaType.APPLICATION_XML)) {
			return String.format(XMLSTRING, rng.nextInt(10000));
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
		return Response.ok().build();
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
		return Response.ok().build();
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

		return Response.ok().build();
	}

	/**
	 * Triggers a loop of calculations to simulate heavy CPU load on the server. The loop count can
	 * be adjusted by specifying a value in the URI.
	 * 
	 * @param loops
	 *            The amount of loops the calculation should run.
	 * @return The "Terminated" string after successfully executing the loop.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/cpuload/{loops}")
	public String generateCPULoad(@PathParam("loops") final int loops) {
		for (int i = 0; i < loops; i++) {
			Math.atan(Math.sqrt(Math.pow(10, 10)));
		}

		return "Terminated";
	}

	/**
	 * Triggers the creation Objects to temporarily generate garbage to start and test the garbage
	 * collection of the server. The loop count can be adjusted by specifying a value in the URI.
	 * 
	 * @param amount
	 *            The amount of loops the garbage generation should run.
	 * @return The "Terminated" string after successfully executing the loop.
	 * @throws WebApplicationException
	 *             If the generation triggered an {@link OutOfMemoryError}.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/garbage/{amount}")
	public String generateGarbage(@PathParam("amount") final int amount) {
		Object[] garbage = new Object[amount];

		try {
			for (int i = 0; i < amount; i++) {
				garbage[i] = new byte[10];
			}
		} catch (OutOfMemoryError ex) {
			throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
		}

		return "Terminated";
	}

	/**
	 * Triggers the creation Objects to generate persistent garbage. The objects will not be cleared
	 * afterwards to simulate pollution of the servers RAM. The loop count can be adjusted by
	 * specifying a value in the URI.
	 * 
	 * @param amount
	 *            The amount of loops the garbage generation should run.
	 * @return The "Terminated" string after successfully executing the loop.
	 * @throws WebApplicationException
	 *             If the generation triggered an {@link OutOfMemoryError}.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/garbage_persistent/{amount}")
	public String generatePersistentGarbage(@PathParam("amount") final int amount) {
		Object[] garbage = new Object[amount];

		try {
			for (int i = 0; i < amount; i++) {
				garbage[i] = new byte[10];
			}

			persistentGarbage.add(garbage);
		} catch (OutOfMemoryError ex) {
			throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
		}

		return "Terminated";
	}

}