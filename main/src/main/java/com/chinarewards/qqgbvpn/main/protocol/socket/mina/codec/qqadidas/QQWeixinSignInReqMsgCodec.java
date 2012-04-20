package com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.exception.PackageException;
import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQWeixinSignInReqMsg;
import com.chinarewards.qqgbvpn.main.protocol.socket.ProtocolLengths;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.ICommandCodec;
import com.chinarewards.qqgbvpn.main.util.Str2ByteUtil;

/**
 * QQ weixin sign in request message codec.
 * 
 * @author yanxin
 * @since 0.3.0
 */
public class QQWeixinSignInReqMsgCodec implements ICommandCodec {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public ICommand decode(IoBuffer in, Charset charset)
			throws PackageException {
		log.debug("QQWeixinSignInReqMsgCodec decode begin!");

		int minLen = ProtocolLengths.COMMAND + ProtocolLengths.POSNETSTRLEN;
		if (in.remaining() < minLen) {
			throw new PackageException(
					"qq weixin sign in packge message body error, body message is :"
							+ in);
		}

		// 指令ID
		in.getUnsignedInt();

		// weixin id len
		int weixinIdLen = in.getUnsignedShort();

		String weixinId = "";
		if (weixinIdLen > 0) {
			byte[] weixinIdByte = new byte[weixinIdLen];
			in.get(weixinIdByte);
			weixinId = new String(weixinIdByte, charset);
		}

		QQWeixinSignInReqMsg msg = new QQWeixinSignInReqMsg(weixinId);

		log.debug("QQWeixinSignInReqMsgCodec decode end, msg:{}", msg);
		return msg;
	}

	/**
	 * QQ weixin sign in request message codec.<br/>
	 * description：mock pos test use it!
	 * 
	 * @param bodyMessage
	 * @param charset
	 * @return
	 */
	@Override
	public byte[] encode(ICommand bodyMessage, Charset charset) {

		log.debug("QQWeixinSignInReqMsgCodec encode begin!");
		QQWeixinSignInReqMsg reqMsg = (QQWeixinSignInReqMsg) bodyMessage;

		long cmdId = reqMsg.getCmdId();
		String weixinId = reqMsg.getWeixinId();

		// calculate byte length
		int byteLen = ProtocolLengths.COMMAND + ProtocolLengths.POSNETSTRLEN;
		int weixinIdLen = Str2ByteUtil.getLen(weixinId, charset);
		byteLen += weixinIdLen;

		byte[] resultByte = new byte[byteLen];
		int index = 0;
		// 指令ID
		Tools.putUnsignedInt(resultByte, cmdId, index);
		index += ProtocolLengths.COMMAND;

		// weixin id
		Tools.putUnsignedShort(resultByte, weixinIdLen, index);
		index += ProtocolLengths.POSNETSTRLEN;
		if (weixinIdLen > 0) {
			Tools.putBytes(resultByte,
					Str2ByteUtil.str2Byte(weixinId, charset), index);
			index += weixinIdLen;
		}

		log.debug(
				"QQWeixinSignInReqMsgCodec encode end!The result byte size:{}",
				resultByte.length);

		return resultByte;
	}

}
