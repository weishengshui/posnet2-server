/**
 * 
 */
package com.chinarewards.qqgbvpn.mgmtui.guice;

import com.chinarewards.qqgbvpn.mgmtui.dao.qqadidas.QqActivityHistoryDao;
import com.chinarewards.qqgbvpn.mgmtui.dao.qqadidas.impl.QqActivityHistoryDaoImpl;
import com.chinarewards.qqgbvpn.mgmtui.logic.qq.adidas.search.QQMemberSearchLogic;
import com.chinarewards.qqgbvpn.mgmtui.logic.qq.adidas.search.impl.QQMemberSearchLogicImple;
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
		bind(QQMemberSearchLogic.class).to(QQMemberSearchLogicImple.class).in(Singleton.class);
		bind(QqActivityHistoryDao.class).to(QqActivityHistoryDaoImpl.class).in(
				Singleton.class);
	}

}
