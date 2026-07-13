package  com.smarteldercare.modules.system.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.modules.system.entity.Doctor;
import com.smarteldercare.modules.system.mapper.DoctorMapper;
import com.smarteldercare.modules.system.service.DoctorService;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper,Doctor>
        implements DoctorService {
};