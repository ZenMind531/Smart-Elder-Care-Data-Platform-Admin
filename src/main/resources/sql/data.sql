INSERT INTO `role` (`role_name`, `role_code`, `description`)
VALUES ('管理员', 'admin', '系统管理员');
INSERT INTO `role` (`role_name`, `role_code`, `description`)
VALUES ('医生', 'doctor', '医生角色');

INSERT INTO `user` (`username`, `password`, `real_name`,`phone_number`, `role_id`) VALUES ('admin', '123456', '管理员', '13800000000', 1);
INSERT INTO `doctor` (`doctor_name`, `gender`, `phone_number`,`department`, `title`) VALUES ('张医生', 'male', '13800000001', '全科', '主治医师');
INSERT INTO `doctor` (`doctor_name`, `gender`, `phone_number`,`department`, `title`) VALUES ('李医生', 'female', '13800000002', '内科', '主任医师');
