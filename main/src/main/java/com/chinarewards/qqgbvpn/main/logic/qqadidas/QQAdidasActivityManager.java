package com.chinarewards.qqgbvpn.main.logic.qqadidas;

import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.GiftObtainedAlreadyException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.InvalidMemberKeyException;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainGiftVo;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainPrivilegeVo;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQWeixinSignInVo;

public interface QQAdidasActivityManager {

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
	 * @throws GiftObtainedAlreadyException
	 *             If have obtaind the gift more than one times.
	 * 
	 * @return QQMemberObtainGiftVo
	 */
	public QQMemberObtainGiftVo obtainFreeGift(String memberKey, String posId);

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
	 * @return QQMemberObtainPrivilegeVo
	 */
	public QQMemberObtainPrivilegeVo obtainPrivilege(String memberKey,
			double consumeAmt, String posId);

	/**
	 * Weixin sign in.
	 * 
	 * @param weixinNo
	 * @param posId
	 * @return int
	 */
	public QQWeixinSignInVo weiXinSignIn(String weixinNo, String posId);
}
