package com.chinarewards.qqgbvpn.main.protocal.qqadidas;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.chinarewards.qq.adidas.domain.ActivityType;
import com.chinarewards.qq.adidas.domain.GiftStatus;
import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityHistory;
import com.chinarewards.qq.adidas.domain.QQActivityMember;
import com.chinarewards.qqgbvpn.domain.event.DomainEvent;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdidasConstant;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainPrivilegeReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainPrivilegeRespMsg;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ObtainPrvilegePrintModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.SmallNote;

public class QQAdidasObtainPrivilegeProtocol extends QQAdidasBaseProtocol {

	private boolean checkDatabase = true;

	@Test
	public void testObtainPrivilege() throws Exception {
		Socket socket = new Socket("localhost", port);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		posInitAndLogin(os, is);

		// invalid member
		checkObtainPrivilege_InvalidMember(os, is, getInvalidMember(), 299);
		checkObtainPrivilege_InvalidMember(os, is, getInvalidMember(), 300);
		checkObtainPrivilege_InvalidMember(os, is, getInvalidMember(), 599);
		checkObtainPrivilege_InvalidMember(os, is, getInvalidMember(), 600);

		// valid member
		checkObtainPrivilege_1_x_0(os, is, getValidMember(), 0);
		checkObtainPrivilege_1_x_0(os, is, getValidMember(), 299);

		checkObtainPrivilege_1_x_50_2_y_50_3_z_0(os, is, getValidMember(), 300,
				300, 600);
		checkObtainPrivilege_1_x_50_2_y_50_3_z_0(os, is, getValidMember(), 599,
				599, 300);
		checkObtainPrivilege_1_x_50_2_y_50_3_z_0(os, is, getValidMember(), 599,
				600, 1000);

		checkObtainPrivilege_1_x_100_2_y_0(os, is, getValidMember(), 600, 299);
		checkObtainPrivilege_1_x_100_2_y_0(os, is, getValidMember(), 600, 300);
		checkObtainPrivilege_1_x_100_2_y_0(os, is, getValidMember(), 600, 599);
		checkObtainPrivilege_1_x_100_2_y_0(os, is, getValidMember(), 600, 600);
		checkObtainPrivilege_1_x_100_2_y_0(os, is, getValidMember(), 1000, 1000);
	}

	private void checkObtainPrivilege_InvalidMember(OutputStream os,
			InputStream is, String who, double consumeAmt) throws Exception {
		String inValidKey = who;

		QQVIPObtainPrivilegeReqMsg reqMsg = new QQVIPObtainPrivilegeReqMsg(
				inValidKey, String.valueOf(consumeAmt));

		// request!
		QQVIPObtainPrivilegeRespMsg respMsg = (QQVIPObtainPrivilegeRespMsg) execReq(
				os, is, reqMsg);

		assertEquals(QQAdidasConstant.PRIVILEGE_FAIL_INVALD_MEMBER,
				respMsg.getResult());
		assertEquals(null, respMsg.getTitle());
		assertEquals(null, respMsg.getTip());

	}

	@SuppressWarnings("unchecked")
	private void checkObtainPrivilege_first_second_ok_db(String memberKey,
			double firstConsumeAmt, double secondConsumeAmt) {
		getEm().clear();

		// check QQActivityHistory
		List<QQActivityHistory> histories = getEm()
				.createQuery(
						"FROM QQActivityHistory h WHERE h.memberKey=:memberKey ORDER BY h.createdAt")
				.setParameter("memberKey", memberKey).getResultList();
		assertEquals(2, histories.size());
		QQActivityHistory history = histories.get(0);
		assertEquals(memberKey, history.getMemberKey());
		assertEquals(firstConsumeAmt, history.getConsumeAmt(), 0);
		assertEquals(ActivityType.PRIVILEGE, history.getAType());
		assertEquals(QQAdidasConstant.REBATE_HALF_AMOUNT,
				history.getRebateAmt(), 0);

		QQActivityHistory history2 = histories.get(1);
		assertEquals(memberKey, history2.getMemberKey());
		assertEquals(secondConsumeAmt, history2.getConsumeAmt(), 0);
		assertEquals(ActivityType.PRIVILEGE, history2.getAType());
		assertEquals(QQAdidasConstant.REBATE_HALF_AMOUNT,
				history2.getRebateAmt(), 0);

		// check QQActivityMember
		List<QQActivityMember> members = getEm()
				.createQuery(
						"FROM QQActivityMember m WHERE m.memberKey = :memberKey")
				.setParameter("memberKey", memberKey).getResultList();
		assertEquals(1, members.size());
		QQActivityMember member = members.get(0);
		assertEquals(GiftStatus.NEW, member.getGiftStatus());
		assertEquals(PrivilegeStatus.DONE, member.getPrivilegeStatus());

	}

