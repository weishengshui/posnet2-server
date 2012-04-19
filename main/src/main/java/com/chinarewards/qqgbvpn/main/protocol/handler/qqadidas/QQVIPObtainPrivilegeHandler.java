package com.chinarewards.qqgbvpn.main.protocol.handler.qqadidas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityManager;
import com.chinarewards.qqgbvpn.main.protocol.ServiceHandler;
import com.chinarewards.qqgbvpn.main.protocol.ServiceRequest;
import com.chinarewards.qqgbvpn.main.protocol.ServiceResponse;
import com.chinarewards.qqgbvpn.main.protocol.ServiceSession;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainPrivilegeReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainPrivilegeRespMsg;
import com.chinarewards.qqgbvpn.main.protocol.filter.LoginFilter;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainPrivilegeVo;
import com.google.inject.Inject;

public class QQVIPObtainPrivilegeHandler implements ServiceHandler {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	protected QQAdidasActivityManager qqAdidasActivityManager;

	@Override
	public void execute(ServiceRequest request, ServiceResponse response) {

		QQVIPObtainPrivilegeReqMsg bodyMessage = (QQVIPObtainPrivilegeReqMsg) request
				.getParameter();
		ServiceSession session = request.getSession();
		String posId = String.valueOf(session.getAttribute(LoginFilter.POS_ID));

		log.debug("QQVIPObtainPrivilegeHandler, bodyMessage=:{}", bodyMessage);
		log.debug("QQVIPObtainPrivilegeHandler, posId=: {}", posId);

		double consumeAmt = Double.parseDouble(bodyMessage.getAmount());

		QQMemberObtainPrivilegeVo privilegeVo = qqAdidasActivityManager
				.obtainPrivilege(bodyMessage.getUserCode(), consumeAmt, posId);
		String noteTitle = null;
		String noteContent = null;
		if (privilegeVo.getSmallNote() != null) {
			noteTitle = privilegeVo.getSmallNote().getTitle();
			noteContent = privilegeVo.getSmallNote().getContent();
		}
		QQVIPObtainPrivilegeRespMsg privilegeRespMeg = new QQVIPObtainPrivilegeRespMsg(
				privilegeVo.getReturnCode(), privilegeVo.getOperateTime(),
				noteTitle, noteContent);
		log.debug("QQVIPObtainPrivilegeHandler resp: {} ", privilegeRespMeg);
		response.writeResponse(privilegeRespMeg);
	}
}
