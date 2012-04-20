CREATE TABLE QQActivityMember (
	id VARCHAR(255) NOT NULL,
	memberKey VARCHAR(255),
	giftStatus VARCHAR(255),
	privilegeStatus VARCHAR(255),
	sendTime datetime,
	createdAt datetime,
	lastModifiedAt datetime,
	primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;