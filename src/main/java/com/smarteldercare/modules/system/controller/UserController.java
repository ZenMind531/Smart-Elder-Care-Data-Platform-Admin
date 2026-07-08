package com.smarteldercare.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
import com.smarteldercare.modules.system.entity.User;
import
        com.smarteldercare.modules.system.service.UserService;
import
        org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ① 用户列表（分页 + 搜索）
    @GetMapping
    public ApiResponse<PageResult<User>> list(
            @RequestParam(defaultValue = "1") Integer
                    page,
            @RequestParam(defaultValue = "10") Integer
                    size,
            @RequestParam(required = false) String
                    keyword,
            @RequestParam(required = false) String
                    status) {

        QueryWrapper<User> wrapper = new
                QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("username",
                    keyword).or().like("real_name", keyword);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");

        Page<User> dbPage = new Page<>(page, size);
        Page<User> resultPage = userService.page(dbPage, wrapper);

        PageResult<User> pageResult = new PageResult<>(
                resultPage.getRecords(),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize());

        return ApiResponse.success(pageResult);
    }

    // ② 用户详情
    @GetMapping("/{id}")
    public ApiResponse<User> detail(@PathVariable Long
                                            id) {
        User user = userService.getById(id);
        if (user == null) {
            return
                    ApiResponse.error(ResultCode.NOT_FOUND.getCode(),
                            "用户不存在");
        }
        return ApiResponse.success(user);
    }

    // ③ 新增用户
    @PostMapping
    public ApiResponse<?> create(@RequestBody User user)
    {
        userService.save(user);
        return ApiResponse.success();
    }

    // ④ 修改用户
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id,
                                 @RequestBody User user) {
        user.setId(id);
        userService.updateById(user);
        return ApiResponse.success();
    }

    // ⑤ 删除用户
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        userService.removeById(id);
        return ApiResponse.success();
    }

    // ⑥ 启用/禁用用户
    @PatchMapping("/{id}/status")
    public ApiResponse<?> toggleStatus(@PathVariable Long
                                               id, @RequestBody Map<String, String> body) {
        User user = userService.getById(id);
        if (user == null) {
            return
                    ApiResponse.error(ResultCode.NOT_FOUND.getCode(),
                            "用户不存在");
        }
        user.setStatus(body.get("status"));
        userService.updateById(user);
        return ApiResponse.success();
    }
}
