package com.chinarewards.qqgbvpn.main.protocol.cmd;

/**
 * Defines the message of a POS client QQ vip adidas rebate request
 * 
 * @author Seek
 */
public class QQVIPAddsRebateRequestMessage implements ICommand{
	
	private static final long CMD_ID = 203;
	
	//指令ID
	private final long cmdId = CMD_ID;
	
	//user code码
	private final String userCode;
	
	//consume amount
	private final String amount;

	public QQVIPAddsRebateRequestMessage(String userCode, String amount) {
		super();
		this.userCode = userCode;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return " [cmdId=" + cmdId + ", userCode=" + userCode + ", amount="
				+ amount + "]";
	}
	
	@Override
	public long getCmdId() {
		return cmdId;
	}

	public String getUserCode() {
		return userCode;
	}

	public String getAmount() {
		return amount;
	}
	
}
