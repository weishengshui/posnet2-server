package com.chinarewards.qqgbvpn.main.mxBean.impl;

import java.util.Map;

import com.chinarewards.qqgbvpn.main.mxBean.IKnownClientsMXBean;
import com.chinarewards.qqgbvpn.main.mxBean.vo.IPosnetConnectAttr;
import com.chinarewards.qqgbvpn.main.mxBean.vo.KnownClient;
import com.google.inject.Inject;

public class KnownClientsMXBean implements IKnownClientsMXBean {

	IPosnetConnectAttr posnetConnectAttr;

	@Inject
	public KnownClientsMXBean(IPosnetConnectAttr posnetConnectAttr) {
		this.posnetConnectAttr = posnetConnectAttr;
	}

	@Override
	public int getKnownClientCount() {
		return posnetConnectAttr.countKnownClients();
	}

	@Override
	public Map<String, KnownClient> getKnownClients() {
		return posnetConnectAttr.getKnownClients();
	}

}
