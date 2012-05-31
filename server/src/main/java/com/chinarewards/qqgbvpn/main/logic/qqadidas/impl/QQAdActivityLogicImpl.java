package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;

import java.util.Date;
import java.util.List;

import com.chinarewards.qq.adidas.domain.ActivityType;
import com.chinarewards.qq.adidas.domain.GiftStatus;
import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qq.adidas.domain.QQActivityMember;
import com.chinarewards.qq.adidas.domain.QQWeixinSignIn;
import com.chinarewards.qqgbvpn.common.DateTimeProvider;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQActivityHistoryDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQActivityMemberDao;
import com.chinarewards.qqgbvpn.main.dao.qqadidas.QQWeixinSignInDao;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.ConsumeAmountNotEnoughException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.GiftObtainedAlreadyException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.InvalidMemberKeyException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.ObtainedPrivilegeAllAlreadyException;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdActivityLogic;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.vo.CalPrivilegeResult;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ObtainPrivilegeResult;
import com.google.inject.Inject;

public class QQAdActivityLogicImpl implements QQAdActivityLogic {

	@Inject
	QQActivityHistoryDao qqActivityHistoryDao;

	@Inject
	QQActivityMemberDao qqActivityMemberDao;

	@Inject
	QQWeixinSignInDao qqWeixinSignInDao;

	@Inject
	DateTimeProvider dateTimeProvider;

	@Override
	public QQActivityHistory obtainFreeGift(String memberKey, String posId)
			throws InvalidMemberKeyException, GiftObtainedAlreadyException {
		// Check status
		QQActivityMember member = qqActivityMemberDao
				.findByMemberKey(memberKey);
		if (member == null) {
			throw new InvalidMemberKeyException();
		}
		if (GiftStatus.DONE == member.getGiftStatus()) {
			throw new GiftObtainedAlreadyException(getLastHistory(memberKey,
					ActivityType.GIFT).getCreatedAt());
		}
		Date now = dateTimeProvider.getTime();

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
			ConsumeAmountNotEnoughException,
			ObtainedPrivilegeAllAlreadyException {
		// check status
		QQActivityMember member = qqActivityMemberDao
				.findByMemberKey(memberKey);
		if (member == null) {
			throw new InvalidMemberKeyException();
		}
		if (PrivilegeStatus.DONE == member.getPrivilegeStatus()) {
			throw new ObtainedPrivilegeAllAlreadyException();
		}

		if (consumeAmt < QQAdConstant.CONSUME_AMOUNT_TO_REBATE_HALF_PRIVILEGE) {
			throw new ConsumeAmountNotEnoughException();
		}

		ObtainPrivilegeResult privilegeResult = new ObtainPrivilegeResult();

		if (PrivilegeStatus.HALF == member.getPrivilegeStatus()) {
			privilegeResult.setExistHistoryLastTime(true);
			privilegeResult.setHistoryLastTime(getLastHistory(memberKey,
					ActivityType.PRIVILEGE));
		}

		CalPrivilegeResult result = calculatePrivilegeResult(
				member.getPrivilegeStatus(), consumeAmt);

		Date now = dateTimeProvider.getTime();
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
			if (consumeAmt >= QQAdConstant.CONSUME_AMOUNT_TO_REBATE_HALF_PRIVILEGE
					&& consumeAmt < QQAdConstant.CONSUME_AMOUNT_TO_REBATE_FULL_PRIVILEGE) {
				rebateAmt = QQAdConstant.REBATE_HALF_AMOUNT;
				nextSt = PrivilegeStatus.HALF;
			} else if (consumeAmt >= QQAdConstant.CONSUME_AMOUNT_TO_REBATE_FULL_PRIVILEGE) {
				rebateAmt = QQAdConstant.REBATE_FULL_AMOUNT;
				nextSt = PrivilegeStatus.DONE;
			}
		} else if (PrivilegeStatus.HALF == status
				&& consumeAmt >= QQAdConstant.CONSUME_AMOUNT_TO_REBATE_HALF_PRIVILEGE) {
			rebateAmt = QQAdConstant.REBATE_FULL_AMOUNT
					- QQAdConstant.REBATE_HALF_AMOUNT;
			nextSt = PrivilegeStatus.DONE;
		}

		return new CalPrivilegeResult(rebateAmt, nextSt);
	}

	@Override
	public QQWeixinSignIn weiXinSignIn(String weixinNo, String posId) {
		Date now = dateTimeProvider.getTime();

		// persist weixin signin
		QQWeixinSignIn signIn = new QQWeixinSignIn();
		signIn.setCreatedAt(now);
		signIn.setLastModifiedAt(now);
		signIn.setWeixinNo(weixinNo);
		signIn.setPosId(posId);
		qqWeixinSignInDao.save(signIn);
		return signIn;
	}

	private QQActivityHistory getLastHistory(String memberKey, ActivityType type) {
		List<QQActivityHistory> histories = qqActivityHistoryDao
				.findHistoriesByMemberKeyAndType(memberKey, type);
		if (histories.isEmpty()) {
			throw new IllegalStateException(
					"Data error! The history should exist in database already as memberKey="
							+ memberKey);
		}

		return histories.get(0);
	}

}
