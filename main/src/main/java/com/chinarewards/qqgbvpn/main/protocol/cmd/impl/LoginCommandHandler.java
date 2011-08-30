package com.chinarewards.qqgbvpn.main.protocol.cmd.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.codehaus.jackson.JsonGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.config.PosNetworkProperties;
import com.chinarewards.qqgbvpn.main.exception.CopyPropertiesException;
import com.chinarewards.qqgbvpn.main.exception.SaveDBException;
import com.chinarewards.qqgbvpn.main.logic.login.LoginManager;
import com.chinarewards.qqgbvpn.main.logic.qqapi.GroupBuyingManager;
import com.chinarewards.qqgbvpn.main.protocol.cmd.CmdConstant;
import com.chinarewards.qqgbvpn.main.protocol.cmd.CommandHandler;
import com.chinarewards.qqgbvpn.main.protocol.cmd.login.LoginResult;
import com.chinarewards.qqgbvpn.main.protocol.socket.message.IBodyMessage;
import com.chinarewards.qqgbvpn.main.protocol.socket.message.LoginRequestMessage;
import com.chinarewards.qqgbvpn.main.protocol.socket.message.LoginResponseMessage;
import com.chinarewards.qqgbvpn.qqapi.exception.MD5Exception;
import com.chinarewards.qqgbvpn.qqapi.exception.ParseXMLException;
import com.chinarewards.qqgbvpn.qqapi.exception.SendPostTimeOutException;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LoginCommandHandler implements CommandHandler {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	public LoginManager loginManager;

	@Inject
	public Provider<GroupBuyingManager> gbm;
	
	
	
	@Override
	public IBodyMessage execute(IoSession session, IBodyMessage bodyMessage) {
		log.debug("LoginCommandHandler======execute==bodyMessage=:"+bodyMessage);
		LoginResponseMessage loginResponseMessage  = loginManager.login((LoginRequestMessage) bodyMessage);
		loginResponseMessage.setCmdId(CmdConstant.LOGIN_CMD_ID_RESPONSE);
		if(loginResponseMessage.getResult() == LoginResult.SUCCESS.getPosCode()){
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("posId", ((LoginRequestMessage) bodyMessage).getPosid());
			String serverKey = new PosNetworkProperties().getTxServerKey();
			log.debug("LoginCommandHandler======execute==serverKey=:"+serverKey);
			params.put("key", serverKey);
			try {
				gbm.get().initGrouponCache(params);
			}catch (Throwable e) {
				log.error("initGrouponCache fail:"+e);
			}
		}
		return loginResponseMessage;
	}

}
