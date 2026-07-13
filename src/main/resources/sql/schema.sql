DROP TABLE IF EXISTS `doctor`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                        `role_name` VARCHAR(50) NOT NULL,
                        `role_code` VARCHAR(50) NOT NULL UNIQUE,
                        `description` VARCHAR(255),
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE
  CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                        `username` VARCHAR(50) NOT NULL UNIQUE,
                        `password` VARCHAR(255) NOT NULL,
                        `real_name` VARCHAR(50),
                        `phone_number` VARCHAR(20),
                        `role_id` BIGINT,
                        `status` VARCHAR(20) DEFAULT 'enabled',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE
  CURRENT_TIMESTAMP,
                        `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `doctor` (
                          `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                          `doctor_name` VARCHAR(50) NOT NULL,
                          `gender` VARCHAR(10),
                          `phone_number` VARCHAR(20),
                          `department` VARCHAR(100),
                          `title` VARCHAR(50),
                          `status` VARCHAR(20) DEFAULT 'enabled',
                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE
  CURRENT_TIMESTAMP,
                          `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

