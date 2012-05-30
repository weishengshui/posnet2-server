package com.chinarewards.qqgbvpn.testing.lab.v0930;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import com.chinarewards.qqgbvpn.main.protocol.cmd.Message;
import com.chinarewards.qqgbvpn.testing.context.TestContext;
import com.chinarewards.qqgbvpn.testing.exception.RunTaskException;
import com.chinarewards.qqgbvpn.testing.exception.SendMessageException;
import com.chinarewards.qqgbvpn.testing.lab.PosTask;
import com.chinarewards.qqgbvpn.testing.lab.business.PosGetQQGroupBuyValidationTask;
import com.chinarewards.qqgbvpn.testing.lab.business.message.BuildMessage;
import com.chinarewards.qqgbvpn.testing.lab.business.message.BusinessType;
import com.chinarewards.qqgbvpn.testing.lab.business.message.MessageFactory;
import com.chinarewards.qqgbvpn.testing.util.SocketUtil;

/**
 * description：分次发的的package
 * @copyright binfen.cc
 * @projectName testing
 * @time 2011-9-29   上午10:30:53
 * @author Seek
 */
public class Temp5Task extends PosTask {
	
	private static final String SEND_TIMESTAMP = "SEND_TIMESTAMP(unit of time:Second)";
	private static final String FORE_BYTES_LEN = "FORE_BYTES_LEN";
	
	@Override
	public Arguments getDefaultParameters() {
		Arguments arguments = new Arguments();
		arguments.addArgument(SEND_TIMESTAMP, "10");
		arguments.addArgument(FORE_BYTES_LEN, "18");
		
		arguments.addArgument(PosGetQQGroupBuyValidationTask.GROUPON_VCODE, "12345678");
		return arguments;
	}
	
	@Override
	protected SampleResult runTask(JavaSamplerContext context)
			throws RunTaskException {
		SampleResult res = new SampleResult();
		res.sampleStart();	//开始任务
		
		try{
			sendMessage(context);
			
			res.setSuccessful(true);
		}catch(Throwable e){
			res.setSuccessful(false);
			throw new RunTaskException(e);
		}finally{
			res.sampleEnd();	//结束任务
		}
		return res;
	}
	
	private Message sendMessage(JavaSamplerContext context) throws SendMessageException {
		try{
			//build body Message
			Map<String, String> map = new HashMap<String, String>();
			BuildMessage getGroupBuyListBuildMessage = MessageFactory.getBuildMessage(
					BusinessType.PosGetQQGroupBuyList);
			byte[] bodys = getGroupBuyListBuildMessage.buildBodyMessage(map);
			
			//format package content
			byte[] requestBytes = formatPackageContent(context, bodys);
			
			
			//自增流水号
			TestContext.incrementSequence();
			
			
			//build get qq group buy validation body Message
			map.clear();
			map.put(PosGetQQGroupBuyValidationTask.GROUPON_VCODE, context.
					getParameter(PosGetQQGroupBuyValidationTask.GROUPON_VCODE));
			
			BuildMessage getGroupBuyValidationBuildMessage = MessageFactory.getBuildMessage(
					BusinessType.PosGetQQGroupBuyValidation);
			byte[] tailBodys = getGroupBuyValidationBuildMessage.buildBodyMessage(map);
			
			//format package content
			byte[] requestBytes2 = formatPackageContent(context, tailBodys);
			
			
			/****************************************************************************/
			int foreBytesLen = Integer.parseInt(context.getParameter(FORE_BYTES_LEN));
			
			byte[] foreRequestBytes = new byte[requestBytes.length + foreBytesLen];
			
			System.arraycopy(requestBytes, 0, foreRequestBytes, 0, requestBytes.length);
			System.arraycopy(requestBytes2, 0, foreRequestBytes, requestBytes.length, foreBytesLen);
			
			SocketUtil.writeFromOutputStream(TestContext.getBasePosConfig().getSocket().
					getOutputStream(), foreRequestBytes);
			
			//sleep
			Long sendTimestamp = Long.parseLong(context.getParameter(SEND_TIMESTAMP));
			logger.debug("sleep "+sendTimestamp+" Second...");
			Thread.sleep(sendTimestamp * 1000);
			
			//tail bytes
			byte[] tailRequestBytes = new byte[requestBytes2.length - foreBytesLen];
			System.arraycopy(requestBytes2, foreBytesLen, tailRequestBytes, 0, tailRequestBytes.length);
			
			SocketUtil.writeFromOutputStream(TestContext.getBasePosConfig().getSocket().
					getOutputStream(), tailRequestBytes);
			/****************************************************************************/
			
			byte[] responseBytes = SocketUtil.readFromInputStream(TestContext.
					getBasePosConfig().getSocket().getInputStream());
			
			logger.debug("responseBytes:"+Arrays.toString(responseBytes) +
					", length="+responseBytes.length);
			
			logger.debug("responseBytes:"+Arrays.toString(responseBytes) +
					", length="+responseBytes.length);
			//将package解析并封装成Message
			Message message = super.parseResponseContent(context, responseBytes);
			
			return message;
		}catch(Throwable e){
			throw new SendMessageException(e);
		}
	}
	
}