	@SuppressWarnings("unchecked")
	private void checkObtainPrivilege_first_ok_db(String memberKey,
			double consumeAmt) {
		getEm().clear();

		double rebateAmt = 0d;
		PrivilegeStatus st = null;
		if (consumeAmt < 600) {
			rebateAmt = QQAdidasConstant.REBATE_HALF_AMOUNT;
			st = PrivilegeStatus.HALF;
		} else {
			rebateAmt = QQAdidasConstant.REBATE_FULL_AMOUNT;
			st = PrivilegeStatus.DONE;
		}

		// check QQActivityHistory
		List<QQActivityHistory> histories = getEm()
				.createQuery(
						"FROM QQActivityHistory h WHERE h.memberKey = :memberKey")
				.setParameter("memberKey", memberKey).getResultList();
		assertEquals(1, histories.size());
		QQActivityHistory history = histories.get(0);
		assertEquals(memberKey, history.getMemberKey());
		assertEquals(consumeAmt, history.getConsumeAmt(), 0);
		assertEquals(ActivityType.PRIVILEGE, history.getAType());
		assertEquals(rebateAmt, history.getRebateAmt(), 0);

		// check QQActivityMember
		List<QQActivityMember> members = getEm()
				.createQuery(
						"FROM QQActivityMember m WHERE m.memberKey = :memberKey")
				.setParameter("memberKey", memberKey).getResultList();
		assertEquals(1, members.size());
		QQActivityMember member = members.get(0);
		assertEquals(GiftStatus.NEW, member.getGiftStatus());
		assertEquals(st, member.getPrivilegeStatus());
	}

	/**
	 * 1_x_0<br/>
	 * 第{1}次消费x{0-299}, 返还{0}
	 * 
	 * @param os
	 * @param is
	 * @param who
	 * @param x
	 * @throws Exception
	 */
	private void checkObtainPrivilege_1_x_0(OutputStream os, InputStream is,
			String who, double x) throws Exception {
		// valid memberKey
		String validKey = who;
		double firstConsumeAmt = x;

		QQVIPObtainPrivilegeReqMsg reqMsg = new QQVIPObtainPrivilegeReqMsg(
				validKey, String.valueOf(firstConsumeAmt));

		// First request!
		QQVIPObtainPrivilegeRespMsg respMsg = (QQVIPObtainPrivilegeRespMsg) execReq(
				os, is, reqMsg);

		assertEquals(QQAdidasConstant.PRIVILEGE_FAIL_CONSUME_NOT_ENOUGH,
				respMsg.getResult());
		assertEquals(null, respMsg.getTitle());
		assertEquals(null, respMsg.getTip());
	}

	/**
	 * 1_x_100_2_y_0<br/>
	 * 第{1}次消费，消费x{600+},返还{100}<br/>
	 * 第{2}次消费，消费y{*},返还{0}
	 * 
	 * @param os
	 * @param is
	 * @param who
	 * @param x
	 * @param y
	 * @throws Exception
	 */
	private void checkObtainPrivilege_1_x_100_2_y_0(OutputStream os,
			InputStream is, String who, double x, double y) throws Exception {
		// valid memberKey
		String validKey = who;
		double firstConsumeAmt = x;
		double secondConsumeAmt = y;

		QQVIPObtainPrivilegeReqMsg reqMsg = new QQVIPObtainPrivilegeReqMsg(
				validKey, String.valueOf(firstConsumeAmt));

		// First request!
		QQVIPObtainPrivilegeRespMsg respMsg = (QQVIPObtainPrivilegeRespMsg) execReq(
				os, is, reqMsg);

		assertEquals(QQAdidasConstant.PRIVILEGE_OK, respMsg.getResult());
		// assemble expected small note!
		ObtainPrvilegePrintModel printModel = new ObtainPrvilegePrintModel();
		printModel.setConsumeAmt(firstConsumeAmt);
		printModel.setExistLastTimeConsume(false);
		printModel.setMemberKey(validKey);
		printModel.setRebateAmt(QQAdidasConstant.REBATE_FULL_AMOUNT);
		SmallNote expectedNote = generatePrivilegeSmallNote(printModel);
		assertEquals(expectedNote.getTitle(), respMsg.getTitle());
		assertEquals(expectedNote.getContent(), respMsg.getTip());
		if (checkDatabase) {
			checkObtainPrivilege_first_ok_db(validKey, firstConsumeAmt);
			assertEquals(
					1,
					countDbJournalNum(validKey,
							DomainEvent.QQ_MEMBER_OBTAIN_PRIVILEGE.toString()));
		}

		// Second request!
		reqMsg = new QQVIPObtainPrivilegeReqMsg(validKey,
				String.valueOf(secondConsumeAmt));
		respMsg = (QQVIPObtainPrivilegeRespMsg) execReq(os, is, reqMsg);

		assertEquals(QQAdidasConstant.PRIVILEGE_FAIL_OBTAINED_ALL_ALREADY,
				respMsg.getResult());
		assertEquals(null, respMsg.getTitle());
		assertEquals(null, respMsg.getTip());

		if (checkDatabase) {
			checkObtainPrivilege_first_ok_db(validKey, firstConsumeAmt);
			assertEquals(
					2,
					countDbJournalNum(validKey,
							DomainEvent.QQ_MEMBER_OBTAIN_PRIVILEGE.toString()));
		}
	}

