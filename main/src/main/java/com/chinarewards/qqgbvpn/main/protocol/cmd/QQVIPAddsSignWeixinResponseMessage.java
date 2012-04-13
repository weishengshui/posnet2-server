package com.chinarewards.qqgbvpn.main.protocol.cmd;

/**
 * Defines the message of a POS client QQ vip adidas sign weixin response
 * 
 * @author Seek
 */
public class QQVIPAddsSignWeixinResponseMessage implements ICommand{
	
	private static final long CMD_ID = 206;
	
	//指令ID
	private final long cmdId = CMD_ID;
	
	//结果
	private final long result;

	public QQVIPAddsSignWeixinResponseMessage(long result) {
		super();
		this.result = result;
	}

	@Override
	public String toString() {
		return " [cmdId=" + cmdId + ", result=" + result + "]";
	}

	@Override
	public long getCmdId() {
		return cmdId;
	}

	public long getResult() {
		return result;
	}
	
}
