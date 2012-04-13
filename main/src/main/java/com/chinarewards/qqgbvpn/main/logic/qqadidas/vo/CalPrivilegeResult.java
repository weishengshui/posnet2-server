package com.chinarewards.qqgbvpn.main.logic.qqadidas.vo;

import com.chinarewards.qq.adidas.domain.PrivilegeStatus;

public class CalPrivilegeResult {

	private double rebateAmt;
	private PrivilegeStatus nextPrivilegeStatus;

	public CalPrivilegeResult(double rebateAmt,
			PrivilegeStatus nextPrivilegeStatus) {
		this.rebateAmt = rebateAmt;
		this.nextPrivilegeStatus = nextPrivilegeStatus;
	}

	public double getRebateAmt() {
		return rebateAmt;
	}

	public PrivilegeStatus getNextPrivilegeStatus() {
		return nextPrivilegeStatus;
	}
}
