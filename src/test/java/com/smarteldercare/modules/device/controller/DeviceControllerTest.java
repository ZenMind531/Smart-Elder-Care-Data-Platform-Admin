package com.smarteldercare.modules.device.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.smarteldercare.modules.device.dto.DeviceDTO;
import com.smarteldercare.modules.device.dto.DeviceStatusDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerTest {

    private static final String TEST_ID_CARD = "TEST_DEVICE_ELDERLY";
    private static final String TEST_DEVICE_CODE = "TEST-DEV-BP-001";

    private Long elderlyId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        createTablesIfNecessary();
        cleanTestData();
        elderlyId = createTestElderlyProfile();
    }

    @AfterEach
    void tearDown() {
        cleanTestData();
    }

    @Test
    @DisplayName("create device")
    void shouldCreateDevice() throws Exception {
        mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.deviceCode").value(TEST_DEVICE_CODE))
            .andExpect(jsonPath("$.data.deviceName").value("Electronic BP Meter B2"))
            .andExpect(jsonPath("$.data.elderlyId").value(elderlyId))
            .andExpect(jsonPath("$.data.elderlyName").value("Device Test Elderly"));
    }

    @Test
    @DisplayName("list devices")
    void shouldListDevices() throws Exception {
        DeviceDTO dto = createRequest();
        dto.setStatus("testing");
        Number id = createTestDevice(dto);

        mockMvc.perform(get("/api/devices")
                .param("page", "1")
                .param("size", "10")
                .param("deviceType", "bp_meter")
                .param("status", "testing"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.records", hasSize(1)))
            .andExpect(jsonPath("$.data.records[0].id").value(id))
            .andExpect(jsonPath("$.data.records[0].deviceCode").value(TEST_DEVICE_CODE));
    }

    @Test
    @DisplayName("get device detail")
    void shouldGetDeviceDetail() throws Exception {
        Number id = createTestDevice();

        mockMvc.perform(get("/api/devices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(id))
            .andExpect(jsonPath("$.data.deviceType").value("bp_meter"));
    }

    @Test
    @DisplayName("update device")
    void shouldUpdateDevice() throws Exception {
        Number id = createTestDevice();
        DeviceDTO dto = createRequest();
        dto.setDeviceName("Updated BP Meter");
        dto.setStatus("offline");

        mockMvc.perform(put("/api/devices/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.deviceName").value("Updated BP Meter"))
            .andExpect(jsonPath("$.data.status").value("offline"));
    }

    @Test
    @DisplayName("delete device")
    void shouldDeleteDevice() throws Exception {
        Number id = createTestDevice();

        mockMvc.perform(delete("/api/devices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/devices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("list devices by elderly")
    void shouldListDevicesByElderlyId() throws Exception {
        createTestDevice();

        mockMvc.perform(get("/api/devices/elderly/{elderlyId}", elderlyId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].elderlyId").value(elderlyId))
            .andExpect(jsonPath("$.data[0].deviceCode").value(TEST_DEVICE_CODE));
    }

    @Test
    @DisplayName("bind device")
    void shouldBindDevice() throws Exception {
        Number id = createUnboundDevice();

        mockMvc.perform(patch("/api/devices/{id}/bind/{elderlyId}", id, elderlyId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.elderlyId").value(elderlyId))
            .andExpect(jsonPath("$.data.elderlyName").value("Device Test Elderly"));
    }

    @Test
    @DisplayName("unbind device")
    void shouldUnbindDevice() throws Exception {
        Number id = createTestDevice();

        mockMvc.perform(patch("/api/devices/{id}/unbind", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.elderlyId").doesNotExist());
    }

    @Test
    @DisplayName("update device status")
    void shouldUpdateDeviceStatus() throws Exception {
        Number id = createTestDevice();
        DeviceStatusDTO dto = new DeviceStatusDTO();
        dto.setStatus("offline");

        mockMvc.perform(patch("/api/devices/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.status").value("offline"));
    }

    private Number createTestDevice() throws Exception {
        return createTestDevice(createRequest());
    }

    private Number createTestDevice(DeviceDTO dto) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
    }

    private Number createUnboundDevice() throws Exception {
        DeviceDTO dto = createRequest();
        dto.setElderlyId(null);
        MvcResult result = mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
    }

    private DeviceDTO createRequest() {
        DeviceDTO dto = new DeviceDTO();
        dto.setDeviceCode(TEST_DEVICE_CODE);
        dto.setDeviceName("Electronic BP Meter B2");
        dto.setDeviceType("bp_meter");
        dto.setElderlyId(elderlyId);
        dto.setStatus("normal");
        dto.setRemark("Coronary heart disease auxiliary monitoring");
        return dto;
    }

    private Long createTestElderlyProfile() {
        jdbcTemplate.update("""
            INSERT INTO elderly_profile
              (elderly_name, gender, age, id_card, phone_number, address,
               emergency_contact, emergency_phone, medical_history, allergy_history,
               risk_level, create_time, update_time, deleted)
            VALUES
              (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 0)
            """,
            "Device Test Elderly", "female", 79, TEST_ID_CARD, "13999990004", "Test Area",
            "Test Contact", "13899990004", "Coronary heart disease", "None", "medium");
        return jdbcTemplate.queryForObject(
            "SELECT id FROM elderly_profile WHERE id_card = ? AND deleted = 0",
            Long.class,
            TEST_ID_CARD
        );
    }

    private void createTablesIfNecessary() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS elderly_profile (
              id BIGINT PRIMARY KEY AUTO_INCREMENT,
              elderly_name VARCHAR(50) NOT NULL,
              gender VARCHAR(20) NOT NULL DEFAULT 'unknown',
              age INT NOT NULL,
              id_card VARCHAR(30) DEFAULT NULL,
              phone_number VARCHAR(20) DEFAULT NULL,
              address VARCHAR(255) DEFAULT NULL,
              emergency_contact VARCHAR(50) DEFAULT NULL,
              emergency_phone VARCHAR(20) DEFAULT NULL,
              medical_history VARCHAR(500) DEFAULT NULL,
              allergy_history VARCHAR(500) DEFAULT NULL,
              risk_level VARCHAR(20) NOT NULL DEFAULT 'low',
              create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
              update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
              deleted TINYINT NOT NULL DEFAULT 0,
              UNIQUE KEY uk_elderly_id_card (id_card),
              KEY idx_elderly_name (elderly_name),
              KEY idx_elderly_gender (gender),
              KEY idx_elderly_risk_level (risk_level)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """);
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS device (
              id BIGINT PRIMARY KEY AUTO_INCREMENT,
              device_code VARCHAR(50) NOT NULL UNIQUE,
              device_name VARCHAR(100) NOT NULL,
              device_type VARCHAR(30) NOT NULL,
              elderly_id BIGINT DEFAULT NULL,
              status VARCHAR(20) DEFAULT 'normal',
              remark TEXT DEFAULT NULL,
              create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
              update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
              deleted TINYINT NOT NULL DEFAULT 0,
              KEY idx_device_elderly_id (elderly_id),
              KEY idx_device_type (device_type),
              KEY idx_device_status (status)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """);
    }

    private void cleanTestData() {
        jdbcTemplate.update("DELETE FROM device WHERE device_code LIKE 'TEST-DEV-%'");
        jdbcTemplate.update("DELETE FROM elderly_profile WHERE id_card = ?", TEST_ID_CARD);
    }
}