	/**
	 * 1_x_50_2_y_50_3_z_0<br/>
	 * 第{1}次消费，消费x{300-599},返还{50}<br/>
	 * 第{2}次消费，消费y{300+},返还{50}<br/>
	 * 第{3}次消费，消费z{*},返还{0}<br/>
	 * 
	 * @param os
	 * @param is
	 * @param who
	 * @param x
	 * @param y
	 * @param z
	 * @throws Exception
	 */
	private void checkObtainPrivilege_1_x_50_2_y_50_3_z_0(OutputStream os,
			InputStream is, String who, double x, double y, double z)
			throws Exception {
		// valid memberKey
		String validKey = who;
		double firstConsumeAmt = x;
		double secondConsumeAmt = y;
		double thirdConsumeAmt = z;

		QQVIPObtainPrivilegeReqMsg reqMsg = new QQVIPObtainPrivilegeReqMsg(
				validKey, String.valueOf(firstConsumeAmt));

		// First request!
		QQVIPObtainPrivilegeRespMsg respMsg = (QQVIPObtainPrivilegeRespMsg) execReq(
				os, is, reqMsg);

		assertEquals(QQAdidasConstant.PRIVILEGE_OK, respMsg.getResult());
		// assemble expected small note!
		ObtainPrvilegePrintModel printModel = new ObtainPrvilegePrintModel();
		printModel.setConsumeAmt(firstConsumeAmt);
		printModel.setExistLastTimeConsume(false);
		printModel.setMemberKey(validKey);
		printModel.setRebateAmt(QQAdidasConstant.REBATE_HALF_AMOUNT);
		SmallNote expectedNote = generatePrivilegeSmallNote(printModel);
		assertEquals(expectedNote.getTitle(), respMsg.getTitle());
		assertEquals(expectedNote.getContent(), respMsg.getTip());
		Date obtainTime = respMsg.getXactTime();

		if (checkDatabase) {
			checkObtainPrivilege_first_ok_db(validKey, firstConsumeAmt);
			assertEquals(
					1,
					countDbJournalNum(validKey,
							DomainEvent.QQ_MEMBER_OBTAIN_PRIVILEGE.toString()));
		}

		// Second request!
		reqMsg = new QQVIPObtainPrivilegeReqMsg(validKey,
				String.valueOf(secondConsumeAmt));
		respMsg = (QQVIPObtainPrivilegeRespMsg) execReq(os, is, reqMsg);

		assertEquals(QQAdidasConstant.PRIVILEGE_OK, respMsg.getResult());
		// assemble expected small note!
		printModel = new ObtainPrvilegePrintModel();
		printModel.setMemberKey(validKey);
		printModel.setConsumeAmt(secondConsumeAmt);
		printModel.setExistLastTimeConsume(true);
		printModel.setLastConsumeDate(obtainTime);
		printModel.setLastRebateAmt(QQAdidasConstant.REBATE_HALF_AMOUNT);
		printModel.setRebateAmt(QQAdidasConstant.REBATE_HALF_AMOUNT);
		expectedNote = generatePrivilegeSmallNote(printModel);
		assertEquals(expectedNote.getTitle(), respMsg.getTitle());
		assertEquals(expectedNote.getContent(), respMsg.getTip());

		if (checkDatabase) {
			checkObtainPrivilege_first_second_ok_db(validKey, firstConsumeAmt,
					secondConsumeAmt);
			assertEquals(
					2,
					countDbJournalNum(validKey,
							DomainEvent.QQ_MEMBER_OBTAIN_PRIVILEGE.toString()));
		}

		// Third request!
		reqMsg = new QQVIPObtainPrivilegeReqMsg(validKey,
				String.valueOf(thirdConsumeAmt));
		respMsg = (QQVIPObtainPrivilegeRespMsg) execReq(os, is, reqMsg);

		assertEquals(QQAdidasConstant.PRIVILEGE_FAIL_OBTAINED_ALL_ALREADY,
				respMsg.getResult());
		assertEquals(null, respMsg.getTitle());
		assertEquals(null, respMsg.getTip());

		if (checkDatabase) {
			checkObtainPrivilege_first_second_ok_db(validKey, firstConsumeAmt,
					secondConsumeAmt);
			assertEquals(
					3,
					countDbJournalNum(validKey,
							DomainEvent.QQ_MEMBER_OBTAIN_PRIVILEGE.toString()));
		}

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
