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

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import javax.inject.Singleton;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import com.mgmtp.perfload.refapp.model.AppInfo;
import com.mgmtp.perfload.refapp.resources.RefAppResource;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Guice module for the jFunk Server.
 * 
 * @author rnaegele
 */
public class RefAppModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(RefAppResource.class);

		// Install servlet module setting a the Jersey/Guice integration.
		install(new ServletModule() {
			@Override
			protected void configureServlets() {
				filter("/*").through(GuiceContainer.class,
						ImmutableMap.of(JSONConfiguration.FEATURE_POJO_MAPPING, "true",
								ResourceConfig.FEATURE_TRACE, "true",
								ResourceConfig.FEATURE_TRACE_PER_REQUEST, "true",
								ServletContainer.FEATURE_FILTER_FORWARD_ON_404, "true"));
			}
		});
	}

	@Provides
	@Singleton
	AppInfo provideAppInfo() throws IOException {
		Reader reader = null;
		try {
			reader = Resources.newReaderSupplier(AppInfo.class.getResource("appinfo.properties"), Charsets.UTF_8).getInput();
			Properties props = new Properties();
			props.load(reader);
			return new AppInfo(props.getProperty("version"), props.getProperty("revision"));
		} finally {
			closeQuietly(reader);
		}
	}
}