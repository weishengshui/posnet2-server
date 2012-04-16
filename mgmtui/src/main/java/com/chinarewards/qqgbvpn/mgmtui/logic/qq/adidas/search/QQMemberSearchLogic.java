package com.chinarewards.qqgbvpn.mgmtui.logic.qq.adidas.search;

import java.util.List;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;

public interface QQMemberSearchLogic {
	/**
	 * @param memberKey
	 * @return
	 * @author weishengshui
	 * 	Get QQ activity history by member key
	 */
	public List<QQActivityHistory> findQqActivityHistoryByMemberKey(String memberKey);

	/**
	 * Just use for testing.. 
	 * @param history
	 * @deprecated
	 */
	public void saveQqActivityHistoryByMemberKey(QQActivityHistory history);
}
