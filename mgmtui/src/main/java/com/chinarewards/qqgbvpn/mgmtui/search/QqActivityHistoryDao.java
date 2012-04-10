package com.chinarewards.qqgbvpn.mgmtui.search;

import java.util.List;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;

public interface QqActivityHistoryDao {
	
	public void saveQQqActivityHistory(QQActivityHistory history);
	
	public List<QQActivityHistory> findQqActivityHistoryByCdkey(String cdkey);
}
