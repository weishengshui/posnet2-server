package com.chinarewards.qqgbvpn.main.qqadidas.vo;

public class QQMemberObtainPrivilegeVo {

	// input
	private String memberKey;
	private double consumeAmt;
	private String posId;

	// output
	private int returnCode = -1;
	private SmallNote smallNote;

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

	public SmallNote getSmallNote() {
		return smallNote;
	}

	public void setSmallNote(SmallNote smallNote) {
		this.smallNote = smallNote;
	}
}
