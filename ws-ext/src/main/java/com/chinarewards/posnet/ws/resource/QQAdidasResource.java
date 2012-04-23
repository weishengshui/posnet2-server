package com.chinarewards.posnet.ws.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.posnet.ws.manager.QQActivityMerberManager;
import com.chinarewards.posnet.ws.vo.SynMemberResp;
import com.google.inject.Inject;

/**
 * description：QQ-Adidas resource!
 * @copyright binfen.cc
 * @projectName ws-ext
 * @time 2012-4-20   下午04:16:03
 * @author Seek
 */
@Path("/qq-adidas")
public class QQAdidasResource {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String QQVIP_ADIDAS_DES_SECRET_KEY = "qq.vip.adidas.des.secretkey";
	
	@Inject
	Configuration configuration;

	@Inject
	QQActivityMerberManager merberManager;

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/sync/qq/member")
	public SynMemberResp synMember(String originStr) {
		String secretKey = configuration.getString(QQVIP_ADIDAS_DES_SECRET_KEY);
		logger.debug("originStr:"+ originStr);
		logger.debug("secretKey:"+ secretKey);
		
		try{
			return merberManager.generateQQActivityMember(originStr, secretKey);
		}catch(Throwable e){
			return new SynMemberResp(4);
		}
	}
}
