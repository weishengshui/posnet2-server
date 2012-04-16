package com.chinarewards.qqgbvpn.mgmtui.logic.qqmembersearch.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qqgbvpn.mgmtui.dao.qqadidas.QqActivityHistoryDao;
import com.chinarewards.qqgbvpn.mgmtui.guice.QqadidasServiceModule;
import com.chinarewards.qqgbvpn.mgmtui.logic.qq.adidas.search.QQMemberSearchLogic;
import com.chinarewards.qqgbvpn.mgmtui.util.JPATestCase;
import com.google.inject.Module;

public class QQMemberSearchLogicTest extends JPATestCase {
	
	protected QQMemberSearchLogic getSearchLogic(){
		return injector.getInstance(QQMemberSearchLogic.class);
	}
	protected QqActivityHistoryDao getQqActivityHistoryDao(){
		return injector.getInstance(QqActivityHistoryDao.class);
	}

	@Override
	protected Module[] getModules() {
		Module[] m = super.getModules();
		List<Module> list = new ArrayList<Module>(Arrays.asList(m));		
		list.add(new QqadidasServiceModule());
		return list.toArray(new Module[0]);
	}
	
	@Test
	public void testfindQqActivityHistoryByCdkey_OK(){
		QQActivityHistory h1 = new QQActivityHistory();
		h1.setMemberKey("123456789");
		QQActivityHistory h2 = new QQActivityHistory();
		h2.setMemberKey("123456789");
		QQMemberSearchLogic searchLogic = getSearchLogic();
		searchLogic.saveQqActivityHistoryByMemberKey(h1);
		searchLogic.saveQqActivityHistoryByMemberKey(h2);
		List<QQActivityHistory> historys = searchLogic.findQqActivityHistoryByMemberKey("123456789");
		assertNotNull(historys);
		assertEquals(2, historys.size());
	}
	@Test
	public void testfindQqActivityHistoryByCdkey_BAD(){
		QQActivityHistory h1 = new QQActivityHistory();
		h1.setMemberKey("123456789");
		QQActivityHistory h2 = new QQActivityHistory();
		h2.setMemberKey("12345679");
		QQMemberSearchLogic searchLogic = getSearchLogic();
		searchLogic.saveQqActivityHistoryByMemberKey(h1);
		searchLogic.saveQqActivityHistoryByMemberKey(h2);
		List<QQActivityHistory> historys = searchLogic.findQqActivityHistoryByMemberKey("123456789");
		assertNotNull(historys);
		assertEquals(1, historys.size());
	}
	
	

}
