package com.chinarewards.qqgbvpn.main.dao.qqadidas;

import java.util.List;

import com.chinarewards.qq.adidas.domain.ActivityType;
import com.chinarewards.qq.adidas.domain.QQActivityHistory;

public interface QQActivityHistoryDao {
	QQActivityHistory save(QQActivityHistory qqActivityHistory);

	List<QQActivityHistory> findHistoriesByMemberKeyAndType(String memberKey,
			ActivityType type);
}
