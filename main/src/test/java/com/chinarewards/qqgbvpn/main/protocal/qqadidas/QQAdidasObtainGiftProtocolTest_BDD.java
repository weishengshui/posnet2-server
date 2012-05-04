package com.chinarewards.qqgbvpn.main.protocal.qqadidas;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qq.adidas.domain.ActivityType;
import com.chinarewards.qq.adidas.domain.GiftStatus;
import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qq.adidas.domain.QQActivityMember;
import com.chinarewards.qqgbvpn.domain.event.DomainEvent;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdConstant;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftRespMsg;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.GiftScreenDisplayGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ScreenDisplay;

/**
 * Try to use BDD method to write test case.
 * 
 * @author yanxin
 * 
 */
public class QQAdidasObtainGiftProtocolTest_BDD extends QQAdidasBaseProtocol {

	Logger log = LoggerFactory.getLogger(getClass());

	ObtainGiftBlackBox blackBox;
	boolean shouldCheckDb = true;

	@Test
	public void testGift_redeemOk() throws Exception {
		whenIStartANewConnection();
		andPosInitAndLogin();
		andRegisterQQVIP("123456789012345");
		andRedeemGift("123456789012345");
		thenPosReturnCodeShouldBe(QQAdConstant.GIFT_OK);
		thenPosScreenDisplayShouldBe("You will get a gift!");
		thenPosReceiptShouldBe("No receipt need!");
		if (shouldCheckDb) {
			thenPosServerDbShouldBe("Table Journal : one redeem record, Table QQActivityHistory : one record, and Table QQActivityMember gift status DONE, privilege status NEW");
		}
		closeConnection();
	}

	@Test
	public void testGift_redeemedAlready() throws Exception {
		whenIStartANewConnection();
		andPosInitAndLogin();
		andRegisterQQVIP("123456789012345");
		andRedeemGift("123456789012345");
		andRedeemGift("123456789012345");
		thenPosReturnCodeShouldBe(QQAdConstant.GIFT_FAIL_OBTAINED_ALREADY);
		thenPosScreenDisplayShouldBe("You had redeemed gift already!");
		thenPosReceiptShouldBe("No receipt need!");
		if (shouldCheckDb) {
			thenPosServerDbShouldBe("Table Journal : two redeem record, Table QQActivityHistory : one record, and Table QQActivityMember gift status DONE, privilege status NEW");
		}
		closeConnection();
	}

	@Test
	public void testGift_invalidMemberKey() throws Exception {
		whenIStartANewConnection();
		andPosInitAndLogin();
		andRedeemGift("123456789012345");
		thenPosReturnCodeShouldBe(QQAdConstant.GIFT_FAIL_INVALID_MEMBER);
		thenPosScreenDisplayShouldBe("You are not qq vip!");
		thenPosReceiptShouldBe("No receipt need!");
		if (shouldCheckDb) {
			thenPosServerDbShouldBe("Table Journal : one redeem record, Table QQActivityHistory : zero record");
		}
		closeConnection();
	}

	private void closeConnection() throws Exception {
		blackBox.getOs().close();
		blackBox.getSocket().close();
	}

	private void thenPosServerDbShouldBe(String string) {
		if ("Table Journal : one redeem record, Table QQActivityHistory : one record, and Table QQActivityMember gift status DONE, privilege status NEW"
				.equals(string)) {
			// check database
			checkObtainGift_ok_db(blackBox.getMemberKey());
			assertEquals(
					1,
					countDbJournalNum(blackBox.getMemberKey(),
							DomainEvent.QQ_MEMBER_OBTAIN_GIFT.toString()));
		} else if ("Table Journal : two redeem record, Table QQActivityHistory : one record, and Table QQActivityMember gift status DONE, privilege status NEW"
				.equals(string)) {
			checkObtainGift_ok_db(blackBox.getMemberKey());
			assertEquals(
					2,
					countDbJournalNum(blackBox.getMemberKey(),
							DomainEvent.QQ_MEMBER_OBTAIN_GIFT.toString()));
		} else if ("Table Journal : one redeem record, Table QQActivityHistory : zero record"
				.equals(string)) {
			checkObtainGift_fail_db(blackBox.getMemberKey());
			assertEquals(
					1,
					countDbJournalNum(blackBox.getMemberKey(),
							DomainEvent.QQ_MEMBER_OBTAIN_GIFT.toString()));
		}
	}

	@SuppressWarnings("unchecked")
	private void checkObtainGift_fail_db(String memberKey) {
		getEm().clear();

		// check QQActivityHistory
		List<QQActivityHistory> histories = getEm()
				.createQuery(
						"FROM QQActivityHistory h WHERE h.memberKey =:memberKey")
				.setParameter("memberKey", memberKey).getResultList();
		assertEquals(0, histories.size());
	}

