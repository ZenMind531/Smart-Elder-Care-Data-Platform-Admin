-- 成员 C：老人档案、健康数据、健康预警模块建表 SQL
-- MySQL 8.0，字符集统一使用 utf8mb4

CREATE DATABASE IF NOT EXISTS elderly
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE elderly;

CREATE TABLE IF NOT EXISTS elderly_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  elderly_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
  family_member_id BIGINT DEFAULT NULL COMMENT '绑定家属ID',
  gender VARCHAR(20) NOT NULL DEFAULT 'unknown' COMMENT '性别：male/female/unknown',
  age INT NOT NULL COMMENT '年龄',
  id_card VARCHAR(30) DEFAULT NULL COMMENT '身份证号',
  phone_number VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  address VARCHAR(255) DEFAULT NULL COMMENT '家庭住址',
  emergency_contact VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
  emergency_phone VARCHAR(20) DEFAULT NULL COMMENT '紧急联系人电话',
  medical_history VARCHAR(500) DEFAULT NULL COMMENT '既往病史',
  allergy_history VARCHAR(500) DEFAULT NULL COMMENT '过敏史',
  risk_level VARCHAR(20) NOT NULL DEFAULT 'low' COMMENT '风险等级：low/medium/high',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  UNIQUE KEY uk_elderly_id_card (id_card),
  KEY idx_elderly_name (elderly_name),
  KEY idx_elderly_gender (gender),
  KEY idx_elderly_risk_level (risk_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='老人档案表';

CREATE TABLE IF NOT EXISTS health_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  elderly_id BIGINT NOT NULL COMMENT '老人ID',
  systolic_pressure INT DEFAULT NULL COMMENT '收缩压',
  diastolic_pressure INT DEFAULT NULL COMMENT '舒张压',
  blood_sugar DECIMAL(5,2) DEFAULT NULL COMMENT '血糖',
  heart_rate INT DEFAULT NULL COMMENT '心率',
  temperature DECIMAL(4,1) DEFAULT NULL COMMENT '体温',
  record_time DATETIME NOT NULL COMMENT '记录时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  KEY idx_health_record_elderly_id (elderly_id),
  KEY idx_health_record_time (record_time),
  CONSTRAINT fk_health_record_elderly
    FOREIGN KEY (elderly_id) REFERENCES elderly_profile (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康数据记录表';

CREATE TABLE IF NOT EXISTS health_warning (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  elderly_id BIGINT NOT NULL COMMENT '老人ID',
  health_record_id BIGINT DEFAULT NULL COMMENT '触发预警的健康记录ID',
  retest_record_id BIGINT DEFAULT NULL COMMENT '复测健康记录ID',
  warning_type VARCHAR(50) NOT NULL COMMENT '预警类型：blood_pressure/blood_sugar/heart_rate/temperature',
  warning_level VARCHAR(20) NOT NULL COMMENT '预警等级：low/medium/high',
  warning_content VARCHAR(500) NOT NULL COMMENT '预警内容',
  status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '处理状态：pending/processing/resolved',
  handle_result VARCHAR(500) DEFAULT NULL COMMENT '处理结果',
  warning_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预警时间',
  handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  KEY idx_health_warning_elderly_id (elderly_id),
  KEY idx_health_warning_record_id (health_record_id),
  KEY idx_health_warning_retest_record_id (retest_record_id),
  KEY idx_health_warning_level (warning_level),
  KEY idx_health_warning_status (status),
  KEY idx_health_warning_time (warning_time),
  CONSTRAINT fk_health_warning_elderly
    FOREIGN KEY (elderly_id) REFERENCES elderly_profile (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康预警表';
