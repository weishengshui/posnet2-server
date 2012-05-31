CREATE TABLE PingTest (
	id VARCHAR(255) NOT NULL,
	descript VARCHAR(255),
	primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO PingTest (id, descript) values ('1', 'Notice: this table can and only can have one record!');