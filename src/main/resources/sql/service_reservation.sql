CREATE TABLE IF NOT EXISTS service_reservation (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   family_member_id BIGINT NOT NULL COMMENT '预约家属ID',
                                                   elderly_id BIGINT NOT NULL COMMENT '老人ID',
                                                   service_type VARCHAR(50) NOT NULL COMMENT '服务类型：问诊/体检/护理/心理',
    service_date DATE NOT NULL COMMENT '预约日期',
    service_time VARCHAR(20) DEFAULT '上午' COMMENT '上午/下午',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT 'pending/confirmed/completed/cancelled',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务预约表';