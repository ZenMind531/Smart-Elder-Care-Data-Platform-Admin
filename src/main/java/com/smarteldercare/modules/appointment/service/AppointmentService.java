package com.smarteldercare.modules.appointment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.appointment.dto.AppointmentDTO;
import com.smarteldercare.modules.appointment.dto.AppointmentStatusDTO;
import com.smarteldercare.modules.appointment.entity.Appointment;
import com.smarteldercare.modules.appointment.vo.AppointmentVO;

public interface AppointmentService extends IService<Appointment> {

    PageResult<AppointmentVO> listAppointments(Long page, Long size, Long elderlyId, String status, String serviceType);

    AppointmentVO getAppointment(Long id);

    AppointmentVO createAppointment(AppointmentDTO dto);

    AppointmentVO updateAppointment(Long id, AppointmentDTO dto);

    void updateAppointmentStatus(Long id, AppointmentStatusDTO dto);

    void deleteAppointment(Long id);
}
