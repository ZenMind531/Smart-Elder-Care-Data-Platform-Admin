package com.smarteldercare.modules.system.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.modules.system.entity.Role;
import
        com.smarteldercare.modules.system.service.RoleService;
import
        org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // 查列表
    @GetMapping
    public ApiResponse<List<Role>> list() {
        List<Role> roles = roleService.list();
        return ApiResponse.success(roles);
    }

    // 新增
    @PostMapping
    public ApiResponse<?> create(@RequestBody Role role)
    {
        roleService.save(role);
        return ApiResponse.success();
    }

    // 修改
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id,
                                 @RequestBody Role role) {
        role.setId(id);
        roleService.updateById(role);
        return ApiResponse.success();
    }

    // 删除
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return ApiResponse.success();
    }
}
