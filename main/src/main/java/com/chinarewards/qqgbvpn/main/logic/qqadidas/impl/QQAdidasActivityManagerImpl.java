package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;

import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qq.adidas.domain.ActivityType;
import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qq.adidas.domain.QQActivityMember;
import com.chinarewards.qqgbvpn.domain.event.DomainEntity;
import com.chinarewards.qqgbvpn.domain.event.DomainEvent;
import com.chinarewards.qqgbvpn.logic.journal.JournalLogic;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.ConsumeAmountNotEnoughException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.DuplicateObtainGiftException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.InvalidMemberKeyException;
import com.chinarewards.qqgbvpn.main.exception.qqadidas.PrivilegeDoneException;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityLogic;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasActivityManager;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasSmallNoteGenerate;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.vo.CalPrivilegeResult;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainGiftVo;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.QQMemberObtainPrivilegeVo;
import com.google.inject.Inject;

public class QQAdidasActivityManagerImpl implements QQAdidasActivityManager {

	Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	QQAdidasActivityLogic qqAdidasActivityLogic;

	@Inject
	QQAdidasSmallNoteGenerate qqAdidasSmallNoteGenerate;

	@Inject
	JournalLogic journalLogic;

	@Override
	public QQMemberObtainGiftVo obtainFreeGift(String memberKey, String posId) {
		int returnCode = -1;
		String entityId = "";
		// obtain gift.
		try {
			QQActivityHistory history = qqAdidasActivityLogic.obtainFreeGift(
					memberKey, posId);
			entityId = history.getId();
			returnCode = QQMemberObtainGiftVo.QQ_MEMBER_OBTAIN_GIFT_SUCCESS;
		} catch (InvalidMemberKeyException e) {
			returnCode = QQMemberObtainGiftVo.QQ_MEMBER_OBTAIN_GIFT_INVALID_MEMBER;
		} catch (DuplicateObtainGiftException e) {
			returnCode = QQMemberObtainGiftVo.QQ_MEMBER_OBTAIN_GIFT_ALREADY;
		} catch (Exception e) {
			returnCode = -1;
		}

		ObjectMapper mapper = new ObjectMapper();
		QQMemberObtainGiftVo giftVo = new QQMemberObtainGiftVo();
		giftVo.setMemberKey(memberKey);
		giftVo.setPosId(posId);
		giftVo.setReturnCode(returnCode);
		// success
		if (returnCode == QQMemberObtainGiftVo.QQ_MEMBER_OBTAIN_GIFT_SUCCESS) {
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
			giftVo.setReturnCode(-1);
		}

		return giftVo;
	}

	@Override
	public QQMemberObtainPrivilegeVo obtainPrivilege(String memberKey,
			double consumeAmt, String posId) {
		int returnCode = -1;
		String entityId = "";
		// obtain privilege
		// qqAdidasActivityLogic.obtainPrivilege(memberKey, consumeAmt, posId);
		return null;
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
				rebateAmt = 50;
				nextSt = PrivilegeStatus.HALF;
			} else if (consumeAmt >= QQAdidasConstant.CONSUME_AMOUNT_TO_REBATE_FULL_PRIVILEGE) {
				rebateAmt = 100;
				nextSt = PrivilegeStatus.DONE;
			}
		} else if (PrivilegeStatus.HALF == status
				&& consumeAmt >= QQAdidasConstant.CONSUME_AMOUNT_TO_REBATE_HALF_PRIVILEGE) {
			rebateAmt = 50;
			nextSt = PrivilegeStatus.DONE;
		}

		return new CalPrivilegeResult(rebateAmt, nextSt);
	}

	@Override
	public int weiXinSignIn(String weixinNo, String posId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
