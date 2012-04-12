package com.chinarewards.qqgbvpn.main.protocol.cmd;

/**
 * Defines the message of a POS client QQ vip adidas sign weixin request
 * 
 * @author Seek
 */
public class QQVIPAddsSignWeixinRequestMessage implements ICommand{
	
	private static final long CMD_ID = 205;
	
	//指令ID
	private final long cmdId = CMD_ID;
	
	//user weixin ID
	private final String weixinId;

	public QQVIPAddsSignWeixinRequestMessage(String weixinId) {
		super();
		this.weixinId = weixinId;
	}

	@Override
	public String toString() {
		return " [cmdId=" + cmdId + ", weixinId=" + weixinId + "]";
	}
	
	@Override
	public long getCmdId() {
		return cmdId;
	}

	public String getWeixinId() {
		return weixinId;
	}
	
}
