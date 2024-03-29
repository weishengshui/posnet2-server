package com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas;

import java.util.Date;

import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;

/**
 * Defines the message of a POS client QQ vip adidas rebate response
 * 
 * @author Seek
 */
public class QQVIPObtainPrivilegeRespMsg implements ICommand{
	
	private static final long CMD_ID = 204;
	
	//指令ID
	private final long cmdId = CMD_ID;
	
	//结果
	private final long result;
	
	//交易时间
	private final Date xactTime;
	
	//小票上的打印标题
	private final String title;
	
	//小票上的打印内容
	private final String tip;

	public QQVIPObtainPrivilegeRespMsg(long result, Date xactTime,
			String title, String tip) {
		super();
		this.result = result;
		this.xactTime = xactTime;
		this.title = title;
		this.tip = tip;
	}

	@Override
	public String toString() {
		return " [cmdId=" + cmdId + ", result=" + result + ", xactTime="
				+ xactTime + ", title=" + title + ", tip=" + tip + "]";
	}

	public long getCmdId() {
		return cmdId;
	}

	public long getResult() {
		return result;
	}

	public Date getXactTime() {
		return xactTime;
	}

	public String getTitle() {
		return title;
	}

	public String getTip() {
		return tip;
	}
	
}
