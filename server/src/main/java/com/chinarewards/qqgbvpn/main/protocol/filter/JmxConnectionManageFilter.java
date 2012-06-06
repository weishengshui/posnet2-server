package com.chinarewards.qqgbvpn.main.protocol.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.main.mxBean.vo.IConnectionAttr;
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
		nextFilter.messageReceived(session, message);

		log.debug("JmxConnectionManageFilter#messageReceived() end!");
	}
}
