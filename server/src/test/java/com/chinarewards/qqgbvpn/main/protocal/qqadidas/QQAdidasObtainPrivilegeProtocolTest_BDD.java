package com.chinarewards.qqgbvpn.main.protocal.qqadidas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.junit.Test;

import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdConstant;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainPrivilegeReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainPrivilegeRespMsg;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.PrivilegeReceiptGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.PrivilegeScreenDisplayGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.Receipt;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ScreenDisplay;

public class QQAdidasObtainPrivilegeProtocolTest_BDD extends
		QQAdidasBaseProtocol {

	ObtainPrivilegeBlackBox blackBox;

	@Test
	public void testObtainPrivilege_invalidMemberKey() throws Exception {
		whenIStartANewConnection();
		andPosInitAndLogin();
		andRedeemPrivilege("123456789012345", 350d);
		thenPosReturnCodeShouldBe(QQAdConstant.PRIVILEGE_FAIL_INVALD_MEMBER);
		thenPosScreenDisplayShouldBe("You are not qq vip!");
		thenPosReceiptShouldBe("No receipt need!");
		closeConnection();
	}

	@Test
	public void testObtainPrivilege_consumeAmtNotEnough() throws Exception {
		whenIStartANewConnection();
		andPosInitAndLogin();
		andRegisterQQVIP("123456789012345");
		andRedeemPrivilege("123456789012345", 299d);
		thenPosReturnCodeShouldBe(QQAdConstant.PRIVILEGE_FAIL_CONSUME_NOT_ENOUGH);
		thenPosScreenDisplayShouldBe("Consume amount is not enought to get privilege!");
		thenPosReceiptShouldBe("No receipt need!");
		closeConnection();
	}

	@Test
	public void testObtainPrivilege_firstSucessfulObtain_part()
			throws Exception {
		whenIStartANewConnection();
		andPosInitAndLogin();
		andRegisterQQVIP("123456789012345");
		andRedeemPrivilege("123456789012345", 300d);
		thenPosReturnCodeShouldBe(QQAdConstant.PRIVILEGE_OK);
		thenPosScreenDisplayShouldBe("privilege ok!");
		thenPosReceiptShouldBe("You obtain partly privilege, and have another chance to obtain again!");
		closeConnection();
	}

	@Test
	public void testObtainPrivilege_secondSucessfulObtain() throws Exception {
		whenIStartANewConnection();
		andPosInitAndLogin();
		andRegisterQQVIP("123456789012345");
		andRedeemPrivilege("123456789012345", 300d);
		andRedeemPrivilege("123456789012345", 600d);
		thenPosReturnCodeShouldBe(QQAdConstant.PRIVILEGE_OK);
		thenPosScreenDisplayShouldBe("privilege ok!");
		thenPosReceiptShouldBe("You obtain whole privilege, and you had obtained before!");
		closeConnection();
	}

	@Test
	public void testObtainPrivilege_firstSucessfulObtain_whole()
			throws Exception {
		whenIStartANewConnection();
		andPosInitAndLogin();
		andRegisterQQVIP("123456789012345");
		andRedeemPrivilege("123456789012345", 600d);
		thenPosReturnCodeShouldBe(QQAdConstant.PRIVILEGE_OK);
		thenPosScreenDisplayShouldBe("privilege ok!");
		thenPosReceiptShouldBe("You obtain whole privilege!");
		closeConnection();
	}

	@Test
	public void testObtainPrivilege_obtainedWholeAlready() throws Exception {
		whenIStartANewConnection();
		andPosInitAndLogin();
		andRegisterQQVIP("123456789012345");
		andRedeemPrivilege("123456789012345", 600d);
		andRedeemPrivilege("123456789012345", 300d);
		thenPosReturnCodeShouldBe(QQAdConstant.PRIVILEGE_FAIL_OBTAINED_ALL_ALREADY);
		thenPosScreenDisplayShouldBe("You had obtained all privilege!");
		thenPosReceiptShouldBe("No receipt need!");
		closeConnection();
	}

	private void thenPosReceiptShouldBe(String string) {
		if ("No receipt need!".equals(string)) {
			assertTrue(true);
		} else if ("You obtain partly privilege, and have another chance to obtain again!"
				.equals(string)) {
			// assemble expected receipt!
			PrivilegeReceiptGenModel genModel = new PrivilegeReceiptGenModel();
			genModel.setReturnCode(QQAdConstant.PRIVILEGE_OK);
			genModel.setConsumeAmt(blackBox.getConsumeAmt());
			genModel.setExistLastTimeConsume(false);
			genModel.setMemberKey(blackBox.getMemberKey());
			genModel.setRebateAmt(QQAdConstant.REBATE_HALF_AMOUNT);
			Receipt expectedNote = genPrivilegeReceipt(genModel);
			assertEquals(expectedNote.getTitle(), blackBox.getTitle());
			assertEquals(expectedNote.getContent(), blackBox.getTip());
		} else if ("You obtain whole privilege!".equals(string)) {
			// assemble expected receipt!
			PrivilegeReceiptGenModel genModel = new PrivilegeReceiptGenModel();
			genModel.setReturnCode(QQAdConstant.PRIVILEGE_OK);
			genModel.setConsumeAmt(blackBox.getConsumeAmt());
			genModel.setExistLastTimeConsume(false);
			genModel.setMemberKey(blackBox.getMemberKey());
			genModel.setRebateAmt(QQAdConstant.REBATE_FULL_AMOUNT);
			Receipt expectedNote = genPrivilegeReceipt(genModel);
			assertEquals(expectedNote.getTitle(), blackBox.getTitle());
			assertEquals(expectedNote.getContent(), blackBox.getTip());
		} else if ("You obtain whole privilege, and you had obtained before!"
				.equals(string)) {
			// assemble expected receipt!
			PrivilegeReceiptGenModel genModel = new PrivilegeReceiptGenModel();
			genModel = new PrivilegeReceiptGenModel();
			genModel.setReturnCode(QQAdConstant.PRIVILEGE_OK);
			genModel.setMemberKey(blackBox.getMemberKey());
			genModel.setConsumeAmt(blackBox.getConsumeAmt());
			genModel.setExistLastTimeConsume(true);
			genModel.setLastConsumeDate(blackBox.getLastSuccessfulOpTimeMap()
					.get(blackBox.getMemberKey()));
			genModel.setLastRebateAmt(QQAdConstant.REBATE_HALF_AMOUNT);
			genModel.setRebateAmt(QQAdConstant.REBATE_HALF_AMOUNT);
			Receipt expectedNote = genPrivilegeReceipt(genModel);
			assertEquals(expectedNote.getTitle(), blackBox.getTitle());
			assertEquals(expectedNote.getContent(), blackBox.getTip());
		}
	}

	private void thenPosScreenDisplayShouldBe(String string) {
		// check screen display
		if ("Consume amount is not enought to get privilege!".equals(string)) {
			PrivilegeScreenDisplayGenModel genModel = new PrivilegeScreenDisplayGenModel(
					QQAdConstant.PRIVILEGE_FAIL_CONSUME_NOT_ENOUGH,
					blackBox.getMemberKey(), blackBox.getConsumeAmt());
			ScreenDisplay expectedDisplay = genPrivilegeScreenDisplay(genModel);
			assertEquals(expectedDisplay.getContent(), blackBox.getTip());
		} else if ("You are not qq vip!".equals(string)) {
			PrivilegeScreenDisplayGenModel genModel = new PrivilegeScreenDisplayGenModel(
					QQAdConstant.PRIVILEGE_FAIL_INVALD_MEMBER,
					blackBox.getMemberKey(), blackBox.getConsumeAmt());
			ScreenDisplay expectedDisplay = genPrivilegeScreenDisplay(genModel);
			assertEquals(expectedDisplay.getContent(), blackBox.getTip());
		} else if ("You had obtained all privilege!".equals(string)) {
			PrivilegeScreenDisplayGenModel genModel = new PrivilegeScreenDisplayGenModel(
					QQAdConstant.PRIVILEGE_FAIL_OBTAINED_ALL_ALREADY,
					blackBox.getMemberKey(), blackBox.getConsumeAmt());
			ScreenDisplay expectedDisplay = genPrivilegeScreenDisplay(genModel);
			assertEquals(expectedDisplay.getContent(), blackBox.getTip());
		}
	}

	private void thenPosReturnCodeShouldBe(int st) {
		assertEquals(st, blackBox.getResult());
	}

	private void andRedeemPrivilege(String memberKey, double consumeAmount)
			throws Exception {
		QQVIPObtainPrivilegeReqMsg reqMsg = new QQVIPObtainPrivilegeReqMsg(
				memberKey, String.valueOf(consumeAmount));

		// First request!
		QQVIPObtainPrivilegeRespMsg respMsg = (QQVIPObtainPrivilegeRespMsg) execReq(
				blackBox.getOs(), blackBox.getIs(), reqMsg);
		blackBox.setMemberKey(memberKey);
		blackBox.setConsumeAmt(consumeAmount);
		blackBox.setResult(respMsg.getResult());
		if (respMsg.getResult() == QQAdConstant.PRIVILEGE_OK
				&& !blackBox.getLastSuccessfulOpTimeMap()
						.containsKey(memberKey)) {
			blackBox.getLastSuccessfulOpTimeMap().put(memberKey,
					respMsg.getXactTime());
		}
		blackBox.setTitle(respMsg.getTitle());
		blackBox.setTip(respMsg.getTip());
	}

	private void whenIStartANewConnection() throws Exception {
		Socket socket = new Socket("localhost", port);
		ObtainPrivilegeBlackBox blackBox = new ObtainPrivilegeBlackBox();
		blackBox.setSocket(socket);
		blackBox.setIs(socket.getInputStream());
		blackBox.setOs(socket.getOutputStream());
		this.blackBox = blackBox;
	}

	private void andPosInitAndLogin() throws Exception {
		// init and login
		OutputStream os = blackBox.getOs();
		InputStream is = blackBox.getIs();
		byte[] challenge = new byte[8];
		oldPosInit(os, is, challenge);
		oldPosLogin(os, is, challenge);
	}

	private void andRegisterQQVIP(String memberKey) {
		generateMember(memberKey);
	}

	private void closeConnection() throws Exception {
		blackBox.getOs().close();
		blackBox.getSocket().close();
	}
}
