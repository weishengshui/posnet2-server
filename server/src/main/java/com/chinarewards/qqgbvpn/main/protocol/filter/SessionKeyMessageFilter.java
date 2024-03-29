package com.chinarewards.qqgbvpn.main.protocol.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.main.Session;
import com.chinarewards.qqgbvpn.main.SessionIdGenerator;
import com.chinarewards.qqgbvpn.main.SessionStore;
import com.chinarewards.qqgbvpn.main.protocol.cmd.HeadMessage;
import com.chinarewards.qqgbvpn.main.protocol.cmd.Message;
import com.chinarewards.qqgbvpn.main.session.ISessionKey;
import com.chinarewards.qqgbvpn.main.session.v1.V1SessionKey;
import com.chinarewards.qqgbvpn.main.util.MinaUtil;

/**
 * 
 * 
 * @author Cyril
 * @since 0.1.0
 */
public class SessionKeyMessageFilter extends IoFilterAdapter {

	/**
	 * The key name which the session ID will be stored in the Mina's session.
	 * <p>
	 * The content is expected to be a string session ID.
	 */
	public static final String SESSION_ID = SessionKeyMessageFilter.class
			.getName() + ".SESSION_ID";
	
	/**
	 * 这个是用来标识store session 的最后活动时间
	 */
	public static final String LAST_ACCESS_TIME = SessionKeyMessageFilter.class
			.getName() + ".LAST_ACCESS_TIME";

	/**
	 * The key name which the session ID will be stored in the Mina's session.
	 * <p>
	 * The content is expected to be a string session ID.
	 */
	public static final String CLIENT_SUPPORT_SESSION_ID = SessionKeyMessageFilter.class
			.getName() + ".CLIENT_SUPPORT_SESSION_ID";

	/**
	 * Whether the server should include the session ID in the header as part
	 * of the response when a message is to be sent.
	 */
	public static final String RETURN_SESSION_ID_TO_CLIENT = SessionKeyMessageFilter.class
			.getName() + ".RETURN_SESSION_ID_TO_CLIENTSESSION_ID";
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	final SessionStore sessionStore;
	
	final SessionIdGenerator sessionIdGenerator;
	
