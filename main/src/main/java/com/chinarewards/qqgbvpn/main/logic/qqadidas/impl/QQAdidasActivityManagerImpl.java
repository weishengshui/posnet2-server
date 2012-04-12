package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qq.adidas.domain.QQWeixinSignIn;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.ConsumeAmountNotEnoughException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateAchievingGiftException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateWeixinNoException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.InvalidMemberKeyException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.PrivilegeDoneException;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityManager;

public class QQAdidasActivityManagerImpl implements QQAdidasActivityManager {

	@Override
	public QQActivityHistory achieveFreeGift(String memberKey)
			throws InvalidMemberKeyException, DuplicateAchievingGiftException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QQActivityHistory achievePrivilege(String memberKey,
			double consumeAmt) throws InvalidMemberKeyException,
			ConsumeAmountNotEnoughException, PrivilegeDoneException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QQWeixinSignIn weiXinSignIn(String weixinNo)
			throws DuplicateWeixinNoException {
		// TODO Auto-generated method stub
		return null;
	}

}
