package com.chinarewards.posnet.ws.resource;

import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.chinarewards.posnet.ws.vo.synMemberResp;
import com.chinarewards.ws.ext.api.qq.adidas.exception.MemberKeyExistedException;
import com.chinarewards.ws.ext.api.qq.adidas.service.QQActivityMemberService;
import com.google.inject.Inject;

/**
 * QQ-Adidas resource!
 * 
 * @author yanxin
 * 
 */
@Path("/qq-adidas")
public class QQAdidasResource {

	@Inject
	QQActivityMemberService memberService;

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/sync/qq/member")
	public synMemberResp synMember(String originStr) {
		int returncode = -1;
		if (originStr == null || "".equals(originStr)) {
			returncode = 2;
		} else {
			try {
				memberService.generateQQActivityMember(originStr, new Date());
			} catch (MemberKeyExistedException e) {
				returncode = 3;
			}
		}
		returncode = 0;
		return new synMemberResp(returncode);
	}
}
