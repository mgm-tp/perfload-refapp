package com.mgmtp.perfload.refapp;

import com.beust.jcommander.Parameter;

/**
 * @author rnaegele
 */
public class ServerArgs {

	@Parameter(names = "-httpPort", description = "The HTTP port.")
	int port = 8199;

	@Parameter(names = "-minTreads", description = "The minimum number of threads for Jetty's thread pool.")
	int minThreads;

	@Parameter(names = "-maxThreads", description = "The maximum number of threads for Jetty's thread pool.")
	int maxThreads;
}
