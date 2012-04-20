package com.chinarewards.qqgbvpn.main.logic.qqadidas.impl;

import java.util.Date;

import javax.persistence.EntityManager;

import com.chinarewards.qq.adidas.domain.GiftStatus;
import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityMember;
import com.chinarewards.ws.ext.api.qq.adidas.exception.MemberKeyExistedException;
import com.chinarewards.ws.ext.api.qq.adidas.service.QQActivityMemberService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class QQActivityMemberServiceImpl implements QQActivityMemberService {

	@Inject
	protected Provider<EntityManager> emp;

	@Transactional
	@Override
	public String generateQQActivityMember(String memberKey, Date timestamp)
			throws MemberKeyExistedException {
		Date now = new Date();
		QQActivityMember member = new QQActivityMember();
		member.setMemberKey(memberKey);
		member.setGiftStatus(GiftStatus.NEW);
		member.setPrivilegeStatus(PrivilegeStatus.NEW);
		member.setSendTime(timestamp);
		member.setCreatedAt(now);
		member.setLastModifiedAt(now);
		emp.get().persist(member);
		emp.get().flush();
		return member.getId();
	}

}
