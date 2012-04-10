package com.chinarewards.qq.adidas.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * Defines a QQ member due to <code>CKDEY</code>. Every member has two kinds of
 * status: gift status and privilege status.
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
	private String cdKey;

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
	 * Importing group number, eg..UUID
	 */
	private String importGroupNo;

	/**
	 * Importing time.
	 */
	private Date importTime;

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

	public String getCdKey() {
		return cdKey;
	}

	public void setCdKey(String cdKey) {
		this.cdKey = cdKey;
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

	public String getImportGroupNo() {
		return importGroupNo;
	}

	public void setImportGroupNo(String importGroupNo) {
		this.importGroupNo = importGroupNo;
	}

	public Date getImportTime() {
		return importTime;
	}

	public void setImportTime(Date importTime) {
		this.importTime = importTime;
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
