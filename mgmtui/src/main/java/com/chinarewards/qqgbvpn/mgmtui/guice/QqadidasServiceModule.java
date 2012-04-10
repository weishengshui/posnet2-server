/**
 * 
 */
package com.chinarewards.qqgbvpn.mgmtui.guice;

import com.chinarewards.qqgbvpn.mgmtui.search.QqActivityHistoryDao;
import com.chinarewards.qqgbvpn.mgmtui.search.QqActivityHistoryDaoImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * 
 * 
 * @author yanxin
 * 
 */
public class QqadidasServiceModule extends AbstractModule {

	protected void configure() {

		bind(QqActivityHistoryDao.class).to(QqActivityHistoryDaoImpl.class).in(Singleton.class);
	}

}
