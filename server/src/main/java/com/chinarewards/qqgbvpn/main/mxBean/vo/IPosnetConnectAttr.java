package com.chinarewards.qqgbvpn.main.mxBean.vo;

import java.util.Map;

public interface IPosnetConnectAttr {

	/**
	 * Get known pos client by pos id.
	 * 
	 * @param posId
	 * @return
	 */
	public OriginalKnownClient getKnownPosClientByPosId(String posId);

	/**
	 * Add new pos client!
	 * 
	 * @param client
	 */
	public void addNewPosClient(OriginalKnownClient client);

	public Map<String, KnownClient> getKnownClients();

	public int countKnownClients();

	public String getPosIdFromSessionId(String sessionId);

	public void addNewRoute(String sessionId, String posId);
}
