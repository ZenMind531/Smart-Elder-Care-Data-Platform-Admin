package com.smarteldercare.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.security.BCryptUtil;
import com.smarteldercare.common.security.JwtUtil;
import com.smarteldercare.common.security.RateLimiter;
import com.smarteldercare.modules.system.dto.RegisterRequest;
import com.smarteldercare.modules.system.entity.Doctor;
import com.smarteldercare.modules.system.entity.Role;
import com.smarteldercare.modules.system.entity.User;
import com.smarteldercare.modules.system.mapper.DoctorMapper;
import com.smarteldercare.modules.system.mapper.RoleMapper;
import com.smarteldercare.modules.system.mapper.UserMapper;
import com.smarteldercare.modules.system.service.UserService;
import com.smarteldercare.modules.system.vo.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements
        UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RateLimiter rateLimiter;

    @Override
    public LoginResult login(String username, String password) {
        String safeUsername = username == null ? "" : username.trim();
        String limitKey = "login:limit:" + safeUsername;

        // 登录前检查是否被锁定
        try {
            rateLimiter.checkLocked(limitKey);
        } catch (RateLimiter.TooManyException e) {
            throw new BusinessException(403, e.getMessage());
        }

        // ① 查用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", safeUsername);
        User user = this.baseMapper.selectOne(wrapper);

        if (user == null) {
            recordLoginFailure(limitKey);
            throw new BusinessException(401, "用户名或密码错误");
        }

        // ② 验密码
        if (!BCryptUtil.matches(password, user.getPassword())) {
            // 兼容旧明文密码：BCrypt 不满足时尝试明文比对
            if (user.getPassword() != null && user.getPassword().equals(password)) {
                // 旧密码明文比对通过 → 自动升级为 BCrypt
                user.setPassword(BCryptUtil.hash(password));
                this.baseMapper.updateById(user);
            } else {
                recordLoginFailure(limitKey);
                throw new BusinessException(401, "用户名或密码错误");
            }
        }

        // ③ 查状态
        if ("pending".equals(user.getStatus())) {
            throw new IllegalArgumentException("账号正在审核中，请等待管理员审核");
        }
        if ("disabled".equals(user.getStatus())) {
            throw new IllegalArgumentException("账号已被禁用");
        }

        // 登录成功 → 清零
        rateLimiter.clear(limitKey);

        // ④ 生成 token
        String token = jwtUtil.generate(user.getId(), user.getUsername());

        // ⑤ 包装返回
        LoginResult result = new LoginResult();
        result.setToken(token);

        LoginResult.UserInfo info = new LoginResult.UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setRealName(user.getRealName());

        Role role = roleMapper.selectById(user.getRoleId());
        info.setRoleName(role != null ? role.getRoleName() : "未知角色");

        result.setUserInfo(info);
        return result;
    }

    private void recordLoginFailure(String limitKey) {
        try {
            rateLimiter.recordFailure(limitKey);
        } catch (RateLimiter.TooManyException e) {
            throw new BusinessException(403, e.getMessage());
        }
    }

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public void register(RegisterRequest request) {
        // 查用户名是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", request.getUsername());
        if (this.baseMapper.selectOne(wrapper) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCryptUtil.hash(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRoleId(request.getRoleId());
        user.setStatus("pending");

        Doctor doctor = new Doctor();
        doctor.setDoctorName(request.getRealName());
        doctor.setGender(request.getGender());
        doctor.setDepartment(request.getDepartment());
        doctor.setTitle(request.getTitle());
        doctor.setStatus("pending");

        this.baseMapper.insert(user);
        doctorMapper.insert(doctor);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.baseMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!BCryptUtil.matches(oldPassword, user.getPassword())) {
            // 兼容旧明文密码
            if (!(user.getPassword() != null && user.getPassword().equals(oldPassword))) {
                throw new IllegalArgumentException("旧密码不正确");
            }
        }
        user.setPassword(BCryptUtil.hash(newPassword));
        this.baseMapper.updateById(user);
    }
}
