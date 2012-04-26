package com.chinarewards.qq.adidas.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * Defines a QQ member due to <code>MEMBERKEY</code>. Every member has two kinds
 * of status: gift status and privilege status.
 * 
 * @author yanxin
 * @since 0.3.0
 */
@Entity
public class QQActivityMember {
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/**
	 * It flag a member.
	 */
	@Column(unique=true, nullable=false) 
	private String memberKey;

	/**
	 * Whether a member has got a gift.
	 */
	@Enumerated(EnumType.STRING)
	private GiftStatus giftStatus;

	/**
	 * Whether a member has got a privilege.
	 */
	@Enumerated(EnumType.STRING)
	private PrivilegeStatus privilegeStatus;

	/**
	 * The sent time which is part of the request body sent from QQ.
	 * 
	 * The different between {@link QQActivityMember#sendTime} and
	 * {@link QQActivityMember#createdAt} is that one is send message time and
	 * the other is the record time. e.g. QQ send a synch member message at
	 * 12:00:00, maybe the createdAt is 12:00:01
	 */
	private Date sendTime;

	/**
	 * Record create time
	 */
	private Date createdAt;

	/**
	 * Record last modified time
	 */
	private Date lastModifiedAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMemberKey() {
		return memberKey;
	}

	public void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
	}

	public GiftStatus getGiftStatus() {
		return giftStatus;
	}

	public void setGiftStatus(GiftStatus giftStatus) {
		this.giftStatus = giftStatus;
	}

	public PrivilegeStatus getPrivilegeStatus() {
		return privilegeStatus;
	}

	public void setPrivilegeStatus(PrivilegeStatus privilegeStatus) {
		this.privilegeStatus = privilegeStatus;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
}
