package com.chinarewards.qqgbvpn.testing.lab.business;

import java.util.HashMap;
import java.util.Map;

import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import com.chinarewards.qqgbvpn.main.protocol.cmd.ErrorBodyMessage;
import com.chinarewards.qqgbvpn.main.protocol.cmd.Message;
import com.chinarewards.qqgbvpn.testing.exception.BuildBodyMessageException;
import com.chinarewards.qqgbvpn.testing.exception.RunTaskException;
import com.chinarewards.qqgbvpn.testing.lab.PosTask;
import com.chinarewards.qqgbvpn.testing.lab.business.message.BuildMessage;
import com.chinarewards.qqgbvpn.testing.lab.business.message.BusinessType;
import com.chinarewards.qqgbvpn.testing.lab.business.message.MessageFactory;

/**
 * description：POS机Init
 * @copyright binfen.cc
 * @projectName testing
 * @time 2011-9-22   下午06:02:53
 * @author Seek
 */
public final class PosInitTask extends PosTask {
	
	@Override
	public SampleResult runTask(JavaSamplerContext context) throws RunTaskException {
		SampleResult res = new SampleResult();
		res.sampleStart();	//开始任务
		
		try{
			byte[] bodys =  buildBodyMessage(context);
			Message message = super.sendMessage(context, bodys);
			
			if(message.getBodyMessage() instanceof ErrorBodyMessage){
				res.setSuccessful(false);
			}else{
				res.setSuccessful(true);
			}
		}catch(Throwable e){
			res.setSuccessful(false);
			throw new RunTaskException(e);
		}finally{
			res.sampleEnd();	//结束任务
		}
		return res;
	}

	@Override
	protected byte[] buildBodyMessage(JavaSamplerContext context)
			throws BuildBodyMessageException {
		Map<String, String> map = new HashMap<String, String>();
		BuildMessage buildMessage = MessageFactory.getBuildMessage(BusinessType.PosInit);
		return buildMessage.buildBodyMessage(map);
	}
	
}
