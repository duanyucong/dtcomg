/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : xuan_chuan

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 25/10/2025 17:29:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父部门id',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 202 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '开发部', 0, '管理员', '0', '0', 'admin', '2025-09-21 13:32:47', '13613180516', '2025-10-24 15:59:16');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `is_show` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否显示在菜单栏中',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 10, 'system', NULL, NULL, 'M', 'Setting', '1', '0', '0', '', NULL, 'admin', '2025-09-23 09:44:39');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'sys/User', 'system:user:list', 'C', 'User', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'sys/Role', 'system:role:list', 'C', 'Key', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (102, '部门管理', 1, 3, 'dept', 'sys/Dept', 'system:dept:list', 'C', 'OfficeBuilding', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (103, '菜单管理', 1, 4, 'menu', 'sys/Menu', 'system:menu:list', 'C', 'Menu', '1', '0', '0', '', NULL, 'admin', '2025-09-23 20:29:27');
INSERT INTO `sys_menu` VALUES (1001, '用户列表', 100, 0, '', NULL, 'system:user:list', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1002, '用户查询', 100, 0, '', NULL, 'system:user:query', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1003, '用户新增', 100, 0, '', NULL, 'system:user:add', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1004, '用户修改', 100, 0, '', NULL, 'system:user:edit', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1005, '用户删除', 100, 0, '', NULL, 'system:user:remove', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1011, '角色列表', 101, 0, '', NULL, 'system:role:list', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1012, '角色查询', 101, 0, '', NULL, 'system:role:query', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1013, '角色新增', 101, 0, '', NULL, 'system:role:add', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1014, '角色修改', 101, 0, '', NULL, 'system:role:edit', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1015, '角色删除', 101, 0, '', NULL, 'system:role:remove', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1021, '部门列表', 102, 0, '', NULL, 'system:dept:list', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1022, '部门查询', 102, 0, '', NULL, 'system:dept:query', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1023, '部门新增', 102, 0, '', NULL, 'system:dept:add', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1024, '部门修改', 102, 0, '', NULL, 'system:dept:edit', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1025, '部门删除', 102, 0, '', NULL, 'system:dept:remove', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1031, '菜单列表', 103, 0, '', NULL, 'system:menu:list', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1032, '菜单查询', 103, 0, '', NULL, 'system:menu:query', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1033, '菜单新增', 103, 0, '', NULL, 'system:menu:add', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1034, '菜单修改', 103, 0, '', NULL, 'system:menu:edit', 'F', '#', '1', '0', '0', '', NULL, '', NULL);
INSERT INTO `sys_menu` VALUES (1035, '菜单删除', 103, 0, '', NULL, 'system:menu:remove', 'F', '#', '1', '0', '0', '', NULL, '', NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '0', '0', '', NULL, '', NULL);

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和部门关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, '0');
INSERT INTO `sys_role_menu` VALUES (1, 100, '0');
INSERT INTO `sys_role_menu` VALUES (1, 101, '0');
INSERT INTO `sys_role_menu` VALUES (1, 102, '0');
INSERT INTO `sys_role_menu` VALUES (1, 103, '0');
INSERT INTO `sys_role_menu` VALUES (1, 301, '0');
INSERT INTO `sys_role_menu` VALUES (1, 302, '0');
INSERT INTO `sys_role_menu` VALUES (1, 303, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1001, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1002, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1003, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1004, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1005, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1011, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1012, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1013, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1014, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1015, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1021, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1022, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1023, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1024, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1025, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1031, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1032, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1033, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1034, '0');
INSERT INTO `sys_role_menu` VALUES (1, 1035, '0');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 100, 'admin', '超级管理员', 'admin@dtcomg.com', '13888888888', '$2a$10$dDtPYU6/ryF.tD1/SlNvyuwHgEprZf/68qtrDlLDhMu1vW/Q4b7uO', '0', '0', 'admin', '2025-09-21 13:32:47', 'admin', '2025-09-21 13:32:47');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, '0');

SET FOREIGN_KEY_CHECKS = 1;