	private void thenPosReturnCodeShouldBe(int st) {
		assertEquals(st, blackBox.getResult());
	}

	private void thenPosReceiptShouldBe(String string) {
		if ("No receipt need!".equals(string)) {
			assertEquals(null, blackBox.getReceipt());
		}
	}

	private void thenPosScreenDisplayShouldBe(String string) {
		// check screen display
		if ("You can get a gift!".equals(string)) {
			ScreenDisplay expectedDisplay = genGiftScreenDisplay(new GiftScreenDisplayGenModel(
					QQAdConstant.GIFT_OK, blackBox.getMemberKey(), null));
			assertEquals(expectedDisplay.getContent(), blackBox.getDisplay()
					.getContent());
		} else if ("You had redeemed gift already!".equals(string)) {
			ScreenDisplay expectedDisplay = genGiftScreenDisplay(new GiftScreenDisplayGenModel(
					QQAdConstant.GIFT_FAIL_OBTAINED_ALREADY,
					blackBox.getMemberKey(),
					blackBox.getLastSuccessfulOperationTime()));
			assertEquals(expectedDisplay.getContent(), blackBox.getDisplay()
					.getContent());

		} else if ("You are not qq vip!".equals(string)) {
			ScreenDisplay expectedDisplay = genGiftScreenDisplay(new GiftScreenDisplayGenModel(
					QQAdConstant.GIFT_FAIL_INVALID_MEMBER,
					blackBox.getMemberKey(), null));
			assertEquals(expectedDisplay.getContent(), blackBox.getDisplay()
					.getContent());

		}
	}

	private void andRegisterQQVIP(String memberKey) {
		generateMember(memberKey);
	}

	private void andRedeemGift(String memberKey) throws Exception {
		OutputStream os = blackBox.getOs();
		InputStream is = blackBox.getIs();
		// prepare input byte.
		QQVIPObtainGiftReqMsg reqMsg = new QQVIPObtainGiftReqMsg(memberKey);
		QQVIPObtainGiftRespMsg respMsg = (QQVIPObtainGiftRespMsg) execReq(os,
				is, reqMsg);

		blackBox.setMemberKey(memberKey);
		blackBox.setResult(respMsg.getResult());
		blackBox.setDisplay(new ScreenDisplay(respMsg.getTip()));
		blackBox.setReceipt(null);
		if (respMsg.getXactTime() != null) {
			blackBox.setLastSuccessfulOperationTime(respMsg.getXactTime());
			log.debug("response content: {}",
					blackBox.getLastSuccessfulOperationTime());
		}
	}

	private void andPosInitAndLogin() throws Exception {
		// init and login
		OutputStream os = blackBox.getOs();
		InputStream is = blackBox.getIs();
		byte[] challenge = new byte[8];
		oldPosInit(os, is, challenge);
		oldPosLogin(os, is, challenge);
	}

	private void whenIStartANewConnection() throws Exception {
		Socket socket = new Socket("localhost", port);
		ObtainGiftBlackBox blackBox = new ObtainGiftBlackBox();
		blackBox.setSocket(socket);
		blackBox.setIs(socket.getInputStream());
		blackBox.setOs(socket.getOutputStream());
		this.blackBox = blackBox;
	}

	@SuppressWarnings("unchecked")
	private void checkObtainGift_ok_db(String memberKey) {
		getEm().clear();

		// check QQActivityHistory
		List<QQActivityHistory> histories = getEm()
				.createQuery(
						"FROM QQActivityHistory h WHERE h.memberKey =:memberKey")
				.setParameter("memberKey", memberKey).getResultList();
		assertEquals(1, histories.size());
		QQActivityHistory history = histories.get(0);
		assertEquals(ActivityType.GIFT, history.getAType());

		// check QQActivityMember
		List<QQActivityMember> members = getEm()
				.createQuery(
						"FROM QQActivityMember m WHERE m.memberKey =:memberKey")
				.setParameter("memberKey", memberKey).getResultList();
		assertEquals(1, members.size());
		QQActivityMember member = members.get(0);
		assertEquals(PrivilegeStatus.NEW, member.getPrivilegeStatus());
		assertEquals(GiftStatus.DONE, member.getGiftStatus());
	}

	private int countDbJournalNum(String memberKey, String event) {
		// check journal
		return Integer
				.parseInt(getEm()
						.createQuery(
								"SELECT COUNT(j.id) FROM Journal j WHERE j.eventDetail like :memberKey AND j.event = :event")
						.setParameter("memberKey", "%" + memberKey + "%")
						.setParameter("event", event).getSingleResult()
						.toString());

	}

}
