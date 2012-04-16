package com.chinarewards.qqgbvpn.main.qqadidas.vo;

public class QQMemberObtainGiftVo {

	/**
	 * Obtain gift successful!
	 */
	public static final int QQ_MEMBER_OBTAIN_GIFT_SUCCESS = 0;

	/**
	 * Invalid memberkey.
	 */
	public static final int QQ_MEMBER_OBTAIN_GIFT_INVALID_MEMBER = 1;

	/**
	 * Had obained already.
	 */
	public static final int QQ_MEMBER_OBTAIN_GIFT_ALREADY = 2;

	// input
	private String memberKey;
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