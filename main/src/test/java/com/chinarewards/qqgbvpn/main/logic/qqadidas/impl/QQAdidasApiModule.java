package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;

import javax.inject.Singleton;

import com.chinarewards.ws.ext.api.qq.adidas.service.QQActivityMemberService;
import com.google.inject.AbstractModule;

public class QQAdidasApiModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(QQActivityMemberService.class).to(
				QQActivityMemberServiceImpl.class).in(Singleton.class);
	}

}
