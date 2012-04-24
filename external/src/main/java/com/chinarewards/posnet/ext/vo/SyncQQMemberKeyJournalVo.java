package com.chinarewards.posnet.ext.vo;

import com.chinarewards.qqgbvpn.domain.event.Journal;

/**
 * Defines the attributes of {@link Journal#eventDetail} when event
 * {SYNC_QQ_MEMBER_KEY_OK or SYNC_QQ_MEMBER_KEY_FAILED} happens.
 * 
 * @author yanxin
 * @since 0.3.0
 */
public class SyncQQMemberKeyJournalVo {
	/**
	 * Raw request body content from QQ.
	 */
	private String rawRequestCtn;

	/**
	 * Raw response body content to QQ.
	 */
	private String rawResponseCtn;

	/**
	 * The stack trace when error appear.
	 */
	private String errorStackTrace;

	/**
	 * Member key which is decrypted from rawRequestCtn.
	 */
	private String memberKey;

	/**
	 * Timestamp which is decrypted from rawRequestCtn.
	 */
	private String timestamp;

	public String getRawRequestCtn() {
		return rawRequestCtn;
	}

	public void setRawRequestCtn(String rawRequestCtn) {
		this.rawRequestCtn = rawRequestCtn;
	}

	public String getRawResponseCtn() {
		return rawResponseCtn;
	}

	public void setRawResponseCtn(String rawResponseCtn) {
		this.rawResponseCtn = rawResponseCtn;
	}

	public String getErrorStackTrace() {
		return errorStackTrace;
	}

	public void setErrorStackTrace(String errorStackTrace) {
		this.errorStackTrace = errorStackTrace;
	}

	public String getMemberKey() {
		return memberKey;
	}

	public void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
