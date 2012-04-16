package com.chinarewards.qqgbvpn.main.qqadidas.vo;

import java.util.Date;

public class QQMemberObtainGiftVo {

	// input
	private String memberKey;
	private String posId;

	// output
	private int returnCode = -1;
	private Date operateTime;
	private SmallNote smallNote;

	public String getMemberKey() {
		return memberKey;
	}

	public void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
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

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
}