package com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.exception.PackageException;
import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.socket.ProtocolLengths;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.ICommandCodec;
import com.chinarewards.qqgbvpn.main.util.Str2ByteUtil;

/**
 * QQVIP obtain gift request message codec.
 * 
 * @author yanxin
 * @since 0.3.0
 */
public class QQVIPObtainGiftReqMsgCodec implements ICommandCodec {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public ICommand decode(IoBuffer in, Charset charset)
			throws PackageException {
		log.debug("QQVIPObtainGiftReqMsgCodec decode begin!");

		int minLen = ProtocolLengths.COMMAND + ProtocolLengths.POSNETSTRLEN;
		if (in.remaining() < minLen) {
			throw new PackageException(
					"qqvip obtain gift packge message body error, body message is :"
							+ in);
		}

		// 指令ID
		in.getUnsignedInt();

		// user_code len
		int userCodeLen = in.getUnsignedShort();
		if (userCodeLen > ProtocolLengths.QQADIDAS_MEMBER_KEY) {
			throw new PackageException(
					"qqvip obtain gift packge message body error, user code len :"
							+ userCodeLen);
		}

		String userCode = "";
		if (userCodeLen > 0) {
			byte[] userCodeByte = new byte[userCodeLen];
			in.get(userCodeByte);
			userCode = new String(userCodeByte, charset);
		}

		QQVIPObtainGiftReqMsg msg = new QQVIPObtainGiftReqMsg(userCode);
		log.debug("QQVIPObtainGiftReqMsgCodec decode end, msg:{}", msg);
		return msg;
	}

	/**
	 * QQVIP obtain gift request message codec.<br/>
	 * description：mock pos test use it!
	 * 
	 * @param bodyMessage
	 * @param charset
	 * @return
	 */
	@Override
	public byte[] encode(ICommand bodyMessage, Charset charset) {

		log.debug("QQVIPObtainGiftReqMsgCodec encode begin!");
		QQVIPObtainGiftReqMsg reqMsg = (QQVIPObtainGiftReqMsg) bodyMessage;

		long cmdId = reqMsg.getCmdId();
		String userCode = reqMsg.getUserCode();

		// calculate byte length
		int byteLen = ProtocolLengths.COMMAND + ProtocolLengths.POSNETSTRLEN;
		int userCodeLen = Str2ByteUtil.getLen(userCode, charset);
		byteLen += userCodeLen;

		byte[] resultByte = new byte[byteLen];
		int index = 0;
		// 指令ID
		Tools.putUnsignedInt(resultByte, cmdId, index);
		index += ProtocolLengths.COMMAND;

		// userCode
		Tools.putUnsignedShort(resultByte, userCodeLen, index);
		index += ProtocolLengths.POSNETSTRLEN;
		if (userCodeLen > 0) {
			Tools.putBytes(resultByte,
					Str2ByteUtil.str2Byte(userCode, charset), index);
			index += userCodeLen;
		}

		log.debug(
				"QQVIPObtainGiftReqMsgCodec encode end!The result byte size:{}",
				resultByte.length);

		return resultByte;
	}

}
