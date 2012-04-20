package com.chinarewards.qqgbvpn.main.protocal.qqadidas;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Stack;

import org.apache.mina.core.buffer.IoBuffer;

import com.chinarewards.qqgbpvn.main.CommonTestConfigModule;
import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.ServerModule;
import com.chinarewards.qqgbvpn.main.guice.AppModule;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdReceiptGen;
import com.chinarewards.qqgbvpn.main.logic.qqadidas.QQAdRespScreenDisplayGen;
import com.chinarewards.qqgbvpn.main.module.qqadidas.QQAdidasApiModule;
import com.chinarewards.qqgbvpn.main.protocol.PosnetBaseProtocol;
import com.chinarewards.qqgbvpn.main.protocol.ServiceHandlerModule;
import com.chinarewards.qqgbvpn.main.protocol.ServiceMapping;
import com.chinarewards.qqgbvpn.main.protocol.ServiceMappingConfigBuilder;
import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainPrivilegeReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQWeixinSignInReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.guice.ServiceHandlerGuiceModule;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.ICommandCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQVIPObtainGiftReqMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQVIPObtainGiftRespMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQVIPObtainPrivilegeReqMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQVIPObtainPrivilegeRespMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQWeixinSignInReqMsgCodec;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas.QQWeixinSignInRespMsgCodec;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.GiftReceiptGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.GiftScreenDisplayGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.PrivilegeReceiptGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.PrivilegeScreenDisplayGenModel;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.Receipt;
import com.chinarewards.qqgbvpn.main.qqadidas.vo.ScreenDisplay;
import com.chinarewards.ws.ext.api.qq.adidas.exception.MemberKeyExistedException;
import com.chinarewards.ws.ext.api.qq.adidas.service.QQActivityMemberService;
import com.google.inject.Module;
import com.google.inject.util.Modules;

public abstract class QQAdidasBaseProtocol extends PosnetBaseProtocol {

	private Stack<String> members = new Stack<String>();

	protected void initMembers() {
		for (int i = 99; i > 10; i--) {
			String key = "1234567890000" + i;
			generateMember(key);
			members.push(key);
		}
	}

	protected String getValidMember() {
		if (members.isEmpty())
			return null;
		return members.pop();
	}

	protected String getInvalidMember() {
		return "777777777777777";
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		initMembers();
	}

	@Override
	protected Module[] getModules() {

		CommonTestConfigModule confModule = new CommonTestConfigModule();
		ServiceMappingConfigBuilder mappingBuilder = new ServiceMappingConfigBuilder();
		ServiceMapping mapping = mappingBuilder.buildMapping(confModule
				.getConfiguration());

		// build the Guice modules.
		Module[] modules = new Module[] {
				// new ApplicationModule(),
				new CommonTestConfigModule(),
				buildPersistModule(confModule.getConfiguration()),
				new ServerModule(),
				new AppModule(),
				new QQAdidasApiModule(),
				Modules.override(
						new ServiceHandlerModule(confModule.getConfiguration()))
						.with(new ServiceHandlerGuiceModule(mapping)) };

		return modules;
	}

	private void generateMember(String memberKey) {
		try {
			getInjector().getInstance(QQActivityMemberService.class)
					.generateQQActivityMember(memberKey, new Date());
		} catch (MemberKeyExistedException e) {
			e.printStackTrace();
		}
	}

	private byte[] reqObj2ReqByte(ICommand reqMsg) {
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

		return outBuf;
	}

	private byte[] getRequestBody(ICommand reqMsg, Charset charset) {
		ICommandCodec codec = null;
		if (reqMsg instanceof QQVIPObtainGiftReqMsg) {
			codec = new QQVIPObtainGiftReqMsgCodec();
		} else if (reqMsg instanceof QQVIPObtainPrivilegeReqMsg) {
			codec = new QQVIPObtainPrivilegeReqMsgCodec();
		} else if (reqMsg instanceof QQWeixinSignInReqMsg) {
			codec = new QQWeixinSignInReqMsgCodec();
		}
		return codec.encode(reqMsg, charset);
	}

	protected void posInitAndLogin(OutputStream os, InputStream is)
			throws Exception {
		// init and login
		byte[] challenge = new byte[8];
		oldPosInit(os, is, challenge);
		oldPosLogin(os, is, challenge);
	}

	protected Receipt genGiftReceipt(GiftReceiptGenModel genModel) {
		return getInjector().getInstance(QQAdReceiptGen.class)
				.generateGiftReceipt(genModel);
	}

	protected Receipt genPrivilegeReceipt(PrivilegeReceiptGenModel genModel) {
		return getInjector().getInstance(QQAdReceiptGen.class)
				.generatePrivilegeReceipt(genModel);
	}

	protected ScreenDisplay genGiftScreenDisplay(
			GiftScreenDisplayGenModel genModel) {
		return getInjector().getInstance(QQAdRespScreenDisplayGen.class)
				.genGiftRespScreenDisplay(genModel);
	}

	protected ScreenDisplay genPrivilegeScreenDisplay(
			PrivilegeScreenDisplayGenModel genModel) {
		return getInjector().getInstance(QQAdRespScreenDisplayGen.class)
				.genPrivilegeRespScreenDisplay(genModel);
	}

	/**
	 * This method can convert request object to byte array and send the byte
	 * array. Receive response byte array and convert to response object.
	 * 
	 * @param req
	 * @return
	 */
	protected ICommand execReq(OutputStream os, InputStream is, ICommand req)
			throws Exception {
		byte[] reqByte = reqObj2ReqByte(req);
		log.debug("send message, size={}!", reqByte.length);

		byte[] respBody = sendMessage(os, is, reqByte);

		ICommandCodec respCodec = null;
		if (req instanceof QQVIPObtainGiftReqMsg) {
			respCodec = new QQVIPObtainGiftRespMsgCodec();
		} else if (req instanceof QQVIPObtainPrivilegeReqMsg) {
			respCodec = new QQVIPObtainPrivilegeRespMsgCodec();
		} else if (req instanceof QQWeixinSignInReqMsg) {
			respCodec = new QQWeixinSignInRespMsgCodec();
		}

		IoBuffer in = IoBuffer.allocate(respBody.length);
		in.put(respBody);
		in.position(0);
		return respCodec.decode(in, getDefaultCharset());
	}

	protected byte[] getDefaultRequestHeader() {
		byte[] header = new byte[] {
				// SEQ
				0, 0, 0, 24,
				// ACK
				0, 0, 0, 0,
				// flags
				0, 0,
				// checksum
				0, 0,
				// message length
				0, 0, 0, 32 };

		return header;
	}
}
