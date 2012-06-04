package com.chinarewards.qqgbvpn.main.mxBean;

import java.util.Map;

import com.chinarewards.qqgbvpn.main.mxBean.vo.KnownClient;

/**
 * 
 * @author yanxin
 * @since 0.3.3
 */
public interface IKnownClientsMXBean {

	public int getKnownClientCount();

	public Map<String, KnownClient> getKnownClients();
}
