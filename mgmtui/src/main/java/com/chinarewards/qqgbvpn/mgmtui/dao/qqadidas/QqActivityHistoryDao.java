package com.chinarewards.qqgbvpn.mgmtui.dao.qqadidas;

import java.util.List;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;

public interface QqActivityHistoryDao {
	/**
	 * Just for test
	 * @param history
	 */
	public void saveQQqActivityHistory(QQActivityHistory history);
	/**
	 * Get QQ activity history by member key
	 * @param cdkey
	 * @return
	 */
	public List<QQActivityHistory> findQqActivityHistoryByMemberKey(String memberKey);
}
