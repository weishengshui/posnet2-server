package com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas;

import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;

/**
 * Defines the message of a POS client QQ vip adidas get present request
 * 
 * @author Seek
 */
public class QQVIPObtainGiftReqMsg implements ICommand{
	
	private static final long CMD_ID = 201;
	
	//指令ID
	private final long cmdId = CMD_ID;
	
	//user code码
	private final String userCode;
	
	public QQVIPObtainGiftReqMsg(String userCode) {
		super();
		this.userCode = userCode;
	}

	@Override
	public String toString() {
		return " [cmdId=" + cmdId + ", userCode=" + userCode + "]";
	}
	
	@Override
	public long getCmdId() {
		return cmdId;
	}

	public String getUserCode() {
		return userCode;
	}
	
}
