package com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.qqadidas;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.exception.PackageException;
import com.chinarewards.qqgbvpn.main.protocol.cmd.ICommand;
import com.chinarewards.qqgbvpn.main.protocol.cmd.qqadidas.QQVIPObtainGiftRespMsg;
import com.chinarewards.qqgbvpn.main.protocol.socket.ProtocolLengths;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.ICommandCodec;

public class QQVIPObtainGiftRespMsgCodec implements ICommandCodec {

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
		log.debug("QQVIPObtainGiftRespMsgCodec decode begin!");

		// 指令ID
		in.getUnsignedInt();
		// result
		long result = in.getUnsignedInt();
		Date xactTime = null;
		String title = null;
		String tip = null;

		byte[] xactTimeBytes = new byte[ProtocolLengths.CR_DATE_LENGTH];
		in.get(xactTimeBytes);
		Calendar c = Tools.getDate(xactTimeBytes, 0);
		xactTime = c.getTime();

		int titleLen = in.getUnsignedShort();
		if (titleLen > 0) {
			byte[] titleBytes = new byte[titleLen];
			in.get(titleBytes);
			title = new String(titleBytes, charset);
		}

		int tipLen = in.getUnsignedShort();
		if (tipLen > 0) {
			byte[] tipBytes = new byte[tipLen];
			in.get(tipBytes);
			tip = new String(tipBytes, charset);
		}

		QQVIPObtainGiftRespMsg msg = new QQVIPObtainGiftRespMsg(result,
				xactTime, title, tip);

		log.debug("QQVIPObtainGiftRespMsgCodec decode end, msg:{}", msg);
		return msg;
	}

	@Override
	public byte[] encode(ICommand bodyMessage, Charset charset) {
		log.debug("QQVIPObtainGiftRespMsgCodec encode begin!");
		QQVIPObtainGiftRespMsg responseMessage = (QQVIPObtainGiftRespMsg) bodyMessage;

		long cmdId = responseMessage.getCmdId();
		long result = responseMessage.getResult();
		Date xactTime = responseMessage.getXactTime();
		String title = responseMessage.getTitle();
		String tip = responseMessage.getTip();

		// calculate byte length
		int byteLen = ProtocolLengths.COMMAND + ProtocolLengths.QQADIDAS_RESULT
				+ ProtocolLengths.CR_DATE_LENGTH + ProtocolLengths.POSNETSTRLEN
				+ ProtocolLengths.POSNETSTRLEN;
		// title
		int titleLen = (title == null) ? 0 : title.length();
		byte[] titleBytes = null;
		if (titleLen > 0) {
			titleBytes = title.getBytes(charset);
			titleLen = titleBytes.length;
		}
		byteLen += titleLen;
		// tip
		byteLen += ProtocolLengths.POSNETSTRLEN;
		int tipLen = (tip == null) ? 0 : tip.length();
		byte[] tipBytes = null;
		if (tipLen > 0) {

			tipBytes = tip.getBytes(charset);
			tipLen = tipBytes.length;
		}
		byteLen += tipLen;

		// assemble result bytes
		byte[] resultByte = new byte[byteLen];
		int index = 0;
		Tools.putUnsignedInt(resultByte, cmdId, index);
		index += ProtocolLengths.COMMAND;

		// result
		Tools.putUnsignedInt(resultByte, result, index);
		index += ProtocolLengths.QQADIDAS_RESULT;

		// xact time
		if (xactTime != null) {
			Calendar ca = Calendar.getInstance();
			ca.setTime(xactTime);
			Tools.putDate(resultByte, ca, index);
		} else {
			for (int i = 0; i < ProtocolLengths.CR_DATE_LENGTH; i++) {
				resultByte[index + i] = 0;
			}
		}
		index += ProtocolLengths.CR_DATE_LENGTH;

		// title
		Tools.putUnsignedShort(resultByte, titleLen, index);
		index += ProtocolLengths.POSNETSTRLEN;
		if (titleLen > 0) {
			Tools.putBytes(resultByte, titleBytes, index);
			index += titleBytes.length;
		}

		// tip
		Tools.putUnsignedShort(resultByte, tipLen, index);
		index += ProtocolLengths.POSNETSTRLEN;
		if (tipLen > 0) {
			log.debug("tipBytes.length=" + tipBytes.length);
			log.debug("index=" + index);
			Tools.putBytes(resultByte, tipBytes, index);
			index += tipBytes.length;
		}

		log.debug(
				"QQVIPObtainGiftRespMsgCodec encode end, result byte size:{}",
				resultByte.length);

		return resultByte;
	}

}
