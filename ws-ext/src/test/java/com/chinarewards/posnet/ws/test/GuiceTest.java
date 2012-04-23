/**
 * 
 */
package com.chinarewards.posnet.ws.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * 
 * @author Cyril
 * @since 0.1.0
 */
@Ignore
public abstract class GuiceTest extends BaseTest {

	private Injector injector;

	/**
	 * Creates an instance of Guice injector with prepared modules returned by
	 * {@link #getModules()}.
	 * 
	 * @return
	 */
	protected Injector createInjector() {
		Module[] modules = getModules();
		if (modules == null) {
			// prevent NullPointerException
			modules = new Module[0];
		}
		injector = Guice.createInjector(modules);
		return injector;
	}

	/**
	 * 
	 * @return
	 */
	public Injector getInjector() {
		return injector;
	}

	/**
	 * Returns a list of modules for Guice injector creation.
	 * 
	 * @return
	 */
	protected Module[] getModules() {
		return null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		createInjector();
	}

	@After
	public void tearDown() throws Exception {
	}

}
