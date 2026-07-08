-- 评估报告表
CREATE TABLE IF NOT EXISTS assessment_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    elderly_id BIGINT NOT NULL COMMENT '老人ID',
    report_title VARCHAR(200) NOT NULL COMMENT '报告标题',
    risk_level VARCHAR(20) NOT NULL COMMENT '风险等级: low/medium/high',
    summary TEXT COMMENT '评估摘要',
    suggestion TEXT COMMENT '建议',
    report_time DATETIME COMMENT '评估时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '评估报告表';

-- 重点人群表
CREATE TABLE IF NOT EXISTS key_population (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    elderly_id BIGINT NOT NULL COMMENT '老人ID',
    population_type VARCHAR(30) NOT NULL COMMENT '类型: chronic/disability/solitary/empty_nesters',
    reason TEXT COMMENT '纳入原因',
    follow_status VARCHAR(20) DEFAULT 'pending' COMMENT '跟进状态: pending/completed',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '重点人群表';

-- 设备表
CREATE TABLE IF NOT EXISTS device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_code VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编号',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_type VARCHAR(30) NOT NULL COMMENT '设备类型: watch/bp_meter/glucometer',
    elderly_id BIGINT COMMENT '绑定老人ID',
    status VARCHAR(20) DEFAULT 'normal' COMMENT '状态: normal/abnormal/disabled',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '设备表';
