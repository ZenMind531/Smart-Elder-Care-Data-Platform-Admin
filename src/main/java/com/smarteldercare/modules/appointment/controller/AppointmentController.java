package com.smarteldercare.modules.appointment.controller;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.appointment.dto.AppointmentDTO;
import com.smarteldercare.modules.appointment.dto.AppointmentStatusDTO;
import com.smarteldercare.modules.appointment.service.AppointmentService;
import com.smarteldercare.modules.appointment.vo.AppointmentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ApiResponse<PageResult<AppointmentVO>> list(
            @RequestParam(required = false, defaultValue = "1") Long page,
            @RequestParam(required = false, defaultValue = "10") Long size,
            @RequestParam(required = false) Long elderlyId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String serviceType
    ) {
        return ApiResponse.success(appointmentService.listAppointments(page, size, elderlyId, status, serviceType));
    }

    @GetMapping("/{id}")
    public ApiResponse<AppointmentVO> detail(@PathVariable Long id) {
        return ApiResponse.success(appointmentService.getAppointment(id));
    }

    @PostMapping
    public ApiResponse<AppointmentVO> create(@Valid @RequestBody AppointmentDTO dto) {
        return ApiResponse.success(appointmentService.createAppointment(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<AppointmentVO> update(@PathVariable Long id,
                                             @Valid @RequestBody AppointmentDTO dto) {
        return ApiResponse.success(appointmentService.updateAppointment(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id,
                                          @Valid @RequestBody AppointmentStatusDTO dto) {
        appointmentService.updateAppointmentStatus(id, dto);
        return ApiResponse.success();
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ApiResponse.success();
    }
}
