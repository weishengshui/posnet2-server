package com.chinarewards.posnet.ext.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.posnet.ext.common.HomeDirLocator;
import com.chinarewards.posnet.ext.config.ConfigReader;
import com.chinarewards.posnet.ext.config.HardCodedConfigModule;
import com.chinarewards.posnet.ext.module.AppModule;
import com.chinarewards.posnet.ext.resource.QQAdidasResource;
import com.chinarewards.posnet.ext.sample.BenchResource;
import com.chinarewards.posnet.ext.sample.JacksonResource;
import com.chinarewards.posnet.ext.sample.SampleResource;
import com.chinarewards.qqgbvpn.core.jpa.JpaPersistModuleBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Entry point for all guice configurations. Register any Guice modules here.
 * 
 * @author yanxin
 * @since 0.3.0
 */
public class GuiceBootstrap extends GuiceServletContextListener {

	Logger log = LoggerFactory.getLogger(getClass());

	protected String rootConfigFilename = "posnet.ini";

	Configuration configuration;

	public static final String HOME_DIR_ENV_KEY = "POSNETEXT_HOME";

	protected Module getConfigModule() {

		return new HardCodedConfigModule(configuration);

	}

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
				bind(QQAdidasResource.class);

				/* bind jackson converters for JAXB/JSON serialization */
				bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
				bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

				serve("*").with(GuiceContainer.class, params);
			}
		});

		// TODO add others modules
		// configuration
		modules.add(getConfigModule());
		// JPA
		modules.add(buildJpaPersistModule());

		modules.add(new AppModule());

		// everything done.
		log.info("Guice modules created.");

		return modules.toArray(new Module[0]);
	}

	@Override
	protected Injector getInjector() {
		log.info("Guice bootstrapping");

		testLogVerboseLevel();
		// build configuration.
		try {
			this.buildConfiguration();
		} catch (ConfigurationException e) {
			throw new RuntimeException("Failed to build configuration", e);
		}
		Injector injector = Guice.createInjector(getModules());

		// run persist service
		PersistService ps = injector.getInstance(PersistService.class);
		ps.start();

		return injector;
	}

	protected JpaPersistModule buildJpaPersistModule() {

		// TODO make it not a builder.
		JpaPersistModuleBuilder builder = new JpaPersistModuleBuilder();

		JpaPersistModule jpaModule = new JpaPersistModule("posnet");
		Properties jpaProp = builder.buildHibernateProperties(configuration,
				"db");
		jpaModule.properties(jpaProp);

		if (log.isDebugEnabled()) {
			log.debug("Database configurations:");
			log.debug("- User    : {}", configuration.getProperty("db.user"));
			// log.debug("- Password: {}",
			// configuration.getProperty("db.password"));
			log.debug("- Driver  : {}", configuration.getProperty("db.driver"));
			log.debug("- URL     : {}", configuration.getProperty("db.url"));
		}

		return jpaModule;
	}

	/**
	 * @return the rootConfigFilename
	 */
	public String getRootConfigFilename() {
		return rootConfigFilename;
	}

	protected void buildConfiguration() throws ConfigurationException {

		// check if the directory is given via command line.
		String homedir = null; // we don't have default directory
		HomeDirLocator homeDirLocator = new HomeDirLocator(homedir);
		homeDirLocator.setHomeDirEnvName(HOME_DIR_ENV_KEY);
		ConfigReader cr = new ConfigReader(homeDirLocator);

		log.info("Home directory: {}", homeDirLocator.getHomeDir());

		// read the configuration
		Configuration conf = cr.read(this.getRootConfigFilename());
		configuration = conf;

		if (this.configuration == null) {
			// no configuration is found, throw exception
			throw new RuntimeException(
					"No configuration is found. Please specify "
							+ HOME_DIR_ENV_KEY
							+ " environment variable for the home directory.");
		}

	}

	protected void testLogVerboseLevel() {
		System.out.println("Testing java logging verbose level");
		log.error("ERROR message (testing)");
		log.warn("WARN  message (testing)");
		log.info("INFO  message (testing)");
		log.debug("DEBUG message (testing)");
		log.trace("TRACE message (testing)");
	}
}
