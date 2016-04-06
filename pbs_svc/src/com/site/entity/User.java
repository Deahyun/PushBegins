package com.site.entity;

public class User {
	public String id, pwd, info;
}

/*
관련 테이블 구조
create table tb_test
(
		id	varchar(30) primary key,
		pwd varchar(30) not null,
		info varchar(100)
)
*/