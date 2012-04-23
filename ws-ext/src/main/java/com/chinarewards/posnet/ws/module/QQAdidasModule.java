package com.chinarewards.posnet.ws.module;

import javax.inject.Singleton;

import com.chinarewards.posnet.ws.dao.QQActivityMemberDao;
import com.chinarewards.posnet.ws.logic.impl.QQActivityMemberServiceImpl;
import com.chinarewards.posnet.ws.manager.QQActivityMerberManager;
import com.chinarewards.posnet.ws.manager.impl.QQActivityMerberManagerImpl;
import com.chinarewards.ws.ext.api.qq.adidas.service.QQActivityMemberService;
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
