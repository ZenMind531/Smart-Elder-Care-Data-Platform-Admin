package com.smarteldercare.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.utils.JwtUtil;
import com.smarteldercare.modules.system.dto.RegisterRequest;
import com.smarteldercare.modules.system.entity.User;
import com.smarteldercare.modules.system.mapper.UserMapper;
import com.smarteldercare.modules.system.service.UserService;
import com.smarteldercare.modules.system.vo.LoginResult;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements
        UserService {
    @Override
    public LoginResult login(String username, String
            password) {
        // ① 查用户
        QueryWrapper<User> wrapper = new
                QueryWrapper<>();
        wrapper.eq("username", username);
        User user = this.baseMapper.selectOne(wrapper);

        if (user == null) {
            throw new
                    IllegalArgumentException("用户名不存在");
        }

        // ② 验密码
        if (!user.getPassword().equals(password)) {
            throw new
                    IllegalArgumentException("密码错误");
        }

        // ③ 查状态
        if ("disabled".equals(user.getStatus())) {
            throw new
                    IllegalArgumentException("账号已被禁用");
        }

        // ④ 生成 token
        String token = JwtUtil.generate(user.getId(),
                user.getUsername());

        // ⑤ 包装返回
        LoginResult result = new LoginResult();
        result.setToken(token);

        LoginResult.UserInfo info = new
                LoginResult.UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setRealName(user.getRealName());
        info.setRoleName("管理员"); //暂时写死，后面查角色表再改

        result.setUserInfo(info);
        return result;
    }
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
        user.setPassword(request.getPassword());  // 实际要加密，先明文
        user.setRealName(request.getRealName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStatus("enabled");
        this.baseMapper.insert(user);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String
            newPassword) {
        User user = this.baseMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("旧密码不正确");
        }
        user.setPassword(newPassword);
        this.baseMapper.updateById(user);
    }


}