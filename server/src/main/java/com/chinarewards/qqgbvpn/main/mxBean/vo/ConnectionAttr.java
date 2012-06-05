package com.chinarewards.qqgbvpn.main.mxBean.vo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionAttr implements IConnectionAttr {

	public Logger log = LoggerFactory.getLogger(getClass());

	// store all open session messages
	private HashMap<Long, IoSessionMsg> sessionMap = new HashMap<Long, IoSessionMsg>();

	// receive bytes count
	private long closedConnectionReceivedBytes;
	// send bytes count
	private long closedConnectionSentBytes;

	@Override
	public int getConnectionCount() {
		return sessionMap.keySet().size();
	}

	// get connection count number by specified status.
	private int getConnectCountByStatus(ConnStatus status) {
		int count = 0;
		Iterator<Long> it = sessionMap.keySet().iterator();
		while (it.hasNext()) {
			long sessionId = it.next();
			if (sessionMap.containsKey(sessionId)) {
				if (status == sessionMap.get(sessionId).getStatus()) {
					count++;
				}
			}
		}

		return count;
	}

	@Override
	public int getActiveConnectionCount() {
		return getConnectCountByStatus(ConnStatus.ACTIVE);
	}

	@Override
	public int getIdleConnectionCount() {
		return getConnectCountByStatus(ConnStatus.IDLE);
	}

	@Override
	public long getBytesReceived() {
		long receivedBytes = closedConnectionReceivedBytes;
		Iterator<Long> it = sessionMap.keySet().iterator();
		while (it.hasNext()) {
			IoSessionMsg msg = sessionMap.get(it.next());
			if (msg != null && msg.getIoSession() != null) {
				receivedBytes += (msg.getIoSession().getReadBytes() - msg
						.getResetReveivedBytes());
			}
		}
		return receivedBytes;
	}

	@Override
	public long getBytesSent() {
		long sentBytes = closedConnectionSentBytes;
		Iterator<Long> it = sessionMap.keySet().iterator();
		while (it.hasNext()) {
			IoSessionMsg msg = sessionMap.get(it.next());
			if (msg != null && msg.getIoSession() != null) {
				sentBytes += (msg.getIoSession().getWrittenBytes() - msg
						.getResetSentBytes());
			}
		}
		return sentBytes;
	}

	@Override
	public void removeConnection(long sessionId) {
		IoSessionMsg msg = sessionMap.get(sessionId);
		if (msg != null && msg.getIoSession() != null) {
			closedConnectionReceivedBytes += (msg.getIoSession().getReadBytes() - msg
					.getResetReveivedBytes());
			closedConnectionSentBytes += (msg.getIoSession().getWrittenBytes() - msg
					.getResetSentBytes());
		}

		sessionMap.remove(sessionId);
	}

	@Override
	public void activeConnection(long sessionId) {
		if (sessionMap.containsKey(sessionId)) {
			IoSessionMsg msg = sessionMap.get(sessionId);
			msg.setStatus(ConnStatus.ACTIVE);
		}
	}

	@Override
	public void idleConnection(long sessionId) {
		Date now = new Date();
		if (sessionMap.containsKey(sessionId)) {
			IoSessionMsg msg = sessionMap.get(sessionId);
			if (msg.getStatus() != ConnStatus.IDLE) {
				msg.setStatus(ConnStatus.IDLE);
				msg.setIdleSince(now);
			}
		}
	}

	@Override
	public void newConnection(IoSession session) {
		IoSessionMsg msg = new IoSessionMsg();
		msg.setIoSession(session);
		msg.setStatus(ConnStatus.ACTIVE);
		sessionMap.put(session.getId(), msg);
	}

	@Override
	public void resetStatData() {
		closedConnectionReceivedBytes = 0L;
		closedConnectionSentBytes = 0L;
		Iterator<Long> it = sessionMap.keySet().iterator();
		while (it.hasNext()) {
			IoSessionMsg msg = sessionMap.get(it.next());
			if (msg != null && msg.getIoSession() != null) {
				msg.setResetReveivedBytes(msg.getIoSession().getReadBytes());
				msg.setResetSentBytes(msg.getIoSession().getWrittenBytes());
			}
		}
	}

	@Override
	public List<Long> closeIdleConnections(int idleSeconds) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, -idleSeconds);
		Date sinceIdleTime = c.getTime();
		List<Long> list = new ArrayList<Long>();
		Iterator<Long> it = sessionMap.keySet().iterator();
		while (it.hasNext()) {
			long sid = it.next();
			IoSessionMsg msg = sessionMap.get(sid);
			if (msg != null && msg.getIoSession() != null) {
				log.trace("sinceIdleTime ={}, idleSince={}", new Object[] {
						sinceIdleTime, msg.getIdleSince() });
				if (msg.getStatus() == ConnStatus.IDLE
						&& (sinceIdleTime.equals(msg.getIdleSince()) || sinceIdleTime
								.after(msg.getIdleSince()))) {
					log.trace("kill--{}", msg.getIoSession().getId());
					msg.getIoSession().close(true);
					list.add(sid);
				}
			}
		}
		return list;
	}

}
