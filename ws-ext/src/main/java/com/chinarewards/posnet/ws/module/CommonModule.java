package com.chinarewards.posnet.ws.module;

import com.chinarewards.posnet.ws.dao.WsBaseDao;
import com.chinarewards.qqgbvpn.common.SimpleDateTimeModule;
import com.google.inject.AbstractModule;

public class CommonModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new SimpleDateTimeModule());

		bind(WsBaseDao.class);
	}

}
