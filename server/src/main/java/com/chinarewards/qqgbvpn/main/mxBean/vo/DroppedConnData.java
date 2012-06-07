package com.chinarewards.qqgbvpn.main.mxBean.vo;

import java.io.Serializable;

public class DroppedConnData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 773176170139811449L;

	private String posId;

	private String ip;

	private DroppedReason reason;

	public DroppedConnData(String posId, String ip, DroppedReason reason) {
		this.posId = posId;
		this.ip = ip;
		this.reason = reason;
	}
}
