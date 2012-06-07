package com.chinarewards.qqgbvpn.main.protocol.filter;

import java.util.Date;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.main.mxBean.vo.IKnownClientConnectAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.OriginalKnownClient;
import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;
import com.chinarewards.qqgbvpn.main.protocol.cmd.InitRequestMessage;
import com.chinarewards.qqgbvpn.main.protocol.cmd.Message;
import com.google.inject.Inject;

/**
 * Why do not put this filter into {@link JmxConnectionManageFilter} ? <br/>
 * Because this filter must be placed after an <code>ICommand</code> has been
 * decoded. It is different from {@link JmxConnectionManageFilter}.
 * 
 * @author yanxin
 * 
 */
public class JmxKnownClientConnectFilter extends IoFilterAdapter {

	Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private IKnownClientConnectAttr knownClientConnectAttr;

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception {
		log.debug("JmxKnownClientConnectFilter#messageReceived() begin!");

		// known client operation!
		ICommand msg = ((Message) message).getBodyMessage();
		if (msg instanceof InitRequestMessage) {
			// init
			InitRequestMessage initMsg = (InitRequestMessage) msg;
			String posId = initMsg.getPosId();
			OriginalKnownClient knownClient = knownClientConnectAttr
					.getKnownPosClientByPosId(posId);
			Date now = new Date();
			String ip = session.getRemoteAddress().toString().substring(1);
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
				knownClientConnectAttr.addNewPosClient(knownClient);
			}
			knownClientConnectAttr.addNewRoute(session.getId(), posId);
		} else {
			// Code moved to JmxConnectionManageFilter
			// Others, get posid according to session id. If not, skip it.
			// Date now = new Date();
			// String posId =
			// knownClientConnectAttr.getPosIdFromSessionId(session
			// .getId());
			// if (!StringUtil.isEmptyString(posId)) {
			// OriginalKnownClient knownClient = knownClientConnectAttr
			// .getKnownPosClientByPosId(posId);
			// knownClient.setLastDataReceivedAt(now);
			// }
		}

		nextFilter.messageReceived(session, message);

		log.debug("JmxKnownClientConnectFilter#messageReceived() end!");
	}
}
