CREATE TABLE QQActivityMember (
	id VARCHAR(255) NOT NULL,
	cdKey VARCHAR(255),
	giftStatus VARCHAR(255),
	privilegeStatus VARCHAR(255),
	importGroupNo VARCHAR(255),
	importTime datetime,
	createdAt datetime,
	lastModifiedAt datetime,
	primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QQActivityHistory (
	id VARCHAR(255) NOT NULL,
	cdKey VARCHAR(255),
	aType VARCHAR(255),
	consumeAmt double precision NOT NULL,
	rebateAmt double precision NOT NULL,
	posId VARCHAR(255),
	createdAt datetime,
	lastModifiedAt datetime,
	primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QQWeixinSignIn (
	id VARCHAR(255) NOT NULL,
	weixinNo VARCHAR(255),
	posId VARCHAR(255),
	createdAt datetime,
	lastModifiedAt datetime,
	primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;