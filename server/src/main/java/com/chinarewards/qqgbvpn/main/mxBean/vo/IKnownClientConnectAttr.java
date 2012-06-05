package com.chinarewards.qqgbvpn.main.mxBean.vo;

import java.util.Map;

/**
 * Known clients attributions
 * 
 * @author yanxin
 * @since 0.3.3
 */
public interface IKnownClientConnectAttr {

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

	public String getPosIdFromSessionId(long sessionId);

	public void addNewRoute(long sessionId, String posId);

	public void afterIdleClientClosed(long sessionId);

	public void afterBadDataClientClosed(long sessionId);

}
