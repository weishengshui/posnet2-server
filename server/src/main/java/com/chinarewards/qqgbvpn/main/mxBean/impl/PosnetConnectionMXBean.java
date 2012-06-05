package com.chinarewards.qqgbvpn.main.mxBean.impl;

import java.util.List;
import java.util.Map;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.main.mxBean.IPosnetConnectionMXBean;
import com.chinarewards.qqgbvpn.main.mxBean.vo.IConnectionAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.IKnownClientConnectAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.KnownClient;
import com.chinarewards.qqgbvpn.main.mxBean.vo.OriginalKnownClient;
import com.google.inject.Inject;

public class PosnetConnectionMXBean extends NotificationBroadcasterSupport
		implements IPosnetConnectionMXBean {

	protected Logger log = LoggerFactory.getLogger(getClass());

	public static final String CORRUPT_DATA_CONNECTION_DROPPED = "CORRUPT_DATA_CONNECTION_DROPPED";
	public static final String IDLE_CONNECTION_DROPPED = "IDLE_CONNECTION_DROPPED";

	IConnectionAttr connectionAttr;
	IKnownClientConnectAttr knownClientConnectAttr;

	@Inject
	public PosnetConnectionMXBean(IConnectionAttr connectionAttr,
			IKnownClientConnectAttr posnetConnectAttr) {
		this.connectionAttr = connectionAttr;
		this.knownClientConnectAttr = posnetConnectAttr;
	}

	@Override
	public int getKnownClientCount() {
		return knownClientConnectAttr.countKnownClients();
	}

	@Override
	public Map<String, KnownClient> getKnownClients() {
		return knownClientConnectAttr.getKnownClients();
	}

	@Override
	public int getConnectionCount() {
		return connectionAttr.getConnectionCount();
	}

	@Override
	public int getActiveConnectionCount() {
		return connectionAttr.getActiveConnectionCount();
	}

	@Override
	public int getIdleConnectionCount() {
		return connectionAttr.getIdleConnectionCount();
	}

	@Override
	public long getBytesReceived() {
		return connectionAttr.getBytesReceived();
	}

	@Override
	public long getBytesSent() {
		return connectionAttr.getBytesSent();
	}

	@Override
	public void resetStatistics() {
		connectionAttr.resetStatData();
	}

	@Override
	public void closeIdleConnections() {
		closeIdleConnections(0);
	}

	@Override
	public void closeIdleConnections(int idleSeconds) {
		List<Long> ids = connectionAttr.closeIdleConnections(idleSeconds);
		for (Long id : ids) {
			knownClientConnectAttr.afterIdleClientClosed(id);
			notifyIdleConnectionDropped(id);
		}
	}

	// notification
	public void notifyIdleConnectionDropped(long sessionId) {
		String posId = knownClientConnectAttr.getPosIdFromSessionId(sessionId);
		if (posId != null) {
			OriginalKnownClient client = knownClientConnectAttr
					.getKnownPosClientByPosId(posId);
			if (client != null) {
				StringBuffer sb = new StringBuffer();
				sb.append("The pos client[posId=").append(client.getPosId())
						.append(", ip=").append(client.getIp())
						.append("] dropped because of idle!");
				log.debug("notify message:{}", sb.toString());
				Notification event = new Notification(IDLE_CONNECTION_DROPPED,
						this, sessionId, System.currentTimeMillis(),
						sb.toString());
				sendNotification(event);
			}
		}
	}

	public void notifyBadDataConnectionDroppped(long sessionId) {
		String posId = knownClientConnectAttr.getPosIdFromSessionId(sessionId);
		if (posId != null) {
			OriginalKnownClient client = knownClientConnectAttr
					.getKnownPosClientByPosId(posId);
			if (client != null) {
				StringBuffer sb = new StringBuffer();
				sb.append("The pos client[posId=").append(client.getPosId())
						.append(", ip=").append(client.getIp())
						.append("] dropped because of bad data!");
				Notification event = new Notification(
						CORRUPT_DATA_CONNECTION_DROPPED, this, sessionId,
						System.currentTimeMillis(), sb.toString());
				sendNotification(event);
			}
		}
	}

}
