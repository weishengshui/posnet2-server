package com.chinarewards.huaxia.main.protocol.handler;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.huaxia.main.logic.coupon.exchange.HuaXiaCouponExchangeManager;
import com.chinarewards.huaxia.main.protocol.cmd.HuaXiaCouponExchangeRequestMessage;
import com.chinarewards.huaxia.main.protocol.cmd.HuaXiaCouponExchangeResponseMessage;
import com.chinarewards.qqgbvpn.main.protocol.ServiceHandler;
import com.chinarewards.qqgbvpn.main.protocol.ServiceRequest;
import com.chinarewards.qqgbvpn.main.protocol.ServiceResponse;
import com.chinarewards.qqgbvpn.main.protocol.ServiceSession;
import com.chinarewards.qqgbvpn.main.protocol.filter.LoginFilter;
import com.google.inject.Inject;
/**
 * 
 * @author weishengshui
 *
 */
public class HuaXiaCouponExchangeCommandHandler implements ServiceHandler {

	Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	HuaXiaCouponExchangeManager couponExchangeManager;

	@Override
	public void execute(ServiceRequest request, ServiceResponse response) {

		HuaXiaCouponExchangeRequestMessage bodyMessage = (HuaXiaCouponExchangeRequestMessage) request;

		log.debug("HuaXiaCouponExchangeCommandHandler======execute==bodyMessage=:"
				+ bodyMessage);

		HashMap<String, Object> params = new HashMap<String, Object>();
		//pos id
		String posId = String.valueOf(request.getSession().getAttribute(LoginFilter.POS_ID));
		//cdkey 验证码
		String cdkey = bodyMessage.getCdkey();
		//cardNO6华夏信用卡后6位
		String cardNo6 = bodyMessage.getCardNo();
		//XXX secu_key是这个？
		byte[] secu_key = (byte[])request.getSession().getAttribute(ServiceSession.CHALLENGE_SESSION_KEY);
		
		params.put("posId", posId);
		params.put("cdkey", cdkey);
		params.put("cardNo6", cardNo6);
		params.put("key", secu_key);
		
		HuaXiaCouponExchangeResponseMessage responseMessage = couponExchangeManager.getResponseMessage(params);
		
		response.writeResponse(responseMessage);
		
	}

}
