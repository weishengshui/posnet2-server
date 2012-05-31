package com.chinarewards.qqgbvpn.main.qqadidas.vo;

import java.util.Date;

public class QQMemberObtainPrivilegeVo {

	// input
	private String memberKey;
	private double consumeAmt;
	private String posId;

	// output
	private int returnCode = -1;
	private Date operateTime;
	private Receipt receipt;
	private ScreenDisplay display;

	public String getMemberKey() {
		return memberKey;
	}

	public void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
	}

	public double getConsumeAmt() {
		return consumeAmt;
	}

	public void setConsumeAmt(double consumeAmt) {
		this.consumeAmt = consumeAmt;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public ScreenDisplay getDisplay() {
		return display;
	}

	public void setDisplay(ScreenDisplay display) {
		this.display = display;
	}
}
