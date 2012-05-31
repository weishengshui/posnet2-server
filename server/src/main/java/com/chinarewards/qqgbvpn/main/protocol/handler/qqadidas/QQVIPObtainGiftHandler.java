package com.chinarewards.qqgbvpn.main.protocol.handler.qqadidas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdActivityManager;
import com.chinarewards.qqgbvpn.main.protocol.ServiceHandler;
import com.chinarewards.qqgbvpn.main.protocol.ServiceRequest;
import com.chinarewards.qqgbvpn.main.protocol.ServiceResponse;
import com.chinarewards.qqgbvpn.main.protocol.ServiceSession;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftRespMsg;
import com.chinarewards.qqgbvpn.main.protocol.filter.LoginFilter;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainGiftVo;
import com.google.inject.Inject;

public class QQVIPObtainGiftHandler implements ServiceHandler {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	protected QQAdActivityManager qqAdidasActivityManager;

	@Override
	public void execute(ServiceRequest request, ServiceResponse response) {

		QQVIPObtainGiftReqMsg bodyMessage = (QQVIPObtainGiftReqMsg) request
				.getParameter();
		ServiceSession session = request.getSession();
		String posId = String.valueOf(session.getAttribute(LoginFilter.POS_ID));

		log.debug("QQVIPObtainGiftHandler, bodyMessage=:{}", bodyMessage);
		log.debug("QQVIPObtainGiftHandler, posId=: {}", posId);

		QQMemberObtainGiftVo giftVo = qqAdidasActivityManager.obtainFreeGift(
				bodyMessage.getUserCode(), posId);
		String title = null;
		String tip = null;
		if (giftVo.getDisplay() != null) {
			tip = giftVo.getDisplay().getContent();
		}
		if (giftVo.getReceipt() != null) {
			title = giftVo.getReceipt().getTitle();
			tip = giftVo.getReceipt().getContent();
		}
		QQVIPObtainGiftRespMsg giftRespMsg = new QQVIPObtainGiftRespMsg(
				giftVo.getReturnCode(), giftVo.getOperateTime(), title, tip);
		log.debug("QQVIPObtainGiftHandler resp: {} ", giftRespMsg);
		response.writeResponse(giftRespMsg);
	}
}
