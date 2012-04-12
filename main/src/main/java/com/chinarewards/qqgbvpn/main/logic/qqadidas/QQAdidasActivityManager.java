package com.chinarewards.qqgbvpn.main.logic.qqadidas;

import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qq.adidas.domain.QQWeixinSignIn;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.ConsumeAmountNotEnoughException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateAchievingGiftException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateWeixinNoException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.InvalidMemberKeyException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.PrivilegeDoneException;

public interface QQAdidasActivityManager {

	/**
	 * QQ member can achieve a gift for free provided by adidas.
	 * 
	 * @memberKey flag a qq member.
	 * 
	 * @throws InvalidMemberKeyException
	 *             If the specified memberkey was not invalid.
	 * @throws DuplicateAchievingGiftException
	 *             If have achieved the gift more than one times.
	 * 
	 * @return {@link QQActivityHistory} If achieve successful it would produce
	 *         a record of {@link QQActivityHistory}
	 */
	public QQActivityHistory achieveFreeGift(String memberKey)
			throws InvalidMemberKeyException, DuplicateAchievingGiftException;

	/**
	 * QQ member can achieve the privilege according to consume at adidas shop.
	 * 
	 * Rebate rule:
	 * <ul>
	 * <li>If consume amount is amt<300, nothing privilege will achieve.</li>
	 * <li>If consume amount is 300=<amt<600 ,and privilege status is
	 * {@link PrivilegeStatus#NEW} will get rebate 50.</li>
	 * <li>If consume amount is 300=<amt<600 ,and privilege status is
	 * {@link PrivilegeStatus#HALF} will get rebate 50.</li>
	 * <li>If consume amount is 300=<amt<600 ,and privilege status is
	 * {@link PrivilegeStatus#DONE} nothing privilege will achieve.</li>
	 * <li>If consume amount is amt>=600,and privilege status is
	 * {@link PrivilegeStatus#NEW} will get rebate 100.</li>
	 * <li>If consume amount is amt>=600,and privilege status is
	 * {@link PrivilegeStatus#HALF} will get rebate 50.</li>
	 * <li>If consume amount is amt>=600,and privilege status is
	 * {@link PrivilegeStatus#DONE} nothing privilege will achieve.</li>
	 * </ul>
	 * 
	 * @return {@link QQActivityHistory} If achieve successful it would produce
	 *         a record of {@link QQActivityHistory}
	 */
	public QQActivityHistory achievePrivilege(String memberKey,
			double consumeAmt) throws InvalidMemberKeyException,
			ConsumeAmountNotEnoughException, PrivilegeDoneException;

	/**
	 * Weixin sign in.
	 * 
	 * @param weixinNo
	 * @return {@link QQWeixinSignIn}
	 * 
	 * @throws DuplicateWeixinNoException
	 */
	public QQWeixinSignIn weiXinSignIn(String weixinNo)
			throws DuplicateWeixinNoException;
}
