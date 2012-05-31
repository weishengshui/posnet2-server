package com.chinarewards.qqgbvpn.main.protocal.qqadidas;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.junit.Test;

import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdConstant;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQWeixinSignInReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQWeixinSignInRespMsg;

public class QQWeixinSignInProtocol extends QQAdidasBaseProtocol {

	@Test
	public void testWeinxinSignIn() throws Exception {
		Socket socket = new Socket("localhost", port);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		posInitAndLogin(os, is);

		// ok
		checkSignIn_OK(os, is, "123");
		checkSignIn_OK(os, is, "123");
		checkSignIn_OK(os, is, "123456");
		checkSignIn_OK(os, is, "1233456789012345");

		os.close();
		socket.close();
	}

	public void checkSignIn_OK(OutputStream os, InputStream is, String weixinNo)
			throws Exception {
		// obtain first time should be ok!
		QQWeixinSignInReqMsg reqMsg = new QQWeixinSignInReqMsg(weixinNo);
		QQWeixinSignInRespMsg respMsg = (QQWeixinSignInRespMsg) execReq(os, is,
				reqMsg);
		assertEquals(QQAdConstant.WEIXIN_SUCCESS, respMsg.getResult());
	}
}
