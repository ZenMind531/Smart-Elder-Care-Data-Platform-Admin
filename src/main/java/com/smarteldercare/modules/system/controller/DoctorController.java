package com.smarteldercare.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
import com.smarteldercare.modules.system.entity.Doctor;
import
        com.smarteldercare.modules.system.service.DoctorService;
import
        org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    // ① 医生列表（分页 + 搜索）
    @GetMapping
    public ApiResponse<PageResult<Doctor>> list(
            @RequestParam(defaultValue = "1") Integer
                    page,
            @RequestParam(defaultValue = "10") Integer
                    size,
            @RequestParam(required = false) String
                    keyword,
            @RequestParam(required = false) String
                    status) {

        QueryWrapper<Doctor> wrapper = new
                QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("doctor_name",
                    keyword).or().like("department", keyword);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");

        Page<Doctor> dbPage = new Page<>(page, size);
        Page<Doctor> resultPage =
                doctorService.page(dbPage, wrapper);

        PageResult<Doctor> pageResult = new PageResult<>(
                resultPage.getRecords(),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize());

        return ApiResponse.success(pageResult);
    }

    // ② 医生详情
    @GetMapping("/{id}")
    public ApiResponse<Doctor> detail(@PathVariable Long
                                              id) {
        Doctor doctor = doctorService.getById(id);
        if (doctor == null) {
            return
                    ApiResponse.error(ResultCode.NOT_FOUND.getCode(),
                            "医生不存在");
        }
        return ApiResponse.success(doctor);
    }

    // ③ 新增医生
    @PostMapping
    public ApiResponse<?> create(@RequestBody Doctor
                                         doctor) {
        doctorService.save(doctor);
        return ApiResponse.success();
    }

    // ④ 修改医生

    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @RequestBody Doctor
            doctor) {
        if (doctorService.getById(id) == null) {
            return ApiResponse.error(404, "医生不存在");
        }
        doctor.setId(id);
        doctorService.updateById(doctor);
        return ApiResponse.success();
    }
//ccccccc

    // ⑤ 删除医生
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        if (doctorService.getById(id) == null) {
            return ApiResponse.error(404, "医生不存在");
        }
        doctorService.removeById(id);
        return ApiResponse.success();
    }
}
