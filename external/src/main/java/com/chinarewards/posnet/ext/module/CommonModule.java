package com.chinarewards.posnet.ext.module;

import com.chinarewards.posnet.ext.dao.WsBaseDao;
import com.chinarewards.qqgbvpn.common.SimpleDateTimeModule;
import com.google.inject.AbstractModule;

public class CommonModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new SimpleDateTimeModule());

		bind(WsBaseDao.class);
	}

}
