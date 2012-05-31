package com.chinarewards.qqgbvpn.main.dao.qqadidas.impl;

import com.chinarewards.qq.adidas.domain.QQWeixinSignIn;
import com.chinarewards.qqgbvpn.core.BaseDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQWeixinSignInDao;

public class QQWeixinSignInDaoImpl extends BaseDao implements QQWeixinSignInDao {

	@Override
	public QQWeixinSignIn save(QQWeixinSignIn qqWeixinSignIn) {
		getEm().persist(qqWeixinSignIn);
		return qqWeixinSignIn;
	}

}
