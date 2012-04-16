package com.chinarewards.qqgbvpn.mgmtui.logic.qq.adidas.search.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qqgbvpn.mgmtui.dao.qqadidas.QqActivityHistoryDao;
import com.chinarewards.qqgbvpn.mgmtui.logic.qq.adidas.search.QQMemberSearchLogic;
import com.google.inject.Inject;
/**
 * 
 * @author weishengshui
 *
 */
public class QQMemberSearchLogicImple implements QQMemberSearchLogic {

	Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	QqActivityHistoryDao qqActHistoryDao;
	/**
	 * @param memberKey
	 * Get QQ activity history by memberKey
	 */
	@Override
	public List<QQActivityHistory> findQqActivityHistoryByMemberKey(String memberKey) {
		log.debug(
				"Beginning method findQqActivityHistoryByMemberKey, param:{}",
				memberKey);
		List<QQActivityHistory> historys = qqActHistoryDao
				.findQqActivityHistoryByMemberKey(memberKey);
		log.debug("Result size:{}", historys.size());
		
		return historys;
	}
	
	/**
	 *  @param history
	 * Just for testing..
	 * @deprecated
	 */
	public void saveQqActivityHistoryByMemberKey(QQActivityHistory history) {
		qqActHistoryDao.saveQQqActivityHistory(history);
	}

}
