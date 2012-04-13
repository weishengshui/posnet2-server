package com.chinarewards.qqgbvpn.main;

import javax.inject.Singleton;

import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityManager;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdidasActivityManagerImpl;
import com.google.inject.AbstractModule;

public class QQAdidasModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(QQAdidasActivityManager.class).to(
				QQAdidasActivityManagerImpl.class).in(Singleton.class);
	}

}
