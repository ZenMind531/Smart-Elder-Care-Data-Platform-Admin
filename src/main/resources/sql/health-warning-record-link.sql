USE elderly;

ALTER TABLE health_warning
  ADD COLUMN health_record_id BIGINT DEFAULT NULL COMMENT '触发预警的健康记录ID' AFTER elderly_id,
  ADD COLUMN retest_record_id BIGINT DEFAULT NULL COMMENT '复测健康记录ID' AFTER health_record_id,
  ADD INDEX idx_health_warning_record_id (health_record_id),
  ADD INDEX idx_health_warning_retest_record_id (retest_record_id);
