package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;

import java.util.Date;
import java.util.List;

import com.chinarewards.qq.adidas.domain.ActivityType;
import com.chinarewards.qq.adidas.domain.GiftStatus;
import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qq.adidas.domain.QQActivityMember;
import com.chinarewards.qq.adidas.domain.QQWeixinSignIn;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQActivityHistoryDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQActivityMemberDao;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.ConsumeAmountNotEnoughException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateObtainGiftException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateWeixinNoException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.InvalidMemberKeyException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.PrivilegeDoneException;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityLogic;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.vo.CalPrivilegeResult;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ObtainPrivilegeResult;
import com.google.inject.Inject;

public class QQAdidasActivityLogicImpl implements QQAdidasActivityLogic {

	@Inject
	QQActivityHistoryDao qqActivityHistoryDao;

	@Inject
	QQActivityMemberDao qqActivityMemberDao;

	@Override
	public QQActivityHistory obtainFreeGift(String memberKey, String posId)
			throws InvalidMemberKeyException, DuplicateObtainGiftException {
		// Check status
		QQActivityMember member = qqActivityMemberDao
				.findByMemberKey(memberKey);
		if (member == null) {
			throw new InvalidMemberKeyException();
		}
		if (GiftStatus.DONE == member.getGiftStatus()) {
			throw new DuplicateObtainGiftException();
		}
		Date now = new Date();

		// persist history
		QQActivityHistory history = new QQActivityHistory();
		history.setCreatedAt(now);
		history.setLastModifiedAt(now);
		history.setMemberKey(memberKey);
		history.setPosId(posId);
		history.setAType(ActivityType.GIFT);
		qqActivityHistoryDao.save(history);

		// Change status
		member.setGiftStatus(GiftStatus.DONE);
		member.setLastModifiedAt(now);
		qqActivityMemberDao.update(member);

		return history;
	}

	@Override
	public ObtainPrivilegeResult obtainPrivilege(String memberKey,
			double consumeAmt, String posId) throws InvalidMemberKeyException,
			ConsumeAmountNotEnoughException, PrivilegeDoneException {
		// check status
		QQActivityMember member = qqActivityMemberDao
				.findByMemberKey(memberKey);
		if (member == null) {
			throw new InvalidMemberKeyException();
		}
		if (PrivilegeStatus.DONE == member.getPrivilegeStatus()) {
			throw new PrivilegeDoneException();
		}

		if (consumeAmt < QQAdidasConstant.CONSUME_AMOUNT_TO_REBATE_HALF_PRIVILEGE) {
			throw new ConsumeAmountNotEnoughException();
		}

		ObtainPrivilegeResult privilegeResult = new ObtainPrivilegeResult();

		if (PrivilegeStatus.HALF == member.getPrivilegeStatus()) {
			List<QQActivityHistory> histories = qqActivityHistoryDao
					.findHistoriesByMemberKey(memberKey);
			if (histories.isEmpty()) {
				throw new IllegalStateException(
						"Data error! The history should exist in database already as memberKey="
								+ memberKey);
			}
			privilegeResult.setExistHistoryLastTime(true);
			privilegeResult.setHistoryLastTime(histories.get(0));
		}

		CalPrivilegeResult result = calculatePrivilegeResult(
				member.getPrivilegeStatus(), consumeAmt);

		Date now = new Date();
		QQActivityHistory history = new QQActivityHistory();
		history.setAType(ActivityType.PRIVILEGE);
		history.setCreatedAt(now);
		history.setLastModifiedAt(now);
		history.setMemberKey(memberKey);
		history.setPosId(posId);
		history.setConsumeAmt(consumeAmt);
		history.setRebateAmt(result.getRebateAmt());
		qqActivityHistoryDao.save(history);
		privilegeResult.setHistoryThisTime(history);

		// Change status
		member.setPrivilegeStatus(result.getNextPrivilegeStatus());
		member.setLastModifiedAt(now);
		qqActivityMemberDao.update(member);

		return privilegeResult;
	}

	/**
	 * NEW, 300 -> 50<br/>
	 * NEW, 600 -> 100<br/>
	 * HALF, 300/600 -> 50<br/>
	 * 
	 * @param status
	 * @param consumeAmt
	 * @return
	 */
	private CalPrivilegeResult calculatePrivilegeResult(PrivilegeStatus status,
			double consumeAmt) {
		PrivilegeStatus nextSt = status;
		double rebateAmt = 0d;

		if (PrivilegeStatus.NEW == status) {
			if (consumeAmt >= QQAdidasConstant.CONSUME_AMOUNT_TO_REBATE_HALF_PRIVILEGE
					&& consumeAmt < QQAdidasConstant.CONSUME_AMOUNT_TO_REBATE_FULL_PRIVILEGE) {
				rebateAmt = QQAdidasConstant.REBATE_HALF_AMOUNT;
				nextSt = PrivilegeStatus.HALF;
			} else if (consumeAmt >= QQAdidasConstant.CONSUME_AMOUNT_TO_REBATE_FULL_PRIVILEGE) {
				rebateAmt = QQAdidasConstant.REBATE_FULL_AMOUNT;
				nextSt = PrivilegeStatus.DONE;
			}
		} else if (PrivilegeStatus.HALF == status
				&& consumeAmt >= QQAdidasConstant.CONSUME_AMOUNT_TO_REBATE_HALF_PRIVILEGE) {
			rebateAmt = QQAdidasConstant.REBATE_FULL_AMOUNT
					- QQAdidasConstant.REBATE_HALF_AMOUNT;
			nextSt = PrivilegeStatus.DONE;
		}

		return new CalPrivilegeResult(rebateAmt, nextSt);
	}

	@Override
	public QQWeixinSignIn weiXinSignIn(String weixinNo, String posId)
			throws DuplicateWeixinNoException {
		// TODO Auto-generated method stub
		return null;
	}

}
