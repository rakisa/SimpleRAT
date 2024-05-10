create table command
(
    id             varchar(63) not null,
    target         varchar(63) not null comment '要执行命令的目标token',
    receiver       varchar(63) null comment '发送命令的结果接收者',
    create_time    timestamp   not null comment '创建任务的时间',
    execution_time timestamp   null comment '执行任务的时间',
    data           text        not null comment '要执行的任务的描述信息',
    flag           tinyint     null comment '是否执行',
    result         text        null comment '接受任务返回的结果',
    action         int         not null comment '操作类型'
)
    comment '执行命令的表' engine = InnoDB;

create table encrypt_keys
(
    ip      varchar(127) not null comment 'ip地址',
    aes_key varchar(255) not null comment '加密密钥'
)
    comment 'aes加密密钥表' engine = InnoDB;

create table machine
(
    id               varchar(63)  not null comment '机器id',
    os               varchar(31)  not null comment '操作系统类型',
    arch             varchar(15)  not null comment '架构',
    kernel_version   varchar(31)  not null comment '内核版本',
    host_name        varchar(63)  not null comment '机器名',
    mac_address      varchar(63)  not null comment 'mac地址',
    cwd              varchar(255) not null comment '程序当前运行目录',
    remark           text         null comment '备注信息',
    flag             tinyint      not null comment '是否存活',
    current_protocol tinytext     not null comment '当前使用的通信协议',
    token            varchar(255) not null comment '进行交流所需要操作的token'
)
    comment '上线机器信息' engine = InnoDB;

