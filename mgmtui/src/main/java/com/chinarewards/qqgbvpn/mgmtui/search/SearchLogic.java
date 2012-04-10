package com.chinarewards.qqgbvpn.mgmtui.search;

import java.util.List;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;

public interface SearchLogic {
	public List<QQActivityHistory> findQqActivityHistoryByCdkey(String cdkey);
	public void saveQqActivityHistoryByCdkey(QQActivityHistory history);
}
