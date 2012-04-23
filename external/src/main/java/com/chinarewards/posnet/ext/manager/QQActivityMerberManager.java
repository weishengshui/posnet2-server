package com.chinarewards.posnet.ext.manager;

import com.chinarewards.posnet.ext.vo.SynMemberResp;

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
