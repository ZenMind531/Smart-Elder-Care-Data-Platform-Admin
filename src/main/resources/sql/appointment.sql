CREATE TABLE IF NOT EXISTS appointment (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT  COMMENT '主键',
    elderly_id       BIGINT NOT NULL                     COMMENT '老人ID',
    service_type     VARCHAR(30) NOT NULL                COMMENT '服务类型：health_check/home_care/rehabilitation/consultation/other',
    appointment_time DATETIME NOT NULL                   COMMENT '预约时间',
    doctor_name      VARCHAR(50)                         COMMENT '服务人员姓名',
    status           VARCHAR(20) DEFAULT 'pending'       COMMENT '状态：pending/confirmed/completed/cancelled',
    description      VARCHAR(500)                        COMMENT '预约描述',
    cancel_reason    VARCHAR(200)                        COMMENT '取消原因',
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    update_time      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted          TINYINT  DEFAULT 0                  COMMENT '逻辑删除',
    KEY idx_appt_elderly (elderly_id),
    KEY idx_appt_status (status),
    KEY idx_appt_time (appointment_time),
    CONSTRAINT fk_appt_elderly FOREIGN KEY (elderly_id) REFERENCES elderly_profile (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务预约表';