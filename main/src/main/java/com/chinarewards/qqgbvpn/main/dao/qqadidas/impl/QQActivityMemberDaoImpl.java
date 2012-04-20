package com.chinarewards.qqgbvpn.main.dao.qqadidas.impl;

import java.util.List;

import com.chinarewards.qq.adidas.domain.QQActivityMember;
import com.chinarewards.qqgbvpn.core.BaseDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQActivityMemberDao;

public class QQActivityMemberDaoImpl extends BaseDao implements
		QQActivityMemberDao {

	@Override
	@SuppressWarnings("unchecked")
	public QQActivityMember findByMemberKey(String memberKey) {
		List<QQActivityMember> member = getEm()
				.createQuery(
						"FROM QQActivityMember m WHERE m.memberKey =:memberKey")
				.setParameter("memberKey", memberKey).getResultList();
		if (!member.isEmpty()) {
			return member.get(0);
		}
		return null;
	}

	@Override
	public QQActivityMember update(QQActivityMember member) {
		log.debug("prepare to update member.");
		getEm().persist(member);
		return member;
	}

}
