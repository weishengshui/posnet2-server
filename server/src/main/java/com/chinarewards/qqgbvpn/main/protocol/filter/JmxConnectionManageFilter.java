package com.chinarewards.qqgbvpn.main.protocol.filter;

import java.util.Date;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.main.mxBean.vo.IConnectionAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.IKnownClientConnectAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.OriginalKnownClient;
import com.chinarewards.utils.StringUtil;
import com.google.inject.Inject;

/**
 * 
 * @author yanxin
 * 
 */
public class JmxConnectionManageFilter extends IoFilterAdapter {

	Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private IConnectionAttr connectionAttr;
	
	@Inject
	private IKnownClientConnectAttr knownClientConnectAttr;

	@Override
	public void sessionIdle(NextFilter nextFilter, IoSession session,
			IdleStatus status) throws Exception {
		connectionAttr.idleConnection(session.getId());
		nextFilter.sessionIdle(session, status);
	}

	@Override
	public void sessionOpened(NextFilter nextFilter, IoSession session)
			throws Exception {
		connectionAttr.newConnection(session);
		nextFilter.sessionOpened(session);
	}

	@Override
	public void sessionClosed(NextFilter nextFilter, IoSession session)
			throws Exception {
		connectionAttr.removeConnection(session.getId());
		nextFilter.sessionClosed(session);
	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception {
		log.debug("JmxConnectionManageFilter#messageReceived() begin!");
		// change connection to active
		connectionAttr.activeConnection(session.getId());
		
		// Others, get posid according to session id. If not, skip it.
		Date now = new Date();
		String posId = knownClientConnectAttr.getPosIdFromSessionId(session
				.getId());
		if (!StringUtil.isEmptyString(posId)) {
			log.debug("Found posId={}", posId);
			OriginalKnownClient knownClient = knownClientConnectAttr
					.getKnownPosClientByPosId(posId);
			knownClient.setLastDataReceivedAt(now);
		}
		
		nextFilter.messageReceived(session, message);

		log.debug("JmxConnectionManageFilter#messageReceived() end!");
	}
}
