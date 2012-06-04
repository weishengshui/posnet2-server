package com.chinarewards.qqgbvpn.main.protocol.filter;

import java.util.Date;
import java.util.Map;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.common.DateTool;
import com.chinarewards.qqgbvpn.main.SessionStore;
import com.chinarewards.qqgbvpn.main.mxBean.vo.IPosnetConnectAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.OriginalKnownClient;
import com.chinarewards.qqgbvpn.main.util.MinaUtil;
import com.chinarewards.utils.StringUtil;

/**
 * Kills idle connections.
 * 
 * @author dengrenwen
 * @since 0.1.0
 */
public class IdleConnectionKillerFilter extends AbstractFilter {

	Logger log = LoggerFactory.getLogger(getClass());

	final SessionStore sessionStore;
	final IPosnetConnectAttr connectAttr;
	
	// 闲置了idleKillerTime毫秒就关闭连接
	private long idleKillerTime;

	public IdleConnectionKillerFilter(SessionStore sessionStore, IPosnetConnectAttr connectAttr,
			long idleKillerTime) {
		this.sessionStore = sessionStore;
		this.connectAttr = connectAttr;
		// time millisecond
		this.idleKillerTime = idleKillerTime * 1000;
	}

	@Override
	public void sessionIdle(NextFilter nextFilter, IoSession session,
			IdleStatus status) throws Exception {

		// 第一次闲置触发就设置闲置开始时间
		if (session.getAttribute("startIdleTime") == null) {
			session.setAttribute("startIdleTime", System.currentTimeMillis());
		} else {
			// 判断闲置时间是否到了可以关闭的条件
			long startTime = Long.valueOf(session.getAttribute("startIdleTime")
					.toString());
			if (System.currentTimeMillis() - startTime >= idleKillerTime) {
				if (log.isDebugEnabled()) {
					log.debug(
							"Connection idle too long, closing... (addr: {}, session ID: {}, POS ID: {})",
							new Object[] {
									MinaUtil.buildAddressPortString(session),
									session.getId(),
									MinaUtil.getPosIdFromSession(getServerSession(
											session, sessionStore)) });
				}

				if (session
						.containsAttribute(SessionKeyMessageFilter.SESSION_ID)) {
					// 在连线断开的时间情况session key的信息，当然是在session key过期的情况下
					sessionStore.expiredSession((String) session
							.getAttribute(SessionKeyMessageFilter.SESSION_ID));

				}
				session.close(true);

				// jmx record when close session!
				String posId = connectAttr.getPosIdFromSessionId(String
						.valueOf(session.getId()));
				if (!StringUtil.isEmptyString(posId)) {
					OriginalKnownClient knownClient = connectAttr
							.getKnownPosClientByPosId(posId);
					if (knownClient != null) {
						Map<String, Integer> idleCount = knownClient
								.getIdleConnectionDropCount();
						Date now = new Date();
						String s = DateTool.getSingleStr(now);
						if (idleCount.containsKey(s)) {
							int i = idleCount.get(s);
							idleCount.put(s, i++);
						} else {
							idleCount.put(s, 1);
						}
					}
				}

				log.trace("sessionIdle() done");
			}
		}

		nextFilter.sessionIdle(session, status);

	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception {

		if (session.getAttribute("startIdleTime") != null) {
			session.removeAttribute("startIdleTime");
		}

		nextFilter.messageReceived(session, message);
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session,
			WriteRequest writeRequest) throws Exception {

		if (session.getAttribute("startIdleTime") != null) {
			session.removeAttribute("startIdleTime");
		}

		nextFilter.messageSent(session, writeRequest);

	}

}
