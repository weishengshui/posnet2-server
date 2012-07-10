package com.chinarewards.huaxia.main.logic.coupon.exchange;

import java.util.HashMap;

import com.chinarewards.huaxia.main.protocol.cmd.HuaXiaCouponExchangeResponseMessage;
/**
 * 
 * @author weishengshui
 *
 */
public interface HuaXiaCouponExchangeManager {
	
	public HuaXiaCouponExchangeResponseMessage getResponseMessage(HashMap<String, Object> params);

}
