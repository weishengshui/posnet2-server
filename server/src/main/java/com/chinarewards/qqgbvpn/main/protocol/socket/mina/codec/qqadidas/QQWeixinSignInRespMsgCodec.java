package com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.exception.PackageException;
import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQWeixinSignInRespMsg;
import com.chinarewards.qqgbvpn.main.protocol.socket.ProtocolLengths;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.ICommandCodec;

public class QQWeixinSignInRespMsgCodec implements ICommandCodec {

	private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * description：mock pos test use it!
	 * 
	 * @param bodyMessage
	 * @param charset
	 * @return
	 */
	@Override
	public ICommand decode(IoBuffer in, Charset charset)
			throws PackageException {
		log.debug("QQWeixinSignInRespMsgCodec decode begin!");

		// 指令ID
		in.getUnsignedInt();
		// result
		long result = in.getUnsignedInt();

		QQWeixinSignInRespMsg msg = new QQWeixinSignInRespMsg(result);

		log.debug("QQWeixinSignInRespMsgCodec decode end, msg:{}", msg);
		return msg;
	}

	@Override
	public byte[] encode(ICommand bodyMessage, Charset charset) {
		log.debug("QQWeixinSignInRespMsgCodec encode begin!");
		QQWeixinSignInRespMsg responseMessage = (QQWeixinSignInRespMsg) bodyMessage;

		long cmdId = responseMessage.getCmdId();
		long result = responseMessage.getResult();

		// calculate byte length
		int byteLen = ProtocolLengths.COMMAND + ProtocolLengths.QQADIDAS_RESULT;

		// assemble result bytes
		byte[] resultByte = new byte[byteLen];
		int index = 0;
		Tools.putUnsignedInt(resultByte, cmdId, index);
		index += ProtocolLengths.COMMAND;

		// result
		Tools.putUnsignedInt(resultByte, result, index);
		index += ProtocolLengths.QQADIDAS_RESULT;

		log.debug("QQWeixinSignInRespMsgCodec encode end, result byte size:{}",
				resultByte.length);

		return resultByte;
	}

}
