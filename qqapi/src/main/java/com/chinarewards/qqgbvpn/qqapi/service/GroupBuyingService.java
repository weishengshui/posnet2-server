package com.chinarewards.qqgbvpn.qqapi.service;

import java.util.HashMap;

public interface GroupBuyingService {
	
	/**
	 * 团购查询
	 * 
	 * @author iori
	 * @param params
	 * @return
	 */
	public HashMap<String, Object> groupBuyingSearch(
			HashMap<String, String> params) throws Exception;

	/**
	 * 团购验证
	 * 
	 * @author iori
	 * @param params
	 * @return
	 */
	public HashMap<String, Object> groupBuyingValidate(
			HashMap<String, String> params) throws Exception;

	/**
	 * 团购取消绑定
	 * 
	 * @author iori
	 * @param params
	 * @return
	 */
	public HashMap<String, Object> groupBuyingUnbind(
			HashMap<String, Object> params) throws Exception;
}
