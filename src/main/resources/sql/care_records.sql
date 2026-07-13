CREATE TABLE IF NOT EXISTS care_record (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT  COMMENT '主键',
    elderly_id    BIGINT NOT NULL                     COMMENT '老人ID',
    caregiver     VARCHAR(50)                         COMMENT '护理人员姓名',
    care_type     VARCHAR(30) NOT NULL                COMMENT '护理类型：medication/vital_signs/cleaning/feeding/exercise/other',
    care_content  TEXT NOT NULL                       COMMENT '护理内容描述',
    care_time     DATETIME NOT NULL                   COMMENT '护理执行时间',
    remark        VARCHAR(500)                        COMMENT '备注',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT  DEFAULT 0                  COMMENT '逻辑删除',
    KEY idx_care_elderly (elderly_id),
    KEY idx_care_time (care_time),
    CONSTRAINT fk_care_elderly FOREIGN KEY (elderly_id) REFERENCES elderly_profile (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理记录表';