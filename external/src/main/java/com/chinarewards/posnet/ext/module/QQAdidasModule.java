package com.chinarewards.posnet.ext.module;

import javax.inject.Singleton;

import com.chinarewards.ext.api.qq.adidas.service.QQActivityMemberService;
import com.chinarewards.posnet.ext.dao.QQActivityMemberDao;
import com.chinarewards.posnet.ext.logic.impl.QQActivityMemberServiceImpl;
import com.chinarewards.posnet.ext.manager.QQActivityMerberManager;
import com.chinarewards.posnet.ext.manager.impl.QQActivityMerberManagerImpl;
import com.google.inject.AbstractModule;

public class QQAdidasModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(QQActivityMemberDao.class);
		bind(QQActivityMemberService.class).to(
				QQActivityMemberServiceImpl.class).in(Singleton.class);
		bind(QQActivityMerberManager.class).to(
				QQActivityMerberManagerImpl.class).in(Singleton.class);
	}

}
