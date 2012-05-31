CREATE TABLE QQWeixinSignIn (
	id VARCHAR(255) NOT NULL,
	weixinNo VARCHAR(255),
	posId VARCHAR(255),
	createdAt datetime,
	lastModifiedAt datetime,
	primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;