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
 * It record the history of qq activity. It is about who got a gift or who got a
 * privilege.
 * 
 * @author yanxin
 * @since 0.3.0
 */
@Entity
public class QQActivityHistory {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/**
	 * It flag a member.
	 */
	private String memberKey;

	/**
	 * The type of this activity.
	 */
	@Column(name = "atype")
	@Enumerated(EnumType.STRING)
	private ActivityType aType;

	/**
	 * Consume amount.
	 */
	private double consumeAmt;

	/**
	 * Rebate amount.
	 */
	private double rebateAmt;

	/**
	 * Pos id
	 */
	private String posId;

	private Date createdAt;

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

	public ActivityType getAType() {
		return aType;
	}

	public void setAType(ActivityType aType) {
		this.aType = aType;
	}

	public double getConsumeAmt() {
		return consumeAmt;
	}

	public void setConsumeAmt(double consumeAmt) {
		this.consumeAmt = consumeAmt;
	}

	public double getRebateAmt() {
		return rebateAmt;
	}

	public void setRebateAmt(double rebateAmt) {
		this.rebateAmt = rebateAmt;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
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
