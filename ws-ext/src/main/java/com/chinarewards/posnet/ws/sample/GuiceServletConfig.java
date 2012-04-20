package com.chinarewards.posnet.ws.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.posnet.ws.sample.BenchResource;
import com.chinarewards.posnet.ws.sample.JacksonResource;
import com.chinarewards.posnet.ws.sample.SampleResource;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Guice Server configuration. Creates an Injector, and binds it to whatever
 * Modules we want. In this case, we use an anonymous Module, but other modules
 * are welcome as well.
 */
public class GuiceServletConfig extends GuiceServletContextListener {

	Logger log = LoggerFactory.getLogger(getClass());

	protected Module[] getModules() {
		List<Module> modules = new ArrayList<Module>();

		modules.add(new ServletModule() {

			@Override
			protected void configureServlets() {

				final Map<String, String> params = new HashMap<String, String>();
				params.put("com.sun.jersey.config.feature.Debug", "true");

				/* bind the REST resources */
				bind(BenchResource.class);
				bind(SampleResource.class);
				bind(JacksonResource.class);

				/* bind jackson converters for JAXB/JSON serialization */
				bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
				bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

				serve("*").with(GuiceContainer.class, params);
			}
		});

		// TODO add others modules

		// everything done.
		log.info("Guice modules created.");

		return modules.toArray(new Module[0]);
	}

	@Override
	protected Injector getInjector() {
		log.info("injector create!");
		return Guice.createInjector(getModules());
	}
}
