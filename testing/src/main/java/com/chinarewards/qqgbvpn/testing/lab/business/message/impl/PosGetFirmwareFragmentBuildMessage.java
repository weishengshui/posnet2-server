package com.chinarewards.qqgbvpn.testing.lab.business.message.impl;

import java.util.Map;

import com.chinarewards.qqgbvpn.main.protocol.SimpleCmdCodecFactory;
import com.chinarewards.qqgbvpn.main.protocol.cmd.GetFirmwareFragmentRequestMessage;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.ICommandCodec;
import com.chinarewards.qqgbvpn.testing.context.TestContext;
import com.chinarewards.qqgbvpn.testing.exception.BuildBodyMessageException;
import com.chinarewards.qqgbvpn.testing.lab.business.message.BuildMessage;
import com.chinarewards.qqgbvpn.testing.lab.firmware.PosGetFirmwareFragmentTask;

/**
 * description：pos get firmware fragment process
 * @copyright binfen.cc
 * @projectName testing
 * @time 2011-9-30   下午05:53:51
 * @author Seek
 */
public class PosGetFirmwareFragmentBuildMessage implements BuildMessage {
	
	@Override
	public byte[] buildBodyMessage(Map<String, String> context) throws BuildBodyMessageException {
		try{
			String posId = TestContext.getBasePosConfig().getPosId();
			long offset = TestContext.getBasePosConfig().getFirmwareOffset();
			long length = Long.parseLong(context.get(PosGetFirmwareFragmentTask.GET_FIRMWARE_FRAGMENT_LENGTH));
			GetFirmwareFragmentRequestMessage bodyMessage = new 
					GetFirmwareFragmentRequestMessage(posId, offset, length);
			
			SimpleCmdCodecFactory cmdCodecFactory = TestContext.getCmdCodecFactory();
			ICommandCodec codec = cmdCodecFactory.getCodec(bodyMessage.getCmdId());
			
			byte[] bodys = codec.encode(bodyMessage, TestContext.getCharset());
			return bodys;
		}catch(Throwable e){
			throw new BuildBodyMessageException(e);
		}
	}
	
}
