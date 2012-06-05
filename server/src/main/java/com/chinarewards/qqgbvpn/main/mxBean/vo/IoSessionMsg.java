package com.chinarewards.qqgbvpn.main.mxBean.vo;

import java.util.Date;

import org.apache.mina.core.session.IoSession;

public class IoSessionMsg {

	private IoSession ioSession;

	/**
	 * idle since time!
	 */
	private Date idleSince;

	private ConnStatus status;

	/**
	 * using for reset functional. After reset operation, the
	 * {@link IoSessionMsg#resetReveivedBytes} part should not put in the
	 * statistic data.
	 */
	private long resetReveivedBytes;

	private long resetSentBytes;

	public IoSession getIoSession() {
		return ioSession;
	}

	public void setIoSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}

	public ConnStatus getStatus() {
		return status;
	}

	public void setStatus(ConnStatus status) {
		this.status = status;
	}

	public long getResetReveivedBytes() {
		return resetReveivedBytes;
	}

	public void setResetReveivedBytes(long resetReveivedBytes) {
		this.resetReveivedBytes = resetReveivedBytes;
	}

	public long getResetSentBytes() {
		return resetSentBytes;
	}

	public void setResetSentBytes(long resetSentBytes) {
		this.resetSentBytes = resetSentBytes;
	}

	public Date getIdleSince() {
		return idleSince;
	}

	public void setIdleSince(Date idleSince) {
		this.idleSince = idleSince;
	}
}
