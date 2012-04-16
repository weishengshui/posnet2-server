package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;

import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qqgbvpn.domain.event.DomainEntity;
import com.chinarewards.qqgbvpn.domain.event.DomainEvent;
import com.chinarewards.qqgbvpn.logic.journal.JournalLogic;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.ConsumeAmountNotEnoughException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.GiftObtainedAlreadyException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.InvalidMemberKeyException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.ObtainedPrivilegeAllAlreadyException;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityLogic;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityManager;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasSmallNoteGenerate;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ObtainPrivilegeResult;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ObtainPrvilegePrintModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainGiftVo;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainPrivilegeVo;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQWeixinSignInVo;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class QQAdidasActivityManagerImpl implements QQAdidasActivityManager {

	Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	QQAdidasActivityLogic qqAdidasActivityLogic;

	@Inject
	QQAdidasSmallNoteGenerate qqAdidasSmallNoteGenerate;

	@Inject
	JournalLogic journalLogic;

	@Transactional
	@Override
	public QQMemberObtainGiftVo obtainFreeGift(String memberKey, String posId) {
		int returnCode = QQAdidasConstant.OTHERS;
		String entityId = "";
		Date operateTime = null;
		// obtain gift.
		try {
			QQActivityHistory history = qqAdidasActivityLogic.obtainFreeGift(
					memberKey, posId);
			entityId = history.getId();
			operateTime = history.getCreatedAt();
			returnCode = QQAdidasConstant.GIFT_OK;
		} catch (InvalidMemberKeyException e) {
			returnCode = QQAdidasConstant.GIFT_FAIL_INVALID_MEMBER;
		} catch (GiftObtainedAlreadyException e) {
			returnCode = QQAdidasConstant.GIFT_FAIL_OBTAINED_ALREADY;
		} catch (Exception e) {
			returnCode = QQAdidasConstant.OTHERS;
		}

		ObjectMapper mapper = new ObjectMapper();
		QQMemberObtainGiftVo giftVo = new QQMemberObtainGiftVo();
		giftVo.setMemberKey(memberKey);
		giftVo.setPosId(posId);
		giftVo.setReturnCode(returnCode);
		giftVo.setOperateTime(operateTime);
		// success
		if (returnCode == QQAdidasConstant.GIFT_OK) {
			// print small note
			giftVo.setSmallNote(qqAdidasSmallNoteGenerate
					.generateAsObtainGift(memberKey));
		}

		// save journal
		try {
			String eventDetail = mapper.writeValueAsString(giftVo);
			journalLogic.logEvent(DomainEvent.QQ_MEMBER_OBTAIN_GIFT,
					DomainEntity.QQ_ACTIVITY_HISTORY, entityId, eventDetail);
		} catch (Exception e) {
			log.error(
					"Exception appear when save journal as obtain qq-adidas gift",
					e);
			giftVo.setReturnCode(QQAdidasConstant.OTHERS);
		}

		return giftVo;
	}

	@Transactional
	@Override
	public QQMemberObtainPrivilegeVo obtainPrivilege(String memberKey,
			double consumeAmt, String posId) {
		int returnCode = QQAdidasConstant.OTHERS;
		String entityId = "";
		Date operateTime = null;
		ObtainPrvilegePrintModel printModel = new ObtainPrvilegePrintModel();
		// obtain privilege
		try {
			ObtainPrivilegeResult result = qqAdidasActivityLogic
					.obtainPrivilege(memberKey, consumeAmt, posId);
			returnCode = QQAdidasConstant.PRIVILEGE_OK;
			entityId = result.getHistoryThisTime().getId();
			operateTime = result.getHistoryThisTime().getCreatedAt();
			// fill with print model used to print small note.
			{
				printModel.setConsumeAmt(consumeAmt);
				printModel.setMemberKey(memberKey);
				printModel.setRebateAmt(result.getHistoryThisTime()
						.getRebateAmt());
				printModel.setExistLastTimeConsume(result
						.isExistHistoryLastTime());
				if (result.isExistHistoryLastTime()) {
					printModel.setLastConsumeDate(result.getHistoryLastTime()
							.getCreatedAt());
					printModel.setLastRebateAmt(result.getHistoryLastTime()
							.getRebateAmt());
				}
			}
		} catch (InvalidMemberKeyException e) {
			returnCode = QQAdidasConstant.PRIVILEGE_FAIL_INVALD_MEMBER;
		} catch (ConsumeAmountNotEnoughException e) {
			returnCode = QQAdidasConstant.PRIVILEGE_FAIL_CONSUME_NOT_ENOUGH;
		} catch (ObtainedPrivilegeAllAlreadyException e) {
			returnCode = QQAdidasConstant.PRIVILEGE_FAIL_OBTAINED_ALL_ALREADY;
		} catch (Exception e) {
			returnCode = QQAdidasConstant.OTHERS;
		}

		// return value.
		ObjectMapper mapper = new ObjectMapper();
		QQMemberObtainPrivilegeVo privilegeVo = new QQMemberObtainPrivilegeVo();
		privilegeVo.setConsumeAmt(consumeAmt);
		privilegeVo.setMemberKey(memberKey);
		privilegeVo.setPosId(posId);
		privilegeVo.setReturnCode(returnCode);
		privilegeVo.setOperateTime(operateTime);
		// print small note.
		if (QQAdidasConstant.PRIVILEGE_OK == returnCode) {
			privilegeVo.setSmallNote(qqAdidasSmallNoteGenerate
					.generateAsObtainPrivilege(printModel));
		}

		// save journal
		try {
			String eventDetail = mapper.writeValueAsString(privilegeVo);
			journalLogic.logEvent(DomainEvent.QQ_MEMBER_OBTAIN_PRIVILEGE,
					DomainEntity.QQ_ACTIVITY_HISTORY, entityId, eventDetail);
		} catch (Exception e) {
			log.error(
					"Exception appear when save journal as obtain qq-adidas privilege",
					e);
			privilegeVo.setReturnCode(QQAdidasConstant.OTHERS);
		}

		return privilegeVo;
	}

	@Transactional
	@Override
	public QQWeixinSignInVo weiXinSignIn(String weixinNo, String posId) {
		qqAdidasActivityLogic.weiXinSignIn(weixinNo, posId);
		QQWeixinSignInVo weixinVo = new QQWeixinSignInVo();
		weixinVo.setReturnCode(QQAdidasConstant.WEIXIN_SUCCESS);
		return weixinVo;
	}

}
