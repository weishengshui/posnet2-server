package com.chinarewards.qqgbvpn.main.protocol.handler.qqadidas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityManager;
import com.chinarewards.qqgbvpn.main.protocol.ServiceHandler;
import com.chinarewards.qqgbvpn.main.protocol.ServiceRequest;
import com.chinarewards.qqgbvpn.main.protocol.ServiceResponse;
import com.chinarewards.qqgbvpn.main.protocol.ServiceSession;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQWeixinSignInRespMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQWeixinSignInReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.filter.LoginFilter;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQWeixinSignInVo;
import com.google.inject.Inject;

public class QQWinxinSignInHandler implements ServiceHandler {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	protected QQAdidasActivityManager qqAdidasActivityManager;

	@Override
	public void execute(ServiceRequest request, ServiceResponse response) {

		QQWeixinSignInReqMsg bodyMessage = (QQWeixinSignInReqMsg) request
				.getParameter();
		ServiceSession session = request.getSession();
		String posId = String.valueOf(session.getAttribute(LoginFilter.POS_ID));

		log.debug("QQWinxinSignInHandler, bodyMessage=:{}", bodyMessage);
		log.debug("QQWinxinSignInHandler, posId=: {}", posId);

		QQWeixinSignInVo weixinSignInVo = qqAdidasActivityManager.weiXinSignIn(
				bodyMessage.getWeixinId(), posId);

		QQWeixinSignInRespMsg weixinRespMsg = new QQWeixinSignInRespMsg(
				weixinSignInVo.getReturnCode());

		response.writeResponse(weixinRespMsg);
	}
}
