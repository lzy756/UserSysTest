-- 初始化数据
INSERT INTO customers (name, phone, email, address, age, gender, created_time, updated_time) VALUES
('张三', '13800138001', 'zhangsan@example.com', '北京市朝阳区建国路1号', 25, 'MALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('李四', '13800138002', 'lisi@example.com', '上海市浦东新区陆家嘴金融中心', 30, 'FEMALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('王五', '13800138003', 'wangwu@example.com', '广州市天河区珠江新城', 28, 'MALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('赵六', '13800138004', 'zhaoliu@example.com', '深圳市南山区科技园', 32, 'FEMALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('钱七', '13800138005', 'qianqi@example.com', '杭州市西湖区文三路', 26, 'MALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入默认管理员用户 (用户名: admin, 密码: admin123 - 使用SHA-256+BCrypt双重加密)
-- 前端使用SHA-256加密后传输，后端对SHA-256哈希值进行BCrypt验证
INSERT INTO users (username, password, created_time, updated_time) VALUES
('admin', '$2a$10$H00e5MuEhwR3/tVHoVqVD.LGsfzuBPuW8gIuKmvDQfsDsUU1aSyxy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);