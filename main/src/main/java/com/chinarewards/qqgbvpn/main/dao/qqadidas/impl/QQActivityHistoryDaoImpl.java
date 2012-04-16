package com.chinarewards.qqgbvpn.main.dao.qqadidas.impl;

import java.util.List;

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

	@SuppressWarnings("unchecked")
	@Override
	public List<QQActivityHistory> findHistoriesByMemberKey(String memberKey) {
		return getEm()
				.createQuery(
						"FROM QQActivityHistory h WHERE h.memberKey =:memberKey")
				.setParameter("memberKey", memberKey).getResultList();
	}

}
