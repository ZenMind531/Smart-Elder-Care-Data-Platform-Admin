package com.smarteldercare.modules.population.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smarteldercare.modules.population.entity.KeyPopulation;
import com.smarteldercare.modules.population.mapper.KeyPopulationMapper;
import com.smarteldercare.modules.population.service.KeyPopulationService;
import org.springframework.stereotype.Service;

@Service
public class KeyPopulationServiceImpl extends ServiceImpl<KeyPopulationMapper, KeyPopulation> implements KeyPopulationService {
}
