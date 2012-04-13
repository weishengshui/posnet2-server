package com.chinarewards.qqgbvpn.main.dao.qqadidas.impl;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qqgbvpn.core.BaseDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQActivityHistoryDao;

public class QQActivityHistoryDaoImpl extends BaseDao implements
		QQActivityHistoryDao {

	@Override
	public QQActivityHistory save(QQActivityHistory qqActivityHistory) {
		getEm().persist(qqActivityHistory);
		return qqActivityHistory;
	}

}
