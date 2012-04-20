package com.chinarewards.qqgbvpn.main;

import javax.inject.Singleton;

import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQActivityHistoryDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQActivityMemberDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQWeixinSignInDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.impl.QQActivityHistoryDaoImpl;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.impl.QQActivityMemberDaoImpl;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.impl.QQWeixinSignInDaoImpl;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdReceiptGen;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdRespScreenDisplayGen;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdActivityLogic;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdActivityManager;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdReceiptGenImpl;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdRespScreenDisplayGenImpl;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdActivityLogicImpl;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdActivityManagerImpl;
import com.google.inject.AbstractModule;

public class QQAdidasModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(QQActivityHistoryDao.class).to(QQActivityHistoryDaoImpl.class);
		bind(QQActivityMemberDao.class).to(QQActivityMemberDaoImpl.class);
		bind(QQWeixinSignInDao.class).to(QQWeixinSignInDaoImpl.class);

		bind(QQAdActivityManager.class).to(
				QQAdActivityManagerImpl.class).in(Singleton.class);
		bind(QQAdActivityLogic.class).to(QQAdActivityLogicImpl.class)
				.in(Singleton.class);
		bind(QQAdReceiptGen.class).to(QQAdReceiptGenImpl.class).in(
				Singleton.class);
		bind(QQAdRespScreenDisplayGen.class).to(
				QQAdRespScreenDisplayGenImpl.class).in(Singleton.class);
	}

}
