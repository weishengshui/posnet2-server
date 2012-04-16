package com.chinarewards.qqgbvpn.main.dao.qqadidas;

import com.chinarewards.qq.adidas.domain.QQActivityMember;

public interface QQActivityMemberDao {

	QQActivityMember findByMemberKey(String memberKey);

	QQActivityMember update(QQActivityMember member);
}
