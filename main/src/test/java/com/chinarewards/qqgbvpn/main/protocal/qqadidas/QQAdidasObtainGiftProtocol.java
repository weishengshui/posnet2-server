package com.chinarewards.qqgbvpn.main.protocal.qqadidas;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.junit.Test;

import com.chinarewards.qq.adidas.domain.ActivityType;
import com.chinarewards.qq.adidas.domain.GiftStatus;
import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qq.adidas.domain.QQActivityMember;
import com.chinarewards.qqgbvpn.domain.event.DomainEvent;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdidasConstant;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftRespMsg;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.SmallNote;

public class QQAdidasObtainGiftProtocol extends QQAdidasBaseProtocol {

	private boolean checkDatabase = true;

	@Test
	public void testObtainGift() throws Exception {
		Socket socket = new Socket("localhost", port);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		posInitAndLogin(os, is);

		// check obtain gift successful
		checkObtainGift_OK(os, is);

		// check obtain gift failure_invalid key.
		checkObtainGift_InvalidKey(os, is);

		// check obtain gift failure_obtained already.
		checkObtainGift_ObtainedAlready(os, is);

		os.close();
		socket.close();
	}

	private void checkObtainGift_ObtainedAlready(OutputStream os, InputStream is)
			throws Exception {
		// valid memberKey
		String validKey = getValidMember();

		// obtain first time should be ok!
		QQVIPObtainGiftReqMsg reqMsg = new QQVIPObtainGiftReqMsg(validKey);
		QQVIPObtainGiftRespMsg respMsg = (QQVIPObtainGiftRespMsg) execReq(os,
				is, reqMsg);
		if (checkDatabase) {
			// check database
			checkObtainGift_ok_db(validKey);
			assertEquals(
					1,
					countDbJournalNum(validKey,
							DomainEvent.QQ_MEMBER_OBTAIN_GIFT.toString()));
		}

		SmallNote expectedNote = generateGiftSmallNote(validKey);
		assertEquals(QQAdidasConstant.GIFT_OK, respMsg.getResult());
		assertEquals(expectedNote.getTitle(), respMsg.getTitle());
		assertEquals(expectedNote.getContent(), respMsg.getTip());

		// obtain second time should be error!
		respMsg = (QQVIPObtainGiftRespMsg) execReq(os, is, reqMsg);

		assertEquals(QQAdidasConstant.GIFT_FAIL_OBTAINED_ALREADY,
				respMsg.getResult());
		assertEquals(null, respMsg.getTitle());
		assertEquals(null, respMsg.getTip());

		if (checkDatabase) {
			// check database
			checkObtainGift_ok_db(validKey);
			assertEquals(
					2,
					countDbJournalNum(validKey,
							DomainEvent.QQ_MEMBER_OBTAIN_GIFT.toString()));
		}
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

	/**
	 * Obtain gift failure, invalid key.
	 * 
	 * @throws Exception
	 */
	private void checkObtainGift_InvalidKey(OutputStream os, InputStream is)
			throws Exception {
		// valid memberKey
		String invalidKey = getInvalidMember();

		// prepare input byte.
		QQVIPObtainGiftReqMsg reqMsg = new QQVIPObtainGiftReqMsg(invalidKey);
		QQVIPObtainGiftRespMsg respMsg = (QQVIPObtainGiftRespMsg) execReq(os,
				is, reqMsg);

		assertEquals(QQAdidasConstant.GIFT_FAIL_INVALID_MEMBER,
				respMsg.getResult());
		assertEquals(null, respMsg.getTitle());
		assertEquals(null, respMsg.getTip());
	}

	/**
	 * Obtain gift successful!
	 */
	private void checkObtainGift_OK(OutputStream os, InputStream is)
			throws Exception {

		// valid memberKey
		String validKey = getValidMember();

		// prepare input byte.
		QQVIPObtainGiftReqMsg reqMsg = new QQVIPObtainGiftReqMsg(validKey);
		QQVIPObtainGiftRespMsg respMsg = (QQVIPObtainGiftRespMsg) execReq(os,
				is, reqMsg);

		SmallNote expectedNote = generateGiftSmallNote(validKey);
		assertEquals(QQAdidasConstant.GIFT_OK, respMsg.getResult());
		assertEquals(expectedNote.getTitle(), respMsg.getTitle());
		assertEquals(expectedNote.getContent(), respMsg.getTip());

		if (checkDatabase) {
			// check database
			checkObtainGift_ok_db(validKey);
			assertEquals(
					1,
					countDbJournalNum(validKey,
							DomainEvent.QQ_MEMBER_OBTAIN_GIFT.toString()));
		}
	}

}
