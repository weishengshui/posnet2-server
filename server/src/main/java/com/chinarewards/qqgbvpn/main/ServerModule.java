/**
 * 
 */
package com.chinarewards.qqgbvpn.main;

import com.chinarewards.qqgbvpn.main.impl.DefaultPosServer;
import com.chinarewards.qqgbvpn.main.impl.InMemorySessionStore;
import com.chinarewards.qqgbvpn.main.mxBean.impl.DatabaseMXBean;
import com.chinarewards.qqgbvpn.main.mxBean.impl.PosnetConnectionMXBean;
import com.chinarewards.qqgbvpn.main.mxBean.vo.ConnectionAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.IConnectionAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.IKnownClientConnectAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.KnownClientConnectAttr;
import com.chinarewards.qqgbvpn.main.protocol.filter.ErrorConnectionKillerFilter;
import com.chinarewards.qqgbvpn.main.protocol.filter.IdleConnectionKillerFilter;
import com.chinarewards.qqgbvpn.main.protocol.filter.LoggingFilter;
import com.chinarewards.qqgbvpn.main.protocol.filter.LoginFilter;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * 
 * 
 * @author Cyril
 * @since 0.1.0
 */
public class ServerModule extends AbstractModule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {

		bind(PosServer.class).to(DefaultPosServer.class);

		bind(SessionStore.class).to(InMemorySessionStore.class).in(
				Singleton.class);

		bind(LoginFilter.class);
		// mxbean
		bind(DatabaseMXBean.class).in(Singleton.class);
		bind(PosnetConnectionMXBean.class).in(Singleton.class);
		
		bind(IConnectionAttr.class).to(ConnectionAttr.class)
				.in(Singleton.class);
		bind(IKnownClientConnectAttr.class).to(KnownClientConnectAttr.class)
				.in(Singleton.class);
		bind(IdleConnectionKillerFilter.class).in(Singleton.class);

		bind(ErrorConnectionKillerFilter.class);
		bind(LoggingFilter.class);
	}

}
