package com.chinarewards.qqgbvpn.main.logic.qqadidas;

import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qq.adidas.domain.QQWeixinSignIn;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.ConsumeAmountNotEnoughException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateObtainGiftException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateWeixinNoException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.InvalidMemberKeyException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.PrivilegeDoneException;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ObtainPrivilegeResult;

public interface QQAdidasActivityLogic {

	/**
	 * QQ member can obtain a gift for free provided by adidas.
	 * 
	 * @param memberKey
	 *            flag a qq member.
	 * @param posId
	 *            flag a pos
	 * 
	 * @throws InvalidMemberKeyException
	 *             If the specified memberkey was not invalid.
	 * @throws DuplicateObtainGiftException
	 *             If have obtaind the gift more than one times.
	 * 
	 * @return {@link QQActivityHistory} If obtain successful it would produce a
	 *         record of {@link QQActivityHistory}
	 */
	public QQActivityHistory obtainFreeGift(String memberKey, String posId)
			throws InvalidMemberKeyException, DuplicateObtainGiftException;

	/**
	 * QQ member can obtain the privilege according to consume at adidas shop.
	 * 
	 * 
	 * Rebate rule:
	 * <ul>
	 * <li>If consume amount is amt<300, nothing privilege will obtain.</li>
	 * <li>If consume amount is 300=<amt<600 ,and privilege status is
	 * {@link PrivilegeStatus#NEW} will get rebate 50.</li>
	 * <li>If consume amount is 300=<amt<600 ,and privilege status is
	 * {@link PrivilegeStatus#HALF} will get rebate 50.</li>
	 * <li>If consume amount is 300=<amt<600 ,and privilege status is
	 * {@link PrivilegeStatus#DONE} nothing privilege will obtain.</li>
	 * <li>If consume amount is amt>=600,and privilege status is
	 * {@link PrivilegeStatus#NEW} will get rebate 100.</li>
	 * <li>If consume amount is amt>=600,and privilege status is
	 * {@link PrivilegeStatus#HALF} will get rebate 50.</li>
	 * <li>If consume amount is amt>=600,and privilege status is
	 * {@link PrivilegeStatus#DONE} nothing privilege will obtain.</li>
	 * </ul>
	 * 
	 * @param memberKey
	 * @param consumeAmt
	 * @param posId
	 * @return {@link ObtainPrivilegeResult} If obtain successful it would
	 *         produce a record of {@link QQActivityHistory} in
	 *         ObtainPrivilegeResult. If exist QQActivityHistory already, return
	 *         the history .
	 */
	public ObtainPrivilegeResult obtainPrivilege(String memberKey,
			double consumeAmt, String posId) throws InvalidMemberKeyException,
			ConsumeAmountNotEnoughException, PrivilegeDoneException;

	/**
	 * Weixin sign in.
	 * 
	 * @param weixinNo
	 * @param posId
	 * @return {@link QQWeixinSignIn}
	 * 
	 * @throws DuplicateWeixinNoException
	 */
	public QQWeixinSignIn weiXinSignIn(String weixinNo, String posId)
			throws DuplicateWeixinNoException;
}
