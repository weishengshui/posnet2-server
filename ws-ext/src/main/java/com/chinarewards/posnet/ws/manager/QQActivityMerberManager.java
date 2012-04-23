package com.chinarewards.posnet.ws.manager;

import com.chinarewards.posnet.ws.vo.SynMemberResp;

public interface QQActivityMerberManager {
	
	/**
	 * parse and handle originStr, Generate a qq member.
	 * 
	 * @param originStr
	 * 
	 * @return SynMemberResp
	 */
	public SynMemberResp generateQQActivityMember(String originStr, final String secretKey);
	
}
