package com.chinarewards.qqgbvpn.main.qqadidas.vo;

public class GiftReceiptGenModel {

	private int returnCode = -1;

	private String memberKey;

	public GiftReceiptGenModel(int returnCode, String memberKey) {
		this.returnCode = returnCode;
		this.memberKey = memberKey;
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
}
