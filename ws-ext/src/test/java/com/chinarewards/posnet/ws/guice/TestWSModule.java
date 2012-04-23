package com.chinarewards.posnet.ws.guice;

import javax.inject.Singleton;

import org.junit.Ignore;

import com.chinarewards.posnet.ws.logic.impl.QQActivityMemberServiceImpl;
import com.chinarewards.posnet.ws.manager.QQActivityMerberManager;
import com.chinarewards.posnet.ws.manager.impl.QQActivityMerberManagerImpl;
import com.chinarewards.ws.ext.api.qq.adidas.service.QQActivityMemberService;
import com.google.inject.AbstractModule;

/**
 * @time 2012-3-5   下午04:24:38
 * @author Seek
 */
@Ignore
public class TestWSModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(QQActivityMemberService.class).to(
				QQActivityMemberServiceImpl.class).in(Singleton.class);
		bind(QQActivityMerberManager.class).to(QQActivityMerberManagerImpl.class).in(
				Singleton.class);
	}

}
