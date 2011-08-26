/**
 * 
 */
package com.chinarewards.qqgbvpn.main;

import javax.inject.Singleton;

import com.chinarewards.qqgbvpn.common.SimpleDateTimeModule;
import com.chinarewards.qqgbvpn.main.dao.qqapi.GroupBuyingDao;
import com.chinarewards.qqgbvpn.main.dao.qqapi.impl.GroupBuyingDaoImpl;
import com.chinarewards.qqgbvpn.main.logic.qqapi.GroupBuyingManager;
import com.chinarewards.qqgbvpn.main.logic.qqapi.impl.GroupBuyingManagerImpl;
import com.chinarewards.qqgbvpn.qqapi.service.GroupBuyingService;
import com.chinarewards.qqgbvpn.qqapi.service.impl.GroupBuyingServiceImpl;
import com.google.inject.AbstractModule;

/**
 * 
 * 
 * @author Cyril
 * @since 1.0.0
 */
public class ApplicationModule extends AbstractModule {

	@Override
	protected void configure() {
		
		install(new SimpleDateTimeModule());

		bind(AppPreference.class).in(Singleton.class);

		// singleton first. can change.
		bind(Application.class).in(Singleton.class);

		// log
		bind(LogConfig.class).to(LogConfigImpl.class).in(Singleton.class);
		
	}

}
