package com.chinarewards.qqgbvpn.main.mxBean.vo;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.common.DateTool;
import com.chinarewards.utils.StringUtil;

public class KnownClientConnectAttr implements IKnownClientConnectAttr {

	Logger log = LoggerFactory.getLogger(getClass());

	private Map<String, OriginalKnownClient> knownClients = new ConcurrentHashMap<String, OriginalKnownClient>();

	// route from sessionid to posid
	private Map<Long, String> posIdRouteMap = new ConcurrentHashMap<Long, String>();

	@Override
	public OriginalKnownClient getKnownPosClientByPosId(String posId) {
		return knownClients.get(posId);
	}

	@Override
	public void addNewPosClient(OriginalKnownClient client) {
		if (!knownClients.containsKey(client.getPosId())) {
			knownClients.put(client.getPosId(), client);
		}
	}

	@Override
	public Map<String, KnownClient> getKnownClients() {
		Map<String, KnownClient> clients = new HashMap<String, KnownClient>();
		Iterator<String> it = knownClients.keySet().iterator();
		Date now = new Date();
		while (it.hasNext()) {
			OriginalKnownClient originalClient = knownClients.get(it.next());
			KnownClient client = new KnownClient();
			client.setIp(originalClient.getIp());
			client.setLastConnectedAt(originalClient.getLastConnectedAt());
			client.setLastDataReceivedAt(originalClient.getLastDataReceivedAt());
			client.setCorruptDataConnectionDropStats(getStatCountFromDateMap(
					originalClient.getCorruptDataConnectionDropCount(), now));
			client.setIdleConnectionDropStats(getStatCountFromDateMap(
					originalClient.getIdleConnectionDropCount(), now));
			clients.put(originalClient.getPosId(), client);
		}
		return clients;
	}

	private StatCountByTime getStatCountFromDateMap(Map<String, Integer> list,
			Date currentTime) {
		StatCountByTime statCount = new StatCountByTime();
		int total = 0;
		int today = 0;
		int last7Days = 0;
		// today
		String todayFmt = DateTool.getSingleStr(currentTime);
		if (list.containsKey(todayFmt)) {
			today = list.get(todayFmt);
		}

		// last 7 days
		Calendar c = Calendar.getInstance();
		c.setTime(currentTime);
		last7Days += today;
		for (int i = 1; i <= 6; i++) {
			c.add(Calendar.DAY_OF_MONTH, -i);
			String dayFmt = DateTool.getSingleStr(c.getTime());
			if (list.containsKey(dayFmt)) {
				last7Days += list.get(dayFmt);
			}
		}

		// total
		Iterator<String> it = list.keySet().iterator();
		while (it.hasNext()) {
			String day = it.next();
			total += list.get(day);
		}

		statCount.setToday(today);
		statCount.setLast7Days(last7Days);
		statCount.setTotal(total);

		return statCount;
	}

	@Override
	public int countKnownClients() {
		return knownClients.keySet().size();
	}

	@Override
	public String getPosIdFromSessionId(long sessionId) {
		return posIdRouteMap.get(sessionId);
	}

	@Override
	public void addNewRoute(long sessionId, String posId) {
		posIdRouteMap.put(sessionId, posId);
	}

	@Override
	public void afterIdleClientClosed(long sessionId) {
		String posId = getPosIdFromSessionId(sessionId);
		if (!StringUtil.isEmptyString(posId)) {
			OriginalKnownClient knownClient = getKnownPosClientByPosId(posId);
			if (knownClient != null) {
				Map<String, Integer> idleCount = knownClient
						.getIdleConnectionDropCount();
				Date now = new Date();
				String s = DateTool.getSingleStr(now);
				if (idleCount.containsKey(s)) {
					int i = idleCount.get(s);
					idleCount.put(s, i + 1);
				} else {
					idleCount.put(s, 1);
				}
			}
		}
	}

	@Override
	public void afterBadDataClientClosed(long sessionId) {
		String posId = getPosIdFromSessionId(sessionId);
		log.debug("afterBadDataClientClosed found posId={}", posId);
		if (!StringUtil.isEmptyString(posId)) {
			OriginalKnownClient knownClient = getKnownPosClientByPosId(posId);
			if (knownClient != null) {
				Map<String, Integer> dataErrorCount = knownClient
						.getCorruptDataConnectionDropCount();
				Date now = new Date();
				String s = DateTool.getSingleStr(now);
				if (dataErrorCount.containsKey(s)) {
					int i = dataErrorCount.get(s);
					dataErrorCount.put(s, i + 1);
				} else {
					dataErrorCount.put(s, 1);
				}
			} else {
				log.debug("strange, can not found knownclient!");
			}
		}
	}
}
