package com.chinarewards.qqgbvpn.main.mxBean.vo;

import java.util.List;

import org.apache.mina.core.session.IoSession;

public interface IConnectionAttr {

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
	 * Remove connection from map when the session is closed!
	 */
	public void removeConnection(long sessionId);

	/**
	 * create a new session in map.
	 * 
	 * @param session
	 */
	public void newConnection(IoSession session);

	/**
	 * Active a connection. When a connection was active, make it active status.
	 * 
	 * @param sessionId
	 */
	public void activeConnection(long sessionId);

	/**
	 * Idle a connection. When a connection was idle, make it idle status.
	 * 
	 * @param sessionId
	 */
	public void idleConnection(long sessionId);

	/**
	 * Reset statistic data, eg.. received bytes count and sent bytes count .
	 */
	public void resetStatData();

	/**
	 * Close those connections who achieve idleSeconds. If idleSeconds is 0,
	 * close all.
	 * 
	 * @param idleSeconds
	 * @return
	 */
	public List<Long> closeIdleConnections(int idleSeconds);
}
