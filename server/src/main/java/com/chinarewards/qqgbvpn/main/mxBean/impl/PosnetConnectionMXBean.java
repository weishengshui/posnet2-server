package com.chinarewards.qqgbvpn.main.mxBean.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.main.mxBean.IPosnetConnectionMXBean;
import com.chinarewards.qqgbvpn.main.mxBean.vo.DroppedReason;
import com.chinarewards.qqgbvpn.main.mxBean.vo.IConnectionAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.IKnownClientConnectAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.KnownClient;
import com.chinarewards.qqgbvpn.main.mxBean.vo.OriginalKnownClient;
import com.google.inject.Inject;

public class PosnetConnectionMXBean extends NotificationBroadcasterSupport
		implements IPosnetConnectionMXBean {

	protected Logger log = LoggerFactory.getLogger(getClass());

	public static final String CONNECTION_DROPPED = "CONNECTION_DROPPED";

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
				ObjectMapper mapper = new ObjectMapper();
				String msg = null;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("posId", client.getPosId());
				map.put("ip", client.getIp());
				map.put("reason", DroppedReason.IDLE);
				try {
					msg = mapper.writeValueAsString(map);
				} catch (Exception e) {
					log.error("convert map to json error.", e);
				}
				log.debug("notify message:{}", msg);
				Notification event = new Notification(CONNECTION_DROPPED, this,
						sessionId, System.currentTimeMillis(), msg);
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
				ObjectMapper mapper = new ObjectMapper();
				String msg = null;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("posId", client.getPosId());
				map.put("ip", client.getIp());
				map.put("reason", DroppedReason.BADDATA);
				try {
					msg = mapper.writeValueAsString(map);
				} catch (Exception e) {
					log.error("convert map to json error.", e);
				}
				log.debug("bad data notify message:{}", msg);
				Notification event = new Notification(CONNECTION_DROPPED, this,
						sessionId, System.currentTimeMillis(), msg);
				sendNotification(event);
			}
		}
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {
		String[] types = new String[] { CONNECTION_DROPPED };

		String name = Notification.class.getName();
		String description = "Connection dropped notification";
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name,
				description);
		return new MBeanNotificationInfo[] { info };
	}

}
