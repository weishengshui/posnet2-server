package com.chinarewards.qqgbvpn.main.qqadidas.vo;

import java.util.Date;

public class ObtainPrvilegePrintModel {

	private String memberKey;
	private double consumeAmt;
	private double rebateAmt;
	private boolean existLastTimeConsume;
	private Date lastConsumeDate;
	private double lastRebateAmt;

	public String getMemberKey() {
		return memberKey;
	}

	public void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
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

	public boolean isExistLastTimeConsume() {
		return existLastTimeConsume;
	}

	public void setExistLastTimeConsume(boolean existLastTimeConsume) {
		this.existLastTimeConsume = existLastTimeConsume;
	}

	public Date getLastConsumeDate() {
		return lastConsumeDate;
	}

	public void setLastConsumeDate(Date lastConsumeDate) {
		this.lastConsumeDate = lastConsumeDate;
	}

	public double getLastRebateAmt() {
		return lastRebateAmt;
	}

	public void setLastRebateAmt(double lastRebateAmt) {
		this.lastRebateAmt = lastRebateAmt;
	}
}
