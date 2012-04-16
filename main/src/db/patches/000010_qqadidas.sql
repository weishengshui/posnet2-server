CREATE TABLE QQActivityHistory (
	id VARCHAR(255) NOT NULL,
	memberKey VARCHAR(255),
	aType VARCHAR(255),
	consumeAmt double precision NOT NULL,
	rebateAmt double precision NOT NULL,
	posId VARCHAR(255),
	createdAt datetime,
	lastModifiedAt datetime,
	primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;