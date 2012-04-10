package com.chinarewards.qqgbvpn.mgmtui.logic.search.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qqgbvpn.mgmtui.guice.SearchModule;
import com.chinarewards.qqgbvpn.mgmtui.search.QqActivityHistoryDao;
import com.chinarewards.qqgbvpn.mgmtui.search.SearchLogic;
import com.chinarewards.qqgbvpn.mgmtui.util.JPATestCase;
import com.google.inject.Module;

public class SearchLogicTest extends JPATestCase {
	
	protected SearchLogic getSearchLogic(){
		return injector.getInstance(SearchLogic.class);
	}
	protected QqActivityHistoryDao getQqActivityHistoryDao(){
		return injector.getInstance(QqActivityHistoryDao.class);
	}

	@Override
	protected Module[] getModules() {
		Module[] m = super.getModules();
		List<Module> list = new ArrayList<Module>(Arrays.asList(m));
		list.add(new SearchModule());
		return list.toArray(new Module[0]);
	}
	
	@Test
	public void testfindQqActivityHistoryByCdkey_OK(){
//		QQActivityHistory h1 = new QQActivityHistory();
//		h1.setCdKey("123456789");
//		QQActivityHistory h2 = new QQActivityHistory();
//		h2.setCdKey("123456789");
		SearchLogic searchLogic = getSearchLogic();
//		searchLogic.saveQqActivityHistoryByCdkey(h1);
//		searchLogic.saveQqActivityHistoryByCdkey(h2);
		List<QQActivityHistory> historys = searchLogic.findQqActivityHistoryByCdkey("123456789");
		assertNotNull(historys);
		assertEquals(6, historys.size());
	}
	@Test
	public void testfindQqActivityHistoryByCdkey_BAD(){
		QQActivityHistory h1 = new QQActivityHistory();
		h1.setCdKey("123456789");
		QQActivityHistory h2 = new QQActivityHistory();
		h2.setCdKey("12345679");
		SearchLogic searchLogic = getSearchLogic();
		searchLogic.saveQqActivityHistoryByCdkey(h1);
		searchLogic.saveQqActivityHistoryByCdkey(h2);
		List<QQActivityHistory> historys = searchLogic.findQqActivityHistoryByCdkey("123456789");
		assertNotNull(historys);
		assertEquals(1, historys.size());
	}
	
	

}
