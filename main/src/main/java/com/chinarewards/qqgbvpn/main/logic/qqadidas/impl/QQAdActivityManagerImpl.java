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
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdActivityLogic;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdActivityManager;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdReceiptGen;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdRespScreenDisplayGen;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.GiftReceiptGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.GiftScreenDisplayGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ObtainPrivilegeResult;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.PrivilegeReceiptGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.PrivilegeScreenDisplayGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainGiftVo;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainPrivilegeVo;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQWeixinSignInVo;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class QQAdActivityManagerImpl implements QQAdActivityManager {

	Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	QQAdActivityLogic qqAdActivityLogic;

	@Inject
	QQAdReceiptGen receiptGen;

	@Inject
	QQAdRespScreenDisplayGen displayGen;

	@Inject
	JournalLogic journalLogic;

	@Transactional
	@Override
	public QQMemberObtainGiftVo obtainFreeGift(String memberKey, String posId) {
		int returnCode = QQAdConstant.OTHERS;
		String entityId = "";
		Date lastObtainedTime = null;
		Date operateTime = null;
		// obtain gift.
		try {
			QQActivityHistory history = qqAdActivityLogic.obtainFreeGift(
					memberKey, posId);
			entityId = history.getId();
			operateTime = history.getCreatedAt();
			returnCode = QQAdConstant.GIFT_OK;
		} catch (InvalidMemberKeyException e) {
			returnCode = QQAdConstant.GIFT_FAIL_INVALID_MEMBER;
		} catch (GiftObtainedAlreadyException e) {
			returnCode = QQAdConstant.GIFT_FAIL_OBTAINED_ALREADY;
			lastObtainedTime = e.getLastObtainedTime();
		} catch (Exception e) {
			returnCode = QQAdConstant.OTHERS;
		}

		ObjectMapper mapper = new ObjectMapper();
		QQMemberObtainGiftVo giftVo = new QQMemberObtainGiftVo();
		giftVo.setMemberKey(memberKey);
		giftVo.setPosId(posId);
		giftVo.setReturnCode(returnCode);
		giftVo.setOperateTime(operateTime);

		// generate pos screen display
		GiftScreenDisplayGenModel gsGenModel = new GiftScreenDisplayGenModel(
				returnCode, memberKey, lastObtainedTime);
		giftVo.setDisplay(displayGen.genGiftRespScreenDisplay(gsGenModel));
		// generate receipt
		giftVo.setReceipt(receiptGen
				.generateGiftReceipt(new GiftReceiptGenModel(returnCode,
						memberKey)));

		// save journal
		try {
			String eventDetail = mapper.writeValueAsString(giftVo);
			journalLogic.logEvent(DomainEvent.QQ_MEMBER_OBTAIN_GIFT,
					DomainEntity.QQ_ACTIVITY_HISTORY, entityId, eventDetail);
		} catch (Exception e) {
			log.error(
					"Exception appear when save journal as obtain qq-adidas gift",
					e);
			giftVo.setReturnCode(QQAdConstant.OTHERS);
		}

		return giftVo;
	}

	@Transactional
	@Override
	public QQMemberObtainPrivilegeVo obtainPrivilege(String memberKey,
			double consumeAmt, String posId) {
		int returnCode = QQAdConstant.OTHERS;
		String entityId = "";
		Date operateTime = null;

		PrivilegeReceiptGenModel prGenModel = new PrivilegeReceiptGenModel();

		// obtain privilege
		try {
			ObtainPrivilegeResult result = qqAdActivityLogic.obtainPrivilege(
					memberKey, consumeAmt, posId);
			returnCode = QQAdConstant.PRIVILEGE_OK;
			entityId = result.getHistoryThisTime().getId();
			operateTime = result.getHistoryThisTime().getCreatedAt();
			// fill with print model used to print small note.
			{
				prGenModel.setConsumeAmt(consumeAmt);
				prGenModel.setMemberKey(memberKey);
				prGenModel.setRebateAmt(result.getHistoryThisTime()
						.getRebateAmt());
				prGenModel.setExistLastTimeConsume(result
						.isExistHistoryLastTime());
				if (result.isExistHistoryLastTime()) {
					prGenModel.setLastConsumeDate(result.getHistoryLastTime()
							.getCreatedAt());
					prGenModel.setLastRebateAmt(result.getHistoryLastTime()
							.getRebateAmt());
				}
			}
		} catch (InvalidMemberKeyException e) {
			returnCode = QQAdConstant.PRIVILEGE_FAIL_INVALD_MEMBER;
		} catch (ConsumeAmountNotEnoughException e) {
			returnCode = QQAdConstant.PRIVILEGE_FAIL_CONSUME_NOT_ENOUGH;
		} catch (ObtainedPrivilegeAllAlreadyException e) {
			returnCode = QQAdConstant.PRIVILEGE_FAIL_OBTAINED_ALL_ALREADY;
		} catch (Exception e) {
			returnCode = QQAdConstant.OTHERS;
		}

		// return value.
		ObjectMapper mapper = new ObjectMapper();
		QQMemberObtainPrivilegeVo privilegeVo = new QQMemberObtainPrivilegeVo();
		privilegeVo.setConsumeAmt(consumeAmt);
		privilegeVo.setMemberKey(memberKey);
		privilegeVo.setPosId(posId);
		privilegeVo.setReturnCode(returnCode);
		privilegeVo.setOperateTime(operateTime);

		// generate pos screen display
		PrivilegeScreenDisplayGenModel psGenModel = new PrivilegeScreenDisplayGenModel(
				returnCode, memberKey, consumeAmt);
		privilegeVo.setDisplay(displayGen
				.genPrivilegeRespScreenDisplay(psGenModel));
		// generate receipt.
		prGenModel.setReturnCode(returnCode);
		privilegeVo.setReceipt(receiptGen.generatePrivilegeReceipt(prGenModel));

		// save journal
		try {
			String eventDetail = mapper.writeValueAsString(privilegeVo);
			journalLogic.logEvent(DomainEvent.QQ_MEMBER_OBTAIN_PRIVILEGE,
					DomainEntity.QQ_ACTIVITY_HISTORY, entityId, eventDetail);
		} catch (Exception e) {
			log.error(
					"Exception appear when save journal as obtain qq-adidas privilege",
					e);
			privilegeVo.setReturnCode(QQAdConstant.OTHERS);
		}

		return privilegeVo;
	}

	@Transactional
	@Override
	public QQWeixinSignInVo weiXinSignIn(String weixinNo, String posId) {
		qqAdActivityLogic.weiXinSignIn(weixinNo, posId);
		QQWeixinSignInVo weixinVo = new QQWeixinSignInVo();
		weixinVo.setReturnCode(QQAdConstant.WEIXIN_SUCCESS);
		return weixinVo;
	}

}
