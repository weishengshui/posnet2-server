package com.chinarewards.posnet.ext.test.dao;

import com.chinarewards.posnet.ext.dao.IQQActivityMemberDao;
import com.chinarewards.posnet.ext.dao.WsBaseDao;
import com.chinarewards.qq.adidas.domain.QQActivityMember;

public class QQActivityMemberDao extends WsBaseDao<QQActivityMember> implements
		IQQActivityMemberDao {

	@Override
	public QQActivityMember insert(QQActivityMember t) {
		getEm().persist(t);
		getEm().flush();
		return t;
	}

}
