package com.chinarewards.qqgbvpn.main.qqadidas.vo;

import java.util.Date;

public class GiftScreenDisplayGenModel {

	private int returnCode = -1;
	private String memberKey;
	private Date lastObtainedTime;

	public GiftScreenDisplayGenModel(int returnCode, String memberKey,
			Date lastObtainedTime) {
		this.returnCode = returnCode;
		this.memberKey = memberKey;
		this.lastObtainedTime = lastObtainedTime;
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

	public Date getLastObtainedTime() {
		return lastObtainedTime;
	}

	public void setLastObtainedTime(Date lastObtainedTime) {
		this.lastObtainedTime = lastObtainedTime;
	}
}