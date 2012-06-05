package com.chinarewards.qqgbvpn.main.mxBean;

import java.util.Map;

import com.chinarewards.qqgbvpn.main.mxBean.vo.KnownClient;

/**
 * 
 * @author yanxin
 * @since 0.3.3
 */
public interface IPosnetConnectionMXBean {

	// show data
	/**
	 * Get current connection count number!
	 * 
	 * @return
	 */
	public int getConnectionCount();

	/**
	 * Get active connection count number
	 * 
	 * @return
	 */
	public int getActiveConnectionCount();

	/**
	 * Get idle connection count number
	 * 
	 * @return
	 */
	public int getIdleConnectionCount();

	/**
	 * get total received bytes
	 * 
	 * @return
	 */
	public long getBytesReceived();

	/**
	 * get total sent bytes
	 * 
	 * @return
	 */
	public long getBytesSent();

	/**
	 * Get known client count number.
	 * 
	 * @return
	 */
	public int getKnownClientCount();

	/**
	 * Get known client data detail.
	 * 
	 * @return
	 */
	public Map<String, KnownClient> getKnownClients();

	// operation
	public void resetStatistics();

	public void closeIdleConnections();

	public void closeIdleConnections(int idleSeconds);
}
