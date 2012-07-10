package com.chinarewards.huaxia.main.protocol.socket.mina.codec;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.huaxia.main.protocol.cmd.HuaXiaCouponExchangeResponseMessage;
import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.exception.PackageException;
import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;
import com.chinarewards.qqgbvpn.main.protocol.socket.ProtocolLengths;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.ICommandCodec;

/**
 * huaxia couponExchange response coder
 * 
 * @author weishengshui
 * 
 */
public class HuaXiaCouponExchangeResponseMessageCodec implements ICommandCodec {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public ICommand decode(IoBuffer in, Charset charset)
			throws PackageException {
		log.debug("huaxia couponExchange response message decode");
		HuaXiaCouponExchangeResponseMessage message = new HuaXiaCouponExchangeResponseMessage();
		if (in.remaining() < ProtocolLengths.HEAD
				+ ProtocolLengths.HUAXIA_COUPON_EXCHANGE_RESULT
				+ ProtocolLengths.CR_DATE_LENGTH + ProtocolLengths.POSNETSTRLEN
				* 2) {
			throw new PackageException(
					"huaxia couponExchange response message body error, body message is : "
							+ in);
		}
		// read from buffer
		long cmdId = in.getUnsignedInt();
		int result = in.getUnsignedShort();
		byte[] xact_timeBytes = new byte[ProtocolLengths.CR_DATE_LENGTH];
		in.get(xact_timeBytes);
		Date xact_time = Tools.getDate(xact_timeBytes, 0);
		int titleLength = in.getUnsignedShort();
		byte[] titleBytes = new byte[titleLength];
		in.get(titleBytes);
		int tipLength = in.getUnsignedShort();
		byte[] tipBytes = new byte[tipLength];
		in.get(tipBytes);

		// construct message
		message.setCmdId(cmdId);
		message.setResult(result);
		message.setXact_time(xact_time);
		message.setTitleLength(titleLength);
		message.setTitle(Tools.byteToString(titleBytes, charset));
		message.setTipLength(tipLength);
		message.setTip(Tools.byteToString(tipBytes, charset));

		return message;
	}

	@Override
	public byte[] encode(ICommand bodyMessage, Charset charset) {
		log.debug("huaxia couponExchange response message encode");

		HuaXiaCouponExchangeResponseMessage responseMessage = (HuaXiaCouponExchangeResponseMessage) bodyMessage;
		long cmdId = responseMessage.getCmdId();
		int result = responseMessage.getResult();
		Calendar xact_time = Calendar.getInstance();
		xact_time.setTime(responseMessage.getXact_time());
		int titleLength = responseMessage.getTipLength();
		String title = responseMessage.getTitle();
		int tipLength = responseMessage.getTipLength();
		String tip = responseMessage.getTip();

		byte[] resultBytes = new byte[ProtocolLengths.COMMAND
				+ ProtocolLengths.HUAXIA_COUPON_EXCHANGE_RESULT
				+ ProtocolLengths.CR_DATE_LENGTH + ProtocolLengths.POSNETSTRLEN
				* 2 + titleLength + tipLength];

		Tools.putUnsignedInt(resultBytes, cmdId, 0);
		Tools.putUnsignedShort(resultBytes, result, ProtocolLengths.COMMAND);
		Tools.putDate(resultBytes, xact_time, ProtocolLengths.COMMAND
				+ ProtocolLengths.HUAXIA_COUPON_EXCHANGE_RESULT);
		Tools.putUnsignedShort(resultBytes, titleLength,
				ProtocolLengths.COMMAND
						+ ProtocolLengths.HUAXIA_COUPON_EXCHANGE_RESULT
						+ ProtocolLengths.CR_DATE_LENGTH);
		Tools.putBytes(resultBytes, title.getBytes(charset),
				ProtocolLengths.COMMAND
						+ ProtocolLengths.HUAXIA_COUPON_EXCHANGE_RESULT
						+ ProtocolLengths.CR_DATE_LENGTH
						+ ProtocolLengths.POSNETSTRLEN);
		Tools.putUnsignedShort(resultBytes, tipLength, ProtocolLengths.COMMAND
				+ ProtocolLengths.HUAXIA_COUPON_EXCHANGE_RESULT
				+ ProtocolLengths.CR_DATE_LENGTH + ProtocolLengths.POSNETSTRLEN
				+ titleLength);
		Tools.putBytes(resultBytes, tip.getBytes(charset),
				ProtocolLengths.COMMAND
						+ ProtocolLengths.HUAXIA_COUPON_EXCHANGE_RESULT
						+ ProtocolLengths.CR_DATE_LENGTH
						+ ProtocolLengths.POSNETSTRLEN * 2 + titleLength);

		log.trace("HuaXiaCouponExchangeResponseMessage:{}", responseMessage);

		return resultBytes;
	}

}
