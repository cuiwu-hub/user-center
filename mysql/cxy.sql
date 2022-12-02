DROP TABLE IF EXISTS user;

DROP TABLE user;
create table user
(
    id bigint auto_increment,
    username varchar(256) null comment '用户昵称',
    userAccount varchar(256) null comment '账号',
    userPassword varchar(512) null comment '密码',
    avatarUrl varchar(1024) null comment '用户头像',
    gender tinyint null comment '性别',
    email varchar(512) null comment '邮箱',
    userStatus tinyint default 0 null comment '用户状态',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete tinyint default 0 null comment '是否删除',
    phone varchar(512) null comment '电话',
    constraint user_pk
        primary key (id)
)
    comment '用户';

-- # TRUNCATE TABLE user;
