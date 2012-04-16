package com.chinarewards.qqgbvpn.main.qqadidas.vo;

public class QQMemberObtainPrivilegeVo {

	/**
	 * Obtain successful! It between 50 and 100.
	 */
	public static final int QQ_MEMBER_OBTAIN_PRIVILEGE_SUCCESS = 0;

	/**
	 * Invalid member key.
	 */
	public static final int QQ_MEMBER_OBTAIN_PRIVILEGE_INVALID_MEMBER = 1;

	/**
	 * Not enough consume amount to get privilege.
	 */
	public static final int QQ_MEMBER_OBTAIN_PRIVILEGE_CONSUME_NOT_ENOUGH = 2;

	/**
	 * The privilege had full obtained.
	 */
	public static final int QQ_MEMBER_OBTAIN_PRIVILEGE_FULL = 3;

	// input
	private String memberKey;
	private double consumeAmt;
	private String posId;

	// output
	private String returnCode;
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

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public SmallNote getSmallNote() {
		return smallNote;
	}

	public void setSmallNote(SmallNote smallNote) {
		this.smallNote = smallNote;
	}
}
