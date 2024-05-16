/*
 Navicat Premium Data Transfer

 Source Server         : sectest
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 10.10.220.50:33306
 Source Schema         : sec

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 29/01/2024 14:11:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user3
-- ----------------------------
DROP TABLE IF EXISTS `user3`;
CREATE TABLE `user3`  (
  `id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user3
-- ----------------------------
INSERT INTO `user3` VALUES (1, 'test');
INSERT INTO `user3` VALUES (2, 'admin');
INSERT INTO `user3` VALUES (3, '123');
INSERT INTO `user3` VALUES (4, '<script>alert(123)</script>');

-- ----------------------------
-- Table structure for user4
-- ----------------------------
DROP TABLE IF EXISTS `user4`;
CREATE TABLE `user4`  (
  `id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user4
-- ----------------------------
INSERT INTO `user4` VALUES (1, 'test');
INSERT INTO `user4` VALUES (2, 'admin');
INSERT INTO `user4` VALUES (3, '123');
INSERT INTO `user4` VALUES (4, '<script>alert(123)</script>');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'test');
INSERT INTO `users` VALUES (2, 'admin');
INSERT INTO `users` VALUES (3, '123');
INSERT INTO `users` VALUES (4, '<script>alert(123)</script>');

SET FOREIGN_KEY_CHECKS = 1;
