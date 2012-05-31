package com.chinarewards.qqgbvpn.main.exception.qqadidas;

import java.util.Date;

/**
 * The member had obtained a gift already.
 * 
 * @author yanxin
 * @since 0.3.0
 */
public class GiftObtainedAlreadyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3203116604599308090L;

	Date lastObtainedTime;

	public GiftObtainedAlreadyException(Date lastObtainedTime) {
		this.lastObtainedTime = lastObtainedTime;
	}

	public Date getLastObtainedTime() {
		return lastObtainedTime;
	}
}
