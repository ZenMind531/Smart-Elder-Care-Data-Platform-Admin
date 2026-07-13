-- 管理员初始数据
-- 密码明文存储，后续接入加密后需修改
-- 用法: mysql -u root -p smart_eldercare < admin-data.sql

INSERT IGNORE INTO user (username, password, real_name, phone_number, role_id, status, create_time, update_time) VALUES
('admin1', 'admin123', '管理员一', '13800000001', 1, 'enabled', NOW(), NOW()),
('admin2', 'admin123', '管理员二', '13800000002', 1, 'enabled', NOW(), NOW()),
('admin3', 'admin123', '管理员三', '13800000003', 1, 'enabled', NOW(), NOW());
