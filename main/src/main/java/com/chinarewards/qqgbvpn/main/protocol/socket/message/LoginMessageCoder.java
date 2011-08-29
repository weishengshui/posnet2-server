package com.chinarewards.qqgbvpn.main.protocol.socket.message;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;

import com.chinarewards.qqgbvpn.common.Tools;
import com.chinarewards.qqgbvpn.main.exception.PackgeException;
import com.chinarewards.qqgbvpn.main.protocol.socket.ProtocolLengths;

/**
 * login coder
 * 
 * @author huangwei
 * 
 */
public class LoginMessageCoder implements IBodyMessageCoder {

	@Override
	public IBodyMessage decode(IoBuffer in, Charset charset)
			throws PackgeException {
		LoginRequestMessage message = new LoginRequestMessage();
		if (in.remaining() != ProtocolLengths.POS_SERIAL
				+ ProtocolLengths.COMMAND + ProtocolLengths.POS_ID
				+ ProtocolLengths.CHALLEUGERESPONSE) {
			throw new PackgeException(
					"login packge message body error, body message is :" + in);
		}
		int cmdId = in.getUnsignedShort();
		long serial = in.getUnsignedInt();
		byte[] posid = new byte[ProtocolLengths.POS_ID];
		byte[] challeugeresponse = new byte[ProtocolLengths.CHALLEUGERESPONSE];
		in.get(posid);
		in.get(challeugeresponse);
		message.setCmdId(cmdId);
		message.setSerial(serial);
		message.setChalleugeresponse(challeugeresponse);
		message.setPosid(new String(posid, charset));
		return message;
	}

	@Override
	public byte[] encode(IBodyMessage bodyMessage, Charset charset) {

		LoginResponseMessage responseMessage = (LoginResponseMessage) bodyMessage;
		int cmdId = responseMessage.getCmdId();
		long serial = responseMessage.getSerial();
		int result = responseMessage.getResult();
		byte[] challeuge = responseMessage.getChalleuge();

		byte[] resultByte = new byte[ProtocolLengths.POS_SERIAL+ProtocolLengths.COMMAND+ProtocolLengths.RESULT+ProtocolLengths.CHALLEUGE];
		
		Tools.putUnsignedShort(resultByte, cmdId, 0);
		Tools.putUnsignedInt(resultByte, serial, ProtocolLengths.COMMAND);
		Tools.putUnsignedShort(resultByte, result, ProtocolLengths.POS_SERIAL+ProtocolLengths.COMMAND);
		Tools.putBytes(resultByte, challeuge, ProtocolLengths.POS_SERIAL+ProtocolLengths.COMMAND+ProtocolLengths.RESULT);
		
		return resultByte;
	}



}
