package com.smarteldercare.modules.population.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.PageResult;
import com.smarteldercare.modules.population.entity.KeyPopulation;
import com.smarteldercare.modules.population.service.KeyPopulationService;
import com.smarteldercare.modules.elderly.mapper.ElderlyProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/key-populations")
public class KeyPopulationController {

    @Autowired
    private KeyPopulationService keyPopulationService;

    @Autowired
    private ElderlyProfileMapper elderlyProfileMapper;

    private void fillElderlyName(KeyPopulation k) {
        if (k != null && k.getElderlyId() != null) {
            com.smarteldercare.modules.elderly.entity.ElderlyProfile e = elderlyProfileMapper.selectById(k.getElderlyId());
            if (e != null) k.setElderlyName(e.getElderlyName());
        }
    }

    @GetMapping
    public ApiResponse<PageResult<KeyPopulation>> list(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String populationType,
            @RequestParam(required = false) String followStatus) {
        Page<KeyPopulation> p = new Page<>(page, size);
        LambdaQueryWrapper<KeyPopulation> q = new LambdaQueryWrapper<>();
        if (populationType != null) q.eq(KeyPopulation::getPopulationType, populationType);
        if (followStatus != null) q.eq(KeyPopulation::getFollowStatus, followStatus);
        q.orderByDesc(KeyPopulation::getCreateTime);
        keyPopulationService.page(p, q);
        p.getRecords().forEach(this::fillElderlyName);
        return ApiResponse.success(new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<KeyPopulation> detail(@PathVariable Long id) {
        KeyPopulation k = keyPopulationService.getById(id);
        if (k == null) return ApiResponse.error(404, "重点人群记录不存在");
        fillElderlyName(k);
        return ApiResponse.success(k);
    }

    @PostMapping
    public ApiResponse<Void> add(@RequestBody KeyPopulation population) {
        keyPopulationService.save(population);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody KeyPopulation population) {
        if (keyPopulationService.getById(id) == null) return ApiResponse.error(404, "重点人群记录不存在");
        population.setId(id);
        keyPopulationService.updateById(population);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (keyPopulationService.getById(id) == null) return ApiResponse.error(404, "重点人群记录不存在");
        keyPopulationService.removeById(id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/follow-status")
    public ApiResponse<Void> updateFollowStatus(@PathVariable Long id, @RequestBody KeyPopulation population) {
        KeyPopulation k = keyPopulationService.getById(id);
        if (k == null) return ApiResponse.error(404, "重点人群记录不存在");
        k.setFollowStatus(population.getFollowStatus());
        keyPopulationService.updateById(k);
        return ApiResponse.success();
    }
}
