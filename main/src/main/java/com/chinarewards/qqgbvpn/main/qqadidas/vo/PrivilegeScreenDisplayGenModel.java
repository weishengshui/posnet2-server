package com.chinarewards.qqgbvpn.main.qqadidas.vo;

public class PrivilegeScreenDisplayGenModel {

	private int returnCode = -1;
	private String memberKey;
	private double consumeAmt;

	public PrivilegeScreenDisplayGenModel(int returnCode, String memberKey,
			double consumeAmt) {
		this.returnCode = returnCode;
		this.memberKey = memberKey;
		this.consumeAmt = consumeAmt;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

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
}