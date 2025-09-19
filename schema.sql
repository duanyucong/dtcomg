-- ----------------------------
-- 1. Department table
-- ----------------------------
CREATE TABLE sys_dept (
  dept_id       bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '部门id',
  parent_id     bigint(20)      DEFAULT 0                  COMMENT '父部门id',
  dept_name     varchar(30)     DEFAULT ''                 COMMENT '部门名称',
  order_num     int(4)          DEFAULT 0                  COMMENT '显示顺序',
  leader        varchar(20)     DEFAULT NULL               COMMENT '负责人',
  status        char(1)         DEFAULT '0'                COMMENT '部门状态（0正常 1停用）',
  del_flag      char(1)         DEFAULT '0'                COMMENT '删除标志（0代表存在 2代表删除）',
  create_by     varchar(64)     DEFAULT ''                 COMMENT '创建者',
  create_time   datetime                                   COMMENT '创建时间',
  update_by     varchar(64)     DEFAULT ''                 COMMENT '更新者',
  update_time   datetime                                   COMMENT '更新时间',
  PRIMARY KEY (dept_id)
) ENGINE=InnoDB AUTO_INCREMENT=200 COMMENT='部门表';

-- ----------------------------
-- 2. User table
-- ----------------------------
CREATE TABLE sys_user (
  user_id       bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '用户ID',
  dept_id       bigint(20)      DEFAULT NULL               COMMENT '部门ID',
  user_name     varchar(30)     NOT NULL                   COMMENT '用户账号',
  nick_name     varchar(30)     NOT NULL                   COMMENT '用户昵称',
  email         varchar(50)     DEFAULT ''                 COMMENT '用户邮箱',
  phone_number  varchar(11)     DEFAULT ''                 COMMENT '手机号码',
  password      varchar(100)    DEFAULT ''                 COMMENT '密码',
  status        char(1)         DEFAULT '0'                COMMENT '帐号状态（0正常 1停用）',
  del_flag      char(1)         DEFAULT '0'                COMMENT '删除标志（0代表存在 2代表删除）',
  create_by     varchar(64)     DEFAULT ''                 COMMENT '创建者',
  create_time   datetime                                   COMMENT '创建时间',
  update_by     varchar(64)     DEFAULT ''                 COMMENT '更新者',
  update_time   datetime                                   COMMENT '更新时间',
  PRIMARY KEY (user_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='用户信息表';

-- ----------------------------
-- 3. Role table
-- ----------------------------
CREATE TABLE sys_role (
  role_id       bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '角色ID',
  role_name     varchar(30)     NOT NULL                   COMMENT '角色名称',
  role_key      varchar(100)    NOT NULL                   COMMENT '角色权限字符串',
  role_sort     int(4)          NOT NULL                   COMMENT '显示顺序',
  status        char(1)         NOT NULL                   COMMENT '角色状态（0正常 1停用）',
  del_flag      char(1)         DEFAULT '0'                COMMENT '删除标志（0代表存在 2代表删除）',
  create_by     varchar(64)     DEFAULT ''                 COMMENT '创建者',
  create_time   datetime                                   COMMENT '创建时间',
  update_by     varchar(64)     DEFAULT ''                 COMMENT '更新者',
  update_time   datetime                                   COMMENT '更新时间',
  PRIMARY KEY (role_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='角色信息表';

-- ----------------------------
-- 4. Menu table
-- ----------------------------
CREATE TABLE sys_menu (
  menu_id       bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '菜单ID',
  menu_name     varchar(50)     NOT NULL                   COMMENT '菜单名称',
  parent_id     bigint(20)      DEFAULT 0                  COMMENT '父菜单ID',
  order_num     int(4)          DEFAULT 0                  COMMENT '显示顺序',
  path          varchar(200)    DEFAULT ''                 COMMENT '路由地址',
  component     varchar(255)    DEFAULT NULL               COMMENT '组件路径',
  perms         varchar(100)    DEFAULT NULL               COMMENT '权限标识',
  menu_type     char(1)         DEFAULT ''                 COMMENT '菜单类型（M目录 C菜单 F按钮）',
  icon          varchar(100)    DEFAULT '#'                COMMENT '菜单图标',
  status        char(1)         DEFAULT '0'                COMMENT '菜单状态（0正常 1停用）',
  del_flag      char(1)         DEFAULT '0'                COMMENT '删除标志（0代表存在 2代表删除）',
  create_by     varchar(64)     DEFAULT ''                 COMMENT '创建者',
  create_time   datetime                                   COMMENT '创建时间',
  update_by     varchar(64)     DEFAULT ''                 COMMENT '更新者',
  update_time   datetime                                   COMMENT '更新时间',
  PRIMARY KEY (menu_id)
) ENGINE=InnoDB AUTO_INCREMENT=2000 COMMENT='菜单权限表';

-- ----------------------------
-- 5. User-Role association table
-- ----------------------------
CREATE TABLE sys_user_role (
  user_id       bigint(20)      NOT NULL                   COMMENT '用户ID',
  role_id       bigint(20)      NOT NULL                   COMMENT '角色ID',
  PRIMARY KEY(user_id, role_id)
) ENGINE=InnoDB COMMENT='用户和角色关联表';

-- ----------------------------
-- 6. Role-Menu association table
-- ----------------------------
CREATE TABLE sys_role_menu (
  role_id       bigint(20)      NOT NULL                   COMMENT '角色ID',
  menu_id       bigint(20)      NOT NULL                   COMMENT '菜单ID',
  PRIMARY KEY(role_id, menu_id)
) ENGINE=InnoDB COMMENT='角色和菜单关联表';

-- ----------------------------
-- 7. Role-Department association table
-- ----------------------------
CREATE TABLE sys_role_dept (
  role_id       bigint(20)      NOT NULL                   COMMENT '角色ID',
  dept_id       bigint(20)      NOT NULL                   COMMENT '部门ID',
  PRIMARY KEY(role_id, dept_id)
) ENGINE=InnoDB COMMENT='角色和部门关联表';
