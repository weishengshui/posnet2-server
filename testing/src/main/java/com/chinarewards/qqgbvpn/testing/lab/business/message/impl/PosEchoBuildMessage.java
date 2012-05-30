package com.chinarewards.qqgbvpn.testing.lab.business.message.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.main.protocol.SimpleCmdCodecFactory;
import com.chinarewards.qqgbvpn.main.protocol.cmd.PosEchoCommandRequestMessage;
import com.chinarewards.qqgbvpn.main.protocol.socket.mina.codec.ICommandCodec;
import com.chinarewards.qqgbvpn.testing.context.TestContext;
import com.chinarewards.qqgbvpn.testing.exception.BuildBodyMessageException;
import com.chinarewards.qqgbvpn.testing.lab.business.PosEchoTask;
import com.chinarewards.qqgbvpn.testing.lab.business.message.BuildMessage;

/**
 * description：pos echo process
 * @copyright binfen.cc
 * @projectName testing
 * @time 2011-10-21   上午11:08:06
 * @author Seek
 */
public class PosEchoBuildMessage implements BuildMessage {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public byte[] buildBodyMessage(Map<String,String> context) throws BuildBodyMessageException {
		logger.debug("PosEchoTask buildBodyMessage() run...");
		try{
			PosEchoCommandRequestMessage bodyMessage = new PosEchoCommandRequestMessage();
			bodyMessage.setData(context.get(PosEchoTask.ECHO_DATA).getBytes("gb2312"));
			
			SimpleCmdCodecFactory cmdCodecFactory = TestContext.getCmdCodecFactory();
			ICommandCodec codec = cmdCodecFactory.getCodec(bodyMessage.getCmdId());
			
			byte[] bodys = codec.encode(bodyMessage, TestContext.getCharset());
			return bodys;
		}catch(Throwable e){
			throw new BuildBodyMessageException(e);
		}
	}
	
	public static void main(String[] args) throws Throwable{
		System.out.println("abcdefg".getBytes("gb2312").length);
	}
	
}
