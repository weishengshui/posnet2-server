package com.chinarewards.qqgbvpn.mgmtui.search;

import java.util.List;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.google.inject.Inject;

public class SearchLogicImple implements SearchLogic {
	
	@Inject
	QqActivityHistoryDao qqActHistoryDao;

	@Override
	public List<QQActivityHistory> findQqActivityHistoryByCdkey(String cdkey) {
		return qqActHistoryDao.findQqActivityHistoryByCdkey(cdkey);
	}
	
	public void saveQqActivityHistoryByCdkey(QQActivityHistory history){
		qqActHistoryDao.saveQQqActivityHistory(history);
	}

}
