package com.chinarewards.qqgbvpn.main.mxBean.vo;

import java.util.Date;

/**
 * Store the known client parameters, the data is calculate from
 * {@link OriginalKnownClient} and display at mxbean.
 * 
 * @author yanxin
 * @since 0.3.3
 */
public class KnownClient {
	private String ip;
	private Date lastConnectedAt;
	private Date lastDataReceivedAt;
	private StatCountByTime corruptDataConnectionDropStats;
	private StatCountByTime idleConnectionDropStats;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getLastConnectedAt() {
		return lastConnectedAt;
	}

	public void setLastConnectedAt(Date lastConnectedAt) {
		this.lastConnectedAt = lastConnectedAt;
	}

	public Date getLastDataReceivedAt() {
		return lastDataReceivedAt;
	}

	public void setLastDataReceivedAt(Date lastDataReceivedAt) {
		this.lastDataReceivedAt = lastDataReceivedAt;
	}

	public StatCountByTime getCorruptDataConnectionDropStats() {
		return corruptDataConnectionDropStats;
	}

	public void setCorruptDataConnectionDropStats(
			StatCountByTime corruptDataConnectionDropStats) {
		this.corruptDataConnectionDropStats = corruptDataConnectionDropStats;
	}

	public StatCountByTime getIdleConnectionDropStats() {
		return idleConnectionDropStats;
	}

	public void setIdleConnectionDropStats(
			StatCountByTime idleConnectionDropStats) {
		this.idleConnectionDropStats = idleConnectionDropStats;
	}
}
