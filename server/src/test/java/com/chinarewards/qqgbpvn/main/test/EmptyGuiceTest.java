/**
 * 
 */
package com.chinarewards.qqgbpvn.main.test;

import org.junit.Test;

import com.google.inject.Module;

/**
 * Make sure the {@link GuiceTest#createInjector()} works properly even if
 * {@link GuiceTest#getModules()} returns <code>null</code>.
 * 
 * @author Cyril
 * @since 0.1.0
 */
public class EmptyGuiceTest extends GuiceTest {

	@Override
	protected Module[] getModules() {
		return null;
	}

	/**
	 * Make sure this does not throw exception.
	 */
	@Test
	public void testNullModules() {
	}

}
