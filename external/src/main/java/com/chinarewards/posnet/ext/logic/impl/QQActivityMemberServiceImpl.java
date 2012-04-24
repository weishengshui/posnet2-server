package com.chinarewards.posnet.ext.logic.impl;

import java.util.Date;

import com.chinarewards.ext.api.qq.adidas.exception.MemberKeyExistedException;
import com.chinarewards.ext.api.qq.adidas.service.QQActivityMemberService;
import com.chinarewards.posnet.ext.dao.QQActivityMemberDao;
import com.chinarewards.qq.adidas.domain.GiftStatus;
import com.chinarewards.qq.adidas.domain.PrivilegeStatus;
import com.chinarewards.qq.adidas.domain.QQActivityMember;
import com.chinarewards.qqgbvpn.common.DateTimeProvider;
import com.google.inject.Inject;

public class QQActivityMemberServiceImpl implements QQActivityMemberService {

	@Inject
	QQActivityMemberDao memberDao;

	@Inject
	DateTimeProvider dateTimeProvider;

	@Override
	public String generateQQActivityMember(String memberKey, Date sendTime)
			throws MemberKeyExistedException {
		QQActivityMember foundMember = memberDao.findQQMemberByKey(memberKey);
		if (foundMember != null) {
			throw new MemberKeyExistedException();
		}
		Date now = dateTimeProvider.getTime();

		QQActivityMember member = new QQActivityMember();
		member.setCreatedAt(now);
		member.setLastModifiedAt(now);
		member.setGiftStatus(GiftStatus.NEW);
		member.setPrivilegeStatus(PrivilegeStatus.NEW);
		member.setMemberKey(memberKey);
		member.setSendTime(sendTime);
		memberDao.insert(member);

		return member.getId();
	}
}