	public SessionKeyMessageFilter(SessionStore sessionStore,
			SessionIdGenerator sessionIdGenerator) {
		// XXX make this injected
		this.sessionStore = sessionStore;
		// XXX make this injected
		this.sessionIdGenerator = sessionIdGenerator;
	}
	
	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) {
		log.debug("SessionKeyMessageFilter#messageReceived() begin!");
		
		boolean createSession = false;
		boolean generateSessionKey = false;
		boolean markSendSessionIdToClient = false;
		String sessionId = null;
		Session serverSession = null;

		// get the protocol message.
		Message posMsg = (Message) message;
		HeadMessage header = posMsg.getHeadMessage();

		// 这个过滤器是用了处理有session key 的client的请求
		// 也就是说，如果没有session key 功能的client不用走
		if ((header.getFlags() & HeadMessage.FLAG_SESSION_ID) == 0) {
			if(!serverHasSessionId(session) || !sessionStore.SessionStoreContainsKey(MinaUtil.getServerSessionId(session))){
				createSession = true;
				generateSessionKey = true;
			}
		} else {

			ISessionKey sessionKey = header.getSessionKey();

			// shows a debug message if the session key is gone unexpectedly.
			// client告诉server这次连接的POS机可以处理session key信息
			// 所以在这次请求的回复中server就会根据这里在session记录的状态，进行回复session key
			if ((header.getFlags() & HeadMessage.FLAG_SESSION_ID) != 0) {
				// no session key found... must have some problem decoding in
				// the
				// previous state.
				log.info(
						"Mina ID {}: no session key in header but marked as containing "
								+ "session key, should be corrupted when decoding. "
								+ "Assuming no session key.", session.getId());

				log.debug("marking client supports session ID");
				session.setAttribute(CLIENT_SUPPORT_SESSION_ID);
			}

			// act accordingly.
			if (sessionKey == null) {

				// case: No session key found in client message.
				if (!serverHasSessionId(session)) {
					// 开机第一次请求会出现的情况
					// client and server has no session key. create one.
					log.debug(
							"Mina session ID {}: client message and server has no session ID, will create one",
							session.getId());
					createSession = true;
					generateSessionKey = true;
					markSendSessionIdToClient = true;
				} else {
					// 这个是正常请求时，client有sessionkey 但是不需要传
					log.debug("Mina session ID {}: existing session ID = {}",
							session.getId(), getServerSessionId(session));
				}

			} else {
				// 重连接，client端有session key
				// case: session key is present in client's message.

				// FIXME 2011-11-02 cyril: need to validate the client's session
				// key!

				// FIXME: 2011-11-02 cyril: need to guard against unsupported
				// session key!

				sessionId = sessionKey.getSessionId();
				log.debug("received session key ={}",sessionId);
				// 正常的重连接，sessionIo里面是没有session id的
				if (!serverHasSessionId(session)) {
					// for a new client connection, we need to save that session
					// ID on server side once the session key is identified as
					// valid. This is required since the session key MAY NOT
					// be present in later messages.
					//
					// We validate by from session store using client' session
					// key.
					// If found, load it, and save the session key into Mina.
					// Otherwise, server will generate a new session key.

					serverSession = sessionStore.getSession(sessionId);
					if (serverSession == null) {
						// no session found in session store
						createSession = true;
						generateSessionKey = true;
						markSendSessionIdToClient = true;
						log.debug(
								"session invalid: no session found with key {}, will create one",
								sessionKey);
					} else {
						// session found in session store
						saveSessionIdToServer(session, sessionId);
						markSendSessionIdToClient = true;
						log.debug("session restored with key {}", sessionKey);
					}

				} else {

					//
					// Case: Server already has a session key (in Mina's
					// session)
					//

					// make sure client's and server's session key match!
					String serverSessionId = getServerSessionId(session);
					markSendSessionIdToClient = true;
					//如果传送过来的数据包里面的session key  和 连线connection里面带的session key 不相等，或者session store里面没有，则要重新登录
					if (!sessionId.equals(serverSessionId) || sessionStore.getSession(serverSessionId) == null) {

						// session key NOT match

						if (log.isInfoEnabled()) {
							log.info(
									"Mina session ID {}: Client session key does not match server's session key"
											+ " ({} vs {}), will create new session",
									new Object[] { session.getId(),
											sessionKey.getSessionId(),
											serverSessionId });
						}

						createSession = true;
						generateSessionKey = true;
						markSendSessionIdToClient = true;

					} else {

						// session key matched!
						log.debug(
								"Mina session ID {}: client and server session key matched",
								session.getId());

					}

				}

			}
		}

		// generate a session ID, also mark the session ID should be sent to
		// the client.
		if (generateSessionKey) {
			// generate session id
			sessionId = sessionIdGenerator.generate();

			log.debug("Mina session ID {}: created session key {}",
					session.getId(), sessionId);
		}

		// if no session is present finally, we need to create one.
		if (createSession) {

			log.debug(
					"creating session for mina session ID {} using session ID {}",
					session.getId(), sessionId);

			// create a session.
			serverSession = sessionStore.createSession(sessionId);
			//创建的时候记录最后一个使用的时间
			serverSession.setAttribute(LAST_ACCESS_TIME, System.currentTimeMillis());
			// save the session key to Mina's session such that it can
			// be retrieved later.
			saveSessionIdToServer(session, sessionId);

		}

		if (markSendSessionIdToClient) {
			markSendSessionIdToClient(session);
		}

		nextFilter.messageReceived(session, message);

		log.debug("SessionKeyMessageFilter#messageReceived() end!");

	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.mina.core.filterchain.IoFilterAdapter#messageSent(org.apache.mina.core.filterchain.IoFilter.NextFilter, org.apache.mina.core.session.IoSession, org.apache.mina.core.write.WriteRequest)
	 */
	@Override
	public void messageSent(NextFilter nextFilter, IoSession session,
			WriteRequest writeRequest) throws Exception {
		log.trace("messageSent() invoked");
		
		//检查是否要发送session key 的信息给client
		if (isMarkSendSessionIdToClient(session)) {
			
			if (session.containsAttribute(CLIENT_SUPPORT_SESSION_ID)) {
			
				log.trace(
						"Mina session ID {}: marked to send session ID back to client",
						session.getId());
				
				// choose a encoder to encode the message
				V1SessionKey key = new V1SessionKey(getServerSessionId(session));
				
				// update the flag and the session key.
				Object rawMessage = writeRequest.getMessage();
				if(rawMessage instanceof Message){
					Message posMsg = (Message)rawMessage;
					HeadMessage header = posMsg.getHeadMessage();
					header.setSessionKey(key);
					header.setFlags(header.getFlags() | HeadMessage.FLAG_SESSION_ID);
				}
			
				
			}
			//发送完成之后就把标记去掉
			removeMarkSendSessionIdToClient(session);
		} else {
			log.trace(
					"Mina session ID {}: not mark to return session ID to client",
					session.getId());
		}
		
        nextFilter.messageSent(session, writeRequest);
        
		log.trace("messageSent() done");
	}

	/**
	 * Check whether session ID is stored in Mina's session.
	 * 
	 * @param session
	 * @return
	 */
	protected boolean serverHasSessionId(IoSession session) {
		return session.containsAttribute(SESSION_ID);
	}

	/**
	 * Check whether session ID is stored in Mina's session.
	 * 
	 * @param session
	 * @return
	 */
	protected void saveSessionIdToServer(IoSession session, String sessionId) {
		session.setAttribute(SESSION_ID, sessionId);
	}

	/**
	 * Check whether session ID is stored in Mina's session.
	 * 
	 * @param session
	 * @return
	 */
	protected String getServerSessionId(IoSession session) {
		return (String) session.getAttribute(SESSION_ID);
	}
	
	protected void markSendSessionIdToClient(IoSession session) {
		log.debug("Mina session ID {}: marking need to return session ID to client", session.getId());
		session.setAttribute(RETURN_SESSION_ID_TO_CLIENT);
	}
	
	protected boolean isMarkSendSessionIdToClient(IoSession session) {
		return session.containsAttribute(RETURN_SESSION_ID_TO_CLIENT);
	}
	
	protected Object removeMarkSendSessionIdToClient(IoSession session) {
		 return session.removeAttribute(RETURN_SESSION_ID_TO_CLIENT);
	}
	
}
