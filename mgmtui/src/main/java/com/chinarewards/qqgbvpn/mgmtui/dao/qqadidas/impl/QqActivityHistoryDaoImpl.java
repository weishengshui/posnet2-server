package com.chinarewards.qqgbvpn.mgmtui.dao.qqadidas.impl;

import java.util.List;

import javax.persistence.EntityManager;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qqgbvpn.core.BaseDao;
import com.chinarewards.qqgbvpn.mgmtui.dao.qqadidas.QqActivityHistoryDao;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class QqActivityHistoryDaoImpl extends BaseDao implements
		QqActivityHistoryDao {

	@Inject
	Provider<EntityManager> em;
	
	/**
	 * Get QQ activity history by member key
	 * @param  memberKey
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<QQActivityHistory> findQqActivityHistoryByMemberKey(String memberKey) {
		List<QQActivityHistory> historys = getEm()
				.createQuery("FROM QQActivityHistory WHERE memberKey=:memberKey order by lastModifiedAt asc")
				.setParameter("memberKey", memberKey).getResultList();
		return historys;
	}

	@Override
	public void saveQQqActivityHistory(QQActivityHistory history) {
		getEm().persist(history);
	}
}
