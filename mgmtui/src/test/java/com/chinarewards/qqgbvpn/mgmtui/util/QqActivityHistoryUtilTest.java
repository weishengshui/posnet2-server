package com.chinarewards.qqgbvpn.mgmtui.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qqgbvpn.mgmtui.guice.QqadidasServiceModule;
import com.chinarewards.qqgbvpn.mgmtui.guice.SearchModule;
import com.chinarewards.qqgbvpn.mgmtui.search.QqActivityHistoryDao;
import com.google.inject.Module;

public class QqActivityHistoryUtilTest extends JPATestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chinarewards.qqgbvpn.mgmtui.util.JPATestCase#getModules()
	 */
	protected Module[] getModules() {
		Module[] modules = super.getModules();

		List<Module> m = new ArrayList<Module>(Arrays.asList(modules));
		m.add(new QqadidasServiceModule());
		m.add(new SearchModule());
		return m.toArray(new Module[0]);
	}

	@Test
	public void testQqActHistoryDao_ok() {
		QqActivityHistoryDao qqActivityHistoryDao = injector
				.getInstance(QqActivityHistoryDao.class);
		// prepare data, 1 records.
		QQActivityHistory h1 = new QQActivityHistory();
		h1.setCdKey("123456789");
		qqActivityHistoryDao.saveQQqActivityHistory(h1);

		assertNotNull(qqActivityHistoryDao);
		List<QQActivityHistory> historys = qqActivityHistoryDao
				.findQqActivityHistoryByCdkey("123456789");
		assertFalse(historys.isEmpty());
		assertEquals(1, historys.size());
	}
	
	@Test
	public void testQqActHistoryDao_cannot_find() {
		QqActivityHistoryDao qqActHistoryD = injector
				.getInstance(QqActivityHistoryDao.class);
		// prepare data, 1 records.
		QQActivityHistory h1 = new QQActivityHistory();
		h1.setCdKey("123456789");
		qqActHistoryD.saveQQqActivityHistory(h1);

		assertNotNull(qqActHistoryD);
		List<QQActivityHistory> historys = qqActHistoryD
				.findQqActivityHistoryByCdkey("933434235");
		assertTrue(historys.isEmpty());
	}

}
