package com.chinarewards.qqgbvpn.main.protocol.filter;

import java.util.Date;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

import com.chinarewards.qqgbvpn.main.mxBean.vo.IPosnetConnectAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.OriginalKnownClient;
import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;
import com.chinarewards.qqgbvpn.main.protocol.cmd.InitRequestMessage;
import com.chinarewards.qqgbvpn.main.protocol.cmd.Message;
import com.chinarewards.utils.StringUtil;
import com.google.inject.Inject;

/**
 * This filter should be placed after an <code>ICommand</code> has been decoded.
 * 
 * @author yanxin
 * 
 */
public class JmxManageFilter extends IoFilterAdapter {

	@Inject
	private IPosnetConnectAttr connectAttr;

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception {

		// Add jmx
		ICommand msg = ((Message) message).getBodyMessage();
		if (msg instanceof InitRequestMessage) {
			// init
			InitRequestMessage initMsg = (InitRequestMessage) msg;
			String posId = initMsg.getPosId();
			OriginalKnownClient knownClient = connectAttr
					.getKnownPosClientByPosId(posId);
			Date now = new Date();
			String ip = session.getRemoteAddress().toString();
			if (knownClient != null) {
				knownClient.setIp(ip);
				knownClient.setLastConnectedAt(now);
				knownClient.setLastDataReceivedAt(now);
			} else {
				knownClient = new OriginalKnownClient();
				knownClient.setIp(ip);
				knownClient.setPosId(posId);
				knownClient.setLastConnectedAt(now);
				knownClient.setLastDataReceivedAt(now);
				connectAttr.addNewRoute(String.valueOf(session.getId()), posId);
				connectAttr.addNewPosClient(knownClient);
			}
		} else {
			// Others, get posid according to session id. If not, skip it.
			Date now = new Date();
			String posId = connectAttr.getPosIdFromSessionId(String
					.valueOf(session.getId()));
			if (!StringUtil.isEmptyString(posId)) {
				OriginalKnownClient knownClient = connectAttr
						.getKnownPosClientByPosId(posId);
				knownClient.setLastDataReceivedAt(now);
			}
		}

		nextFilter.messageReceived(session, message);
	}
}
