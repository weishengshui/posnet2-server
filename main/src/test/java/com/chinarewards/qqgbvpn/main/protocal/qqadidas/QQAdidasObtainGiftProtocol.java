package com.chinarewards.qqgbvpn.main.protocal.qqadidas;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.junit.Test;

import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdidasSmallNoteGenerate;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.impl.QQAdidasConstant;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftRespMsg;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQVIPObtainGiftReqMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQVIPObtainGiftRespMsgCodec;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.SmallNote;
import com.chinarewards.ws.ext.api.qq.adidas.exception.MemberKeyExistedException;
import com.chinarewards.ws.ext.api.qq.adidas.service.QQActivityMemberService;

public class QQAdidasObtainGiftProtocol extends QQAdidasProtocol {

	@Test
	public void testObtainGift() throws Exception {
		Socket socket = new Socket("localhost", port);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();

		// check ok
		checkObtainGift_ok(os, is);

		os.close();
		socket.close();
	}

	/**
	 * Obtain gift successful!
	 */
	private void checkObtainGift_ok(OutputStream os, InputStream is)
			throws Exception {

		String memberKey = "123456789012345";
		// generate member
		generateMember(memberKey);

		// init and login
		byte[] challenge = new byte[8];
		oldPosInit(os, is, challenge);
		oldPosLogin(os, is, challenge);

		// prepare input byte.
		QQVIPObtainGiftReqMsg reqMsg = new QQVIPObtainGiftReqMsg(memberKey);
		byte[] header = getDefaultRequestHeader();
		byte[] body = getRequestBody(reqMsg, getDefaultCharset());
		int len = header.length + body.length;
		byte[] outBuf = new byte[len];
		System.arraycopy(header, 0, outBuf, 0, header.length);
		System.arraycopy(body, 0, outBuf, header.length, body.length);

		// override length
		Tools.putUnsignedInt(outBuf, len, 12);
		// calculate and override checksum
		int checksum = Tools.checkSum(outBuf, outBuf.length);
		log.debug("check sum:{}", Integer.toHexString(checksum));
		Tools.putUnsignedShort(outBuf, checksum, 10);

		log.debug("full size:{}", outBuf.length);
		printToHex(outBuf, outBuf.length);

		log.debug("send message!");
		os.write(outBuf);
		// ----------
		Thread.sleep(runForSeconds * 500);
		byte[] response = new byte[500];
		int respLen = is.read(response);
		log.debug("return byte size:{}", respLen);
		printToHex(response, respLen);

		// check
		int respHeaderLen = getDefaultResponseHeaderLength();
		int respBodyLen = respLen - respHeaderLen;
		byte[] respBody = new byte[respBodyLen];
		System.arraycopy(response, respHeaderLen, respBody, 0, respBodyLen);

		QQVIPObtainGiftRespMsg respMsg = getObtainGiftRespMsg(respBody,
				getDefaultCharset());
		SmallNote expectedNote = generateGiftSmallNote(memberKey);

		assertEquals(QQAdidasConstant.GIFT_OK, respMsg.getResult());
		assertEquals(expectedNote.getTitle(), respMsg.getTitle());
		assertEquals(expectedNote.getContent(), respMsg.getTip());
	}

	private void generateMember(String memberKey) {
		try {
			getInjector().getInstance(QQActivityMemberService.class)
					.generateQQActivityMember(memberKey, new Date());
		} catch (MemberKeyExistedException e) {
			e.printStackTrace();
		}
	}

	private void printToHex(byte[] response, int n) {
		for (int i = 0; i < n; i++) {
			String s = Integer.toHexString((byte) response[i]);
			if (s.length() < 2)
				s = "0" + s;
			if (s.length() > 2)
				s = s.substring(s.length() - 2);
			System.out.print(s + " ");
			if ((i + 1) % 8 == 0)
				System.out.println("");
		}
	}

	private byte[] getRequestBody(QQVIPObtainGiftReqMsg reqMsg, Charset charset) {
		QQVIPObtainGiftReqMsgCodec codec = new QQVIPObtainGiftReqMsgCodec();
		return codec.encode(reqMsg, charset);
	}

	private QQVIPObtainGiftRespMsg getObtainGiftRespMsg(byte[] respBody,
			Charset charset) throws Exception {
		QQVIPObtainGiftRespMsgCodec codec = new QQVIPObtainGiftRespMsgCodec();
		IoBuffer in = IoBuffer.allocate(respBody.length);
		in.put(respBody);
		in.position(0);
		return (QQVIPObtainGiftRespMsg) codec.decode(in, charset);

	}

	private SmallNote generateGiftSmallNote(String memberKey) {
		return getInjector().getInstance(QQAdidasSmallNoteGenerate.class)
				.generateAsObtainGift(memberKey);
	}
}
