-- 数据库账号管理
-- 所有人必须使用以下账号之一，禁止直接使用 root
--
-- 账号清单：
--   admin  Admin123!  全部权限
--   reader  Reader123!  只读查询
--   writer  Writer123!  增删改查
--   backup  Backup123!  备份专用

CREATE USER IF NOT EXISTS 'admin'@'localhost' IDENTIFIED BY 'Admin123!';
GRANT ALL PRIVILEGES ON smart_eldercare.* TO 'admin'@'localhost';

CREATE USER IF NOT EXISTS 'reader'@'localhost' IDENTIFIED BY 'Reader123!';
GRANT SELECT ON smart_eldercare.* TO 'reader'@'localhost';

CREATE USER IF NOT EXISTS 'writer'@'localhost' IDENTIFIED BY 'Writer123!';
GRANT SELECT, INSERT, UPDATE, DELETE ON smart_eldercare.* TO 'writer'@'localhost';

CREATE USER IF NOT EXISTS 'backup'@'localhost' IDENTIFIED BY 'Backup123!';
GRANT SELECT, LOCK TABLES, SHOW VIEW ON smart_eldercare.* TO 'backup'@'localhost';
GRANT RELOAD ON *.* TO 'backup'@'localhost';

FLUSH PRIVILEGES;
