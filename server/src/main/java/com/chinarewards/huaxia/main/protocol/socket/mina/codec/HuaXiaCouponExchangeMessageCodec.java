package com.chinarewards.huaxia.main.protocol.socket.mina.codec;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.huaxia.main.protocol.cmd.HuaXiaCouponExchangeRequestMessage;
import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.exception.PackageException;
import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;
import com.chinarewards.qqgbvpn.main.protocol.socket.ProtocolLengths;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.ICommandCodec;

/**
 * huaxia couponExchange coder
 * 
 * @author weishengshui
 * 
 */
public class HuaXiaCouponExchangeMessageCodec implements ICommandCodec {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public ICommand decode(IoBuffer in, Charset charset)
			throws PackageException {
		log.debug("huaxia couponExchange message decode");
		HuaXiaCouponExchangeRequestMessage message = new HuaXiaCouponExchangeRequestMessage();
		if (in.remaining() < ProtocolLengths.HEAD
				+ ProtocolLengths.POSNETSTRLEN * 2) {
			throw new PackageException(
					"huaxia couponExchange message body error, body message is : "
							+ in);
		}
		// read from buffer
		long cmdId = in.getUnsignedInt();
		int cdkeyLength = in.getUnsignedShort();
		byte[] cdkeyBytes = new byte[cdkeyLength];
		in.get(cdkeyBytes);
		int cardNoLength = in.getUnsignedShort();
		byte[] cardNoBytes = new byte[cardNoLength];
		in.get(cardNoBytes);

		// construct message
		message.setCmdId(cmdId);
		message.setCdkeyLength(cdkeyLength);
		message.setCdkey(Tools.byteToString(cdkeyBytes, charset));
		message.setCardNoLength(cardNoLength);
		message.setCardNo(Tools.byteToString(cardNoBytes, charset));
		return message;
	}

	@Override
	public byte[] encode(ICommand bodyMessage, Charset charset) {
		log.debug("huaxia couponExchange message encode");

		HuaXiaCouponExchangeRequestMessage requestMessage = (HuaXiaCouponExchangeRequestMessage) bodyMessage;
		long cmdId = requestMessage.getCmdId();
		int cdkeyLength = requestMessage.getCdkeyLength();
		String cdkey = requestMessage.getCdkey();
		int cardNoLength = requestMessage.getCardNoLength();
		String cardNo = requestMessage.getCardNo();

		byte[] resultBytes = new byte[ProtocolLengths.COMMAND
				+ ProtocolLengths.POSNETSTRLEN + cdkeyLength
				+ ProtocolLengths.POSNETSTRLEN + cardNoLength];

		Tools.putUnsignedInt(resultBytes, cmdId, 0);
		Tools.putUnsignedShort(resultBytes, cdkeyLength,
				ProtocolLengths.COMMAND);
		Tools.putBytes(resultBytes, cdkey.getBytes(charset),
				ProtocolLengths.COMMAND + ProtocolLengths.POSNETSTRLEN);
		Tools.putUnsignedShort(resultBytes, cardNoLength,
				ProtocolLengths.COMMAND + ProtocolLengths.POSNETSTRLEN
						+ cdkeyLength);
		Tools.putBytes(resultBytes, cardNo.getBytes(charset),
				ProtocolLengths.COMMAND + ProtocolLengths.POSNETSTRLEN
						+ cdkeyLength + ProtocolLengths.POSNETSTRLEN);
		
		log.trace("HuaXiaCouponExchangeRequestMessage:{}",requestMessage);
		
		return resultBytes;
	}

}
