package com.chinarewards.qqgbvpn.main.mxBean.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Store the original known client parameters.
 * 
 * @author yanxin
 * @since 0.3.3
 */
public class OriginalKnownClient {
	private String posId;
	private String ip;
	private Date lastConnectedAt;
	private Date lastDataReceivedAt;

	// Key String - yyyy-MM-dd
	private Map<String, Integer> corruptDataConnectionDropCount = new HashMap<String, Integer>();
	private Map<String, Integer> idleConnectionDropCount = new HashMap<String, Integer>();

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

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

	public Map<String, Integer> getCorruptDataConnectionDropCount() {
		return corruptDataConnectionDropCount;
	}

	public void setCorruptDataConnectionDropCount(
			Map<String, Integer> corruptDataConnectionDropCount) {
		this.corruptDataConnectionDropCount = corruptDataConnectionDropCount;
	}

	public Map<String, Integer> getIdleConnectionDropCount() {
		return idleConnectionDropCount;
	}

	public void setIdleConnectionDropCount(
			Map<String, Integer> idleConnectionDropCount) {
		this.idleConnectionDropCount = idleConnectionDropCount;
	}
}
