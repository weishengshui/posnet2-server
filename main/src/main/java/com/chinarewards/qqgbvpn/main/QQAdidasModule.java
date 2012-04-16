package com.chinarewards.qqgbvpn.main;

import javax.inject.Singleton;

import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQActivityHistoryDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQActivityMemberDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.impl.QQActivityHistoryDaoImpl;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.impl.QQActivityMemberDaoImpl;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityLogic;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityManager;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasSmallNoteGenerate;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdidasActivityLogicImpl;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdidasActivityManagerImpl;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdidasSmallNoteGenerateImpl;
import com.google.inject.AbstractModule;

public class QQAdidasModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(QQActivityHistoryDao.class).to(QQActivityHistoryDaoImpl.class);
		bind(QQActivityMemberDao.class).to(QQActivityMemberDaoImpl.class);
		bind(QQAdidasActivityManager.class).to(
				QQAdidasActivityManagerImpl.class).in(Singleton.class);
		bind(QQAdidasActivityLogic.class).to(QQAdidasActivityLogicImpl.class)
				.in(Singleton.class);
		bind(QQAdidasSmallNoteGenerate.class).to(
				QQAdidasSmallNoteGenerateImpl.class).in(Singleton.class);
	}

}
