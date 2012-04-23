package com.chinarewards.ext.api.qq.adidas.service;

import java.util.Date;

import com.chinarewards.ext.api.qq.adidas.exception.MemberKeyExistedException;

/**
 * The interface to ws.
 * 
 * @author yanxin
 * @since 0.3.0
 */
public interface QQActivityMemberService {

	/**
	 * Generate a qq member according to the specified memberkey come from QQ.
	 * At the same time, should note the timestamp come from qq.
	 * 
	 * @param memberKey
	 * @param timestamp
	 * 
	 * @return id of entity QQActivityMember.
	 * 
	 * @throws MemberKeyExistedException
	 *             if the specified memberkey existed in the database already.
	 */
	public String generateQQActivityMember(String memberKey, Date timestamp)
			throws MemberKeyExistedException;
}
