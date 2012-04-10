package com.chinarewards.qqgbvpn.mgmtui.search;

import java.util.List;

import javax.persistence.EntityManager;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qqgbvpn.core.BaseDao;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class QqActivityHistoryDaoImpl extends BaseDao implements
		QqActivityHistoryDao {

	@Inject
	Provider<EntityManager> em;

	@Override
	@SuppressWarnings("unchecked")
	public List<QQActivityHistory> findQqActivityHistoryByCdkey(String cdkey) {
		List<QQActivityHistory> historys = (List<QQActivityHistory>) (getEm()
				.createQuery(
						"from QQActivityHistory where cdkey=:cdkey order by lastModifiedAt")
				.setParameter("cdkey", cdkey).getResultList());
		return historys;
	}

	@Override
	public void saveQQqActivityHistory(QQActivityHistory history) {
		getEm().persist(history);
	}
}
