package com.chinarewards.huaxia.main.protocol.cmd;

import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;

/**
 * 
 * @author weishengshui
 * 
 */
public class HuaXiaCouponExchangeRequestMessage implements ICommand {

	public static final long HUA_XIA_COUPON_EXCHANGE_CMD_ID = 301;

	private long cmdId;
	// 验证码
	private int cdkeyLength;
	private String cdkey;
	// 卡号的最后6位
	private int cardNoLength;
	private String cardNo;

	@Override
	public long getCmdId() {
		return this.cmdId;
	}

	public int getCdkeyLength() {
		return cdkeyLength;
	}

	public void setCdkeyLength(int cdkeyLength) {
		this.cdkeyLength = cdkeyLength;
	}

	public String getCdkey() {
		return cdkey;
	}

	public void setCdkey(String cdkey) {
		this.cdkey = cdkey;
	}

	public int getCardNoLength() {
		return cardNoLength;
	}

	public void setCardNoLength(int cardNoLength) {
		this.cardNoLength = cardNoLength;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public void setCmdId(long cmdId) {
		this.cmdId = cmdId;
	}

}
