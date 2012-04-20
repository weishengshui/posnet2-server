package com.chinarewards.qqgbvpn.main.qqadidas.vo;

import com.chinarewards.qq.adidas.domain.QQActivityHistory;

public class ObtainPrivilegeResult {

	QQActivityHistory historyThisTime;
	boolean existHistoryLastTime = false;
	QQActivityHistory historyLastTime;

	public QQActivityHistory getHistoryThisTime() {
		return historyThisTime;
	}

	public void setHistoryThisTime(QQActivityHistory historyThisTime) {
		this.historyThisTime = historyThisTime;
	}

	public boolean isExistHistoryLastTime() {
		return existHistoryLastTime;
	}

	public void setExistHistoryLastTime(boolean existHistoryLastTime) {
		this.existHistoryLastTime = existHistoryLastTime;
	}

	public QQActivityHistory getHistoryLastTime() {
		return historyLastTime;
	}

	public void setHistoryLastTime(QQActivityHistory historyLastTime) {
		this.historyLastTime = historyLastTime;
	}
}
