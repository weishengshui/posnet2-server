package com.chinarewards.qqgbvpn.main.dao.qqadidas.impl;

import java.util.List;

import com.chinarewards.qq.adidas.domain.ActivityType;
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
	public List<QQActivityHistory> findHistoriesByMemberKeyAndType(
			String memberKey, ActivityType type) {
		return getEm()
				.createQuery(
						"FROM QQActivityHistory h WHERE h.memberKey =:memberKey AND h.aType =:aType")
				.setParameter("memberKey", memberKey)
				.setParameter("aType", type).getResultList();
	}

}
