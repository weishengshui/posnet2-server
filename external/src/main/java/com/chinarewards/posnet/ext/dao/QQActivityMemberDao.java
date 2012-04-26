package com.chinarewards.posnet.ext.dao;

import java.util.List;

import com.chinarewards.qq.adidas.domain.QQActivityMember;

public class QQActivityMemberDao extends WsBaseDao<QQActivityMember> {

	@SuppressWarnings("unchecked")
	public QQActivityMember findQQMemberByKey(String memberKey) {
		List<QQActivityMember> list = getEm()
				.createQuery(
						"FROM QQActivityMember m where m.memberKey =:memberKey")
				.setParameter("memberKey", memberKey).getResultList();
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

}
