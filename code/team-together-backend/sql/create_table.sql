# 数据库初始化
CREATE DATABASE IF NOT EXISTS team_together;

USE team_together;

-- 用户表
create table user
(
    id           bigint auto_increment primary key comment 'id',
    username     varchar(256) null comment '用户昵称',
    userAccount  varchar(256) null comment '账号',
    avatarUrl    varchar(1024) null comment '用户头像',
    gender       tinyint null comment '性别',
    userPassword varchar(512)       not null comment '密码',
    phone        varchar(128) null comment '电话',
    email        varchar(512) null comment '邮箱',
    userStatus   int      default 0 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0 not null comment '是否删除',
    userRole     int      default 0 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planetCode   varchar(512) null comment '星球编号',
    tags         varchar(1024) null comment '标签 json 列表'
) comment '用户';

-- 队伍表
create table team
(
    id          bigint auto_increment primary key comment 'id',
    name        varchar(256)       not null comment '队伍名称',
    description varchar(1024) null comment '描述',
    maxNum      int      default 1 not null comment '最大人数',
    expireTime  datetime null comment '过期时间',
    userId      bigint comment '用户id（队长 id）',
    status      int      default 0 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512) null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0 not null comment '是否删除'
) comment '队伍';

-- 用户队伍关系
create table user_team
(
    id         bigint auto_increment comment 'id' primary key,
    userId     bigint comment '用户id',
    teamId     bigint comment '队伍id',
    joinTime   datetime null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0 not null comment '是否删除',
    UNIQUE KEY uk_userId_teamId (userId, teamId),
    CONSTRAINT fk_user_team_userId
        FOREIGN KEY (userId) REFERENCES user(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_user_team_teamId
        FOREIGN KEY (teamId) REFERENCES team(id)
            ON DELETE CASCADE
) comment '用户队伍关系';

-- 标签表（可以不创建，因为标签字段已经放到了用户表中）
create table tag
(
    id         bigint auto_increment comment 'id'
        primary key,
    tagName    varchar(256) null comment '标签名称',
    userId     bigint null comment '用户 id',
    parentId   bigint null comment '父标签 id',
    isParent   tinyint null comment '0 - 不是, 1 - 父标签',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0 not null comment '是否删除',
    constraint uniIdx_tagName
        unique (tagName)
) comment '标签';

create index idx_userId
    on tag (userId);

-- 模拟数据
-- 用户表数据（包含管理员和普通用户）
INSERT INTO user (username, userAccount, avatarUrl, gender, userPassword, phone, email, userRole, planetCode, tags)
VALUES
    ('管理员', 'admin', 'https://robohash.org/admin', 1, '123456', '13800138000', 'admin@star.com', 1, 'ADM001', '["管理","系统"]'),
    ('陈可辛', 'kexin', 'https://robohash.org/quantum', 0, '123456', '13912345678', 'kexin@gmail.com', 0, 'QXZ202', '["Java","SpringBoot","MySQL"]'),
    ('王浩然', 'haoran', 'https://robohash.org/fairy', 1, '123456', '18600998877', 'haoran@gmail.com', 0, 'DEV666', '["Python","AI"]'),
    ('张一鸣', 'yiming', 'https://robohash.org/prophet', 0, '123456', '13122334455', 'yiming@gmail.com', 0, 'DT2023', '["JavaScript", "HTML", "前端"]'),
    ('赵子轩', 'newbie', 'https://robohash.org/newbie', 1, '123456', '15988997766', 'zixuan@gmail.com', 0, 'NB2024', '["产品", "Apifox"]');

-- 队伍表数据（包含不同状态）
INSERT INTO team (name, description, maxNum, expireTime, userId, status, password)
VALUES
   ('科技教育小组', 'CS 6460 课程小组', 5, DATE_ADD(NOW(), INTERVAL 7 DAY), 2, 0, NULL),
   ('数据库小组', 'CS 6400 课程小组', 3, DATE_ADD(NOW(), INTERVAL 30 DAY), 1, 2, 'qazwsx'),
   ('软件开发小组', 'CS 6310 课程小组', 10, DATE_ADD(NOW(), INTERVAL 60 DAY), 3, 1, NULL);

-- 用户队伍关系数据（自动维护时间戳）
INSERT INTO user_team (userId, teamId, joinTime) VALUES
-- 算法攻坚队成员
(2, 1, NOW()), -- 队长
(3, 1, DATE_ADD(NOW(), INTERVAL 10 MINUTE)),
(4, 1, DATE_ADD(NOW(), INTERVAL 20 MINUTE)),
-- 量子计算研究组
(1, 2, NOW()), -- 队长
(2, 2, DATE_ADD(NOW(), INTERVAL 5 MINUTE)),
-- 新人训练营
(3, 3, NOW()), -- 队长
(5, 3, DATE_ADD(NOW(), INTERVAL 15 MINUTE));