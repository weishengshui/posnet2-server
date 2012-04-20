/**
 * 
 */
package com.chinarewards.qqgbvpn.main.codec.qqadidas;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.junit.Test;

import com.chinarewards.qqgbpvn.main.test.GuiceTest;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftRespMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainPrivilegeReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainPrivilegeRespMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQWeixinSignInReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQWeixinSignInRespMsg;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQVIPObtainGiftReqMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQVIPObtainGiftRespMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQVIPObtainPrivilegeReqMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQVIPObtainPrivilegeRespMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQWeixinSignInReqMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQWeixinSignInRespMsgCodec;

/**
 * 
 * @author yanxin
 * @since 0.3.0
 */
public class QQVIPAdidasCodecTest extends GuiceTest {

	private Charset getCharset() {
		return Charset.forName("GB2312");
	}

	@Test
	public void testQQVIPObtainGiftReqMsgCodec() throws Exception {
		QQVIPObtainGiftReqMsgCodec codec = new QQVIPObtainGiftReqMsgCodec();
		// encode
		String memberKey = "12345678901234";
		QQVIPObtainGiftReqMsg reqMsg = new QQVIPObtainGiftReqMsg(memberKey);
		byte[] codeByte = codec.encode(reqMsg, getCharset());

		// decode
		IoBuffer in = IoBuffer.allocate(codeByte.length);
		in.put(codeByte);
		in.position(0);

		QQVIPObtainGiftReqMsg reqMsgResult = (QQVIPObtainGiftReqMsg) codec
				.decode(in, getCharset());
		assertEquals(memberKey, reqMsgResult.getUserCode());
	}

	@Test
	public void testQQVIPObtainGiftRespMsgCodec() throws Exception {
		QQVIPObtainGiftRespMsgCodec codec = new QQVIPObtainGiftRespMsgCodec();

		// encode
		long result = 1;
		Date xactTime = new Date();
		String title = "didas Neo 五月新品";
		String tip = "11111111111234可以领取免费礼品一份.";

		QQVIPObtainGiftRespMsg respMsg = new QQVIPObtainGiftRespMsg(result,
				xactTime, title, tip);
		byte[] codeByte = codec.encode(respMsg, getCharset());

		// decode
		IoBuffer in = IoBuffer.allocate(codeByte.length);
		in.put(codeByte);
		in.position(0);
		QQVIPObtainGiftRespMsg respMsgResult = (QQVIPObtainGiftRespMsg) codec
				.decode(in, getCharset());

		assertEquals(result, respMsgResult.getResult());
		assertEquals(xactTime, respMsgResult.getXactTime());
		assertEquals(title, respMsgResult.getTitle());
		assertEquals(tip, respMsgResult.getTip());
	}

	@Test
	public void testQQVIPObtainPrivilegeReqMsgCodec() throws Exception {
		QQVIPObtainPrivilegeReqMsgCodec codec = new QQVIPObtainPrivilegeReqMsgCodec();
		// encode
		String memberKey = "12345678901234";
		String consumeAmt = "300.0";

		QQVIPObtainPrivilegeReqMsg reqMsg = new QQVIPObtainPrivilegeReqMsg(
				memberKey, consumeAmt);

		byte[] codeByte = codec.encode(reqMsg, getCharset());

		// decode
		IoBuffer in = IoBuffer.allocate(codeByte.length);
		in.put(codeByte);
		in.position(0);

		QQVIPObtainPrivilegeReqMsg reqMsgResult = (QQVIPObtainPrivilegeReqMsg) codec
				.decode(in, getCharset());
		assertEquals(memberKey, reqMsgResult.getUserCode());
		assertEquals(consumeAmt, reqMsgResult.getAmount());
	}

	@Test
	public void testQQVIPObtainPrivilegeRespMsgCodec() throws Exception {
		QQVIPObtainPrivilegeRespMsgCodec codec = new QQVIPObtainPrivilegeRespMsgCodec();

		// encode
		long result = 1;
		Date xactTime = new Date();
		String title = "didas Neo 五月新品";
		String tip = "11111111111234本次消费300元.享受50元折扣优惠,折扣后实际支付金额为250元.还一次消费300-600元,折扣50元的机会.";

		QQVIPObtainPrivilegeRespMsg respMsg = new QQVIPObtainPrivilegeRespMsg(
				result, xactTime, title, tip);
		byte[] codeByte = codec.encode(respMsg, getCharset());

		// decode
		IoBuffer in = IoBuffer.allocate(codeByte.length);
		in.put(codeByte);
		in.position(0);
		QQVIPObtainPrivilegeRespMsg respMsgResult = (QQVIPObtainPrivilegeRespMsg) codec
				.decode(in, getCharset());

		assertEquals(result, respMsgResult.getResult());
		assertEquals(xactTime, respMsgResult.getXactTime());
		assertEquals(title, respMsgResult.getTitle());
		assertEquals(tip, respMsgResult.getTip());
	}

	@Test
	public void testQQWeixinSignInReqMsgCodec() throws Exception {
		QQWeixinSignInReqMsgCodec codec = new QQWeixinSignInReqMsgCodec();

		// encode
		String weixinId = "1234567890";
		QQWeixinSignInReqMsg reqMsg = new QQWeixinSignInReqMsg(weixinId);
		byte[] codeByte = codec.encode(reqMsg, getCharset());

		// decode
		IoBuffer in = IoBuffer.allocate(codeByte.length);
		in.put(codeByte);
		in.position(0);

		QQWeixinSignInReqMsg reqMsgResult = (QQWeixinSignInReqMsg) codec
				.decode(in, getCharset());
		assertEquals(weixinId, reqMsgResult.getWeixinId());
	}

	@Test
	public void testQQWeixinSignInRespMsgCodec() throws Exception {
		QQWeixinSignInRespMsgCodec codec = new QQWeixinSignInRespMsgCodec();

		// encode
		long result = 1;
		QQWeixinSignInRespMsg respMsg = new QQWeixinSignInRespMsg(result);

		byte[] codeByte = codec.encode(respMsg, getCharset());

		// decode
		IoBuffer in = IoBuffer.allocate(codeByte.length);
		in.put(codeByte);
		in.position(0);
		QQWeixinSignInRespMsg respMsgResult = (QQWeixinSignInRespMsg) codec
				.decode(in, getCharset());

		assertEquals(result, respMsgResult.getResult());
	}
}
