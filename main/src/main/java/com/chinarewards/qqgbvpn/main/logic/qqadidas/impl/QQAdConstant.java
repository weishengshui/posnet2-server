package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;

/**
 * Define all the status and key number.
 * 
 * @author yanxin
 * @since 0.3.0
 */
public class QQAdConstant {

	/**
	 * 获取优惠的消费下限,如 300元
	 */
	public static final double CONSUME_AMOUNT_TO_REBATE_HALF_PRIVILEGE = 300d;
	/**
	 * 获取优惠的消费上限,如600元
	 */
	public static final double CONSUME_AMOUNT_TO_REBATE_FULL_PRIVILEGE = 600d;

	/**
	 * 总共能获取的优惠,如100元
	 */
	public static final double REBATE_FULL_AMOUNT = 100d;

	/**
	 * 能获取的部分优惠， 如50元
	 */
	public static final double REBATE_HALF_AMOUNT = 50d;

	// ----------- privilege status begin----------------
	/**
	 * 50/100元现金抵用劵
	 */
	public static final int PRIVILEGE_OK = 0;

	/**
	 * 没有优惠-优惠已经领完
	 */
	public static final int PRIVILEGE_FAIL_OBTAINED_ALL_ALREADY = 1;

	/**
	 * 会员key无效
	 */
	public static final int PRIVILEGE_FAIL_INVALD_MEMBER = 2;
	/**
	 * 没有优惠-消费金额不够
	 */
	public static final int PRIVILEGE_FAIL_CONSUME_NOT_ENOUGH = 3;

	// ------------------privilege status end -------------

	// --------------gift status begin-------------
	/**
	 * 成功
	 */
	public static final int GIFT_OK = 0;

	/**
	 * 已经领取过
	 */
	public static final int GIFT_FAIL_OBTAINED_ALREADY = 1;

	/**
	 * 会员Key无效
	 */
	public static final int GIFT_FAIL_INVALID_MEMBER = 2;

	// -----------gift status end ----------------------- /**

	// ------------weixin signin satus start --------------
	/**
	 * 成功
	 */
	public static final int WEIXIN_SUCCESS = 0;
	// ------------weixin signin satus start --------------

	/**
	 * 非业务状态
	 */
	public static final int OTHERS = -1;

}
