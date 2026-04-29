/*
Navicat MySQL Data Transfer

Source Server         : SEVER 3305
Source Server Version : 50620
Source Host           : localhost:3305
Source Database       : chat_application

Target Server Type    : MYSQL
Target Server Version : 50620
File Encoding         : 65001

Date: 2021-07-29 20:23:48
*/

SET FOREIGN_KEY_CHECKS=0;

-- Ensure database exists (helps on fresh installs)
CREATE DATABASE IF NOT EXISTS `chat_application` DEFAULT CHARACTER SET utf8;
USE `chat_application`;

-- ----------------------------
-- Table structure for files
-- ----------------------------
DROP TABLE IF EXISTS `files`;
CREATE TABLE `files` (
  `FileID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `FileExtension` varchar(255) DEFAULT NULL,
  `BlurHash` varchar(255) DEFAULT NULL,
  `Width` int(11) DEFAULT NULL,
  `Height` int(11) DEFAULT NULL,
  `FileSize` bigint(20) DEFAULT NULL,
  `Status` char(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`FileID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of files
-- ----------------------------

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `MessageID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `FromUserID` int(10) unsigned NOT NULL,
  `ToUserID` int(10) unsigned NOT NULL,
  `MessageType` int(11) NOT NULL,
  `Text` text,
  `FileID` int(10) unsigned DEFAULT NULL,
  `Status` tinyint(4) NOT NULL DEFAULT '0',
  `ReplyToMessageID` int(10) unsigned DEFAULT NULL,
  `ReplyUserName` varchar(255) DEFAULT NULL,
  `ReplyText` text,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MessageID`),
  KEY `idx_messages_users` (`FromUserID`,`ToUserID`,`MessageID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `UserID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `UserName` varchar(255) DEFAULT NULL,
  `Password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('30', 'taher', '123');
INSERT INTO `user` VALUES ('32', 'arafat', '123');
INSERT INTO `user` VALUES ('33', 'mihad', '123');

-- ----------------------------
-- Table structure for user_account
-- ----------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
  `UserID` int(10) unsigned NOT NULL,
  `UserName` varchar(255) DEFAULT NULL,
  `Gender` char(1) NOT NULL DEFAULT '',
  `Image` longblob,
  `ImageString` varchar(255) DEFAULT '',
  `Status` char(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`UserID`),
  CONSTRAINT `user_account_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `user` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_account
-- ----------------------------
INSERT INTO `user_account` VALUES ('30', 'taher', '', null, '', '1');
INSERT INTO `user_account` VALUES ('32', 'arafat', '', null, '', '1');
INSERT INTO `user_account` VALUES ('33', 'mihad', '', null, '', '1');

-- ----------------------------
-- Table structure for group_chat
-- ----------------------------
DROP TABLE IF EXISTS `group_chat`;
CREATE TABLE `group_chat` (
  `GroupID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `GroupName` varchar(255) DEFAULT NULL,
  `AdminID` int(10) unsigned NOT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`GroupID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for group_chat_member
-- ----------------------------
DROP TABLE IF EXISTS `group_chat_member`;
CREATE TABLE `group_chat_member` (
  `GroupID` int(10) unsigned NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`GroupID`,`UserID`),
  CONSTRAINT `group_chat_member_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `group_chat` (`GroupID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_chat_member_ibfk_2` FOREIGN KEY (`UserID`) REFERENCES `user` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
