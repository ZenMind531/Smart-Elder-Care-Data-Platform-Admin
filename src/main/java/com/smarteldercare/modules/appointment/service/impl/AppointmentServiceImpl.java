package com.smarteldercare.modules.appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.common.exception.BusinessException;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.common.result.ResultCode;
import com.smarteldercare.modules.appointment.dto.AppointmentDTO;
import com.smarteldercare.modules.appointment.dto.AppointmentStatusDTO;
import com.smarteldercare.modules.appointment.entity.Appointment;
import com.smarteldercare.modules.appointment.mapper.AppointmentMapper;
import com.smarteldercare.modules.appointment.service.AppointmentService;
import com.smarteldercare.modules.appointment.vo.AppointmentVO;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl
        extends ServiceImpl<AppointmentMapper, Appointment>
        implements AppointmentService {

    private final ElderlyProfileMapper elderlyProfileMapper;

    @Override
    public PageResult<AppointmentVO> listAppointments(Long page, Long size, Long elderlyId, String status, String serviceType) {
        Page<Appointment> p = new Page<>(
                page == null || page < 1 ? 1 : page,
                size == null || size < 1 ? 10 : Math.min(size, 100)
        );

        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(elderlyId != null, Appointment::getElderlyId, elderlyId)
               .eq(status != null && !status.isEmpty(), Appointment::getStatus, status)
               .eq(serviceType != null && !serviceType.isEmpty(), Appointment::getServiceType, serviceType)
               .orderByDesc(Appointment::getAppointmentTime);

        Page<Appointment> result = this.page(p, wrapper);
        List<AppointmentVO> records = result.getRecords().stream()
                .map(this::toVO)
                .toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public AppointmentVO getAppointment(Long id) {
        return toVO(getExistingById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO createAppointment(AppointmentDTO dto) {
        if (elderlyProfileMapper.selectById(dto.getElderlyId()) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(dto, appointment);
        appointment.setStatus("pending");
        save(appointment);
        return toVO(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO updateAppointment(Long id, AppointmentDTO dto) {
        Appointment appointment = getExistingById(id);
        BeanUtils.copyProperties(dto, appointment);
        appointment.setId(id);
        updateById(appointment);
        return toVO(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAppointmentStatus(Long id, AppointmentStatusDTO dto) {
        Appointment appointment = getExistingById(id);
        appointment.setStatus(dto.getStatus());
        if (dto.getCancelReason() != null) {
            appointment.setCancelReason(dto.getCancelReason());
        }
        updateById(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAppointment(Long id) {
        getExistingById(id);
        removeById(id);
    }

    private Appointment getExistingById(Long id) {
        Appointment appointment = getById(id);
        if (appointment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return appointment;
    }

    private AppointmentVO toVO(Appointment appointment) {
        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);
        if (appointment.getElderlyId() != null) {
            com.smarteldercare.modules.elderly.entity.ElderlyProfile e = elderlyProfileMapper.selectById(appointment.getElderlyId());
            if (e != null) vo.setElderlyName(e.getElderlyName());
        }
        return vo;
    }
}
