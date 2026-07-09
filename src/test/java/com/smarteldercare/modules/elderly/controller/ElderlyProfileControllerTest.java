package com.smarteldercare.modules.elderly.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.smarteldercare.modules.elderly.dto.ElderlyProfileDTO;
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
class ElderlyProfileControllerTest {

    private static final String TEST_ID_CARD = "TEST_ELDERLY_001";

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
    }

    @AfterEach
    void tearDown() {
        cleanTestData();
    }

    @Test
    @DisplayName("create elderly profile")
    void shouldCreateElderlyProfile() throws Exception {
        mockMvc.perform(post("/api/elderly")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.elderlyName").value("Test Elderly"))
            .andExpect(jsonPath("$.data.gender").value("male"))
            .andExpect(jsonPath("$.data.riskLevel").value("medium"));
    }

    @Test
    @DisplayName("list elderly profiles")
    void shouldListElderlyProfiles() throws Exception {
        Integer id = createTestElderlyProfile();

        mockMvc.perform(get("/api/elderly")
                .param("page", "1")
                .param("size", "10")
                .param("keyword", "Test Elderly")
                .param("gender", "male"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.records", hasSize(1)))
            .andExpect(jsonPath("$.data.records[0].id").value(id))
            .andExpect(jsonPath("$.data.records[0].elderlyName").value("Test Elderly"));
    }

    @Test
    @DisplayName("get elderly profile detail")
    void shouldGetElderlyProfileDetail() throws Exception {
        Integer id = createTestElderlyProfile();

        mockMvc.perform(get("/api/elderly/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(id))
            .andExpect(jsonPath("$.data.idCard").value(TEST_ID_CARD));
    }

    @Test
    @DisplayName("get elderly health summary")
    void shouldGetElderlyHealthSummary() throws Exception {
        Integer id = createTestElderlyProfile();
        createSummaryHealthRecords(id);
        createSummaryHealthWarnings(id);

        mockMvc.perform(get("/api/elderly/{id}/health-summary", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.elderlyInfo.id").value(id))
            .andExpect(jsonPath("$.data.elderlyInfo.idCard").value(TEST_ID_CARD))
            .andExpect(jsonPath("$.data.latestHealthRecord.systolicPressure").value(150))
            .andExpect(jsonPath("$.data.latestHealthRecord.recordTime").value("2026-07-08 09:00:00"))
            .andExpect(jsonPath("$.data.recentWarnings", hasSize(3)))
            .andExpect(jsonPath("$.data.recentWarnings[0].warningContent").value("Recent warning 3"))
            .andExpect(jsonPath("$.data.riskLevel").value("medium"))
            .andExpect(jsonPath("$.data.pendingWarningCount").value(3));
    }

    @Test
    @DisplayName("update elderly profile")
    void shouldUpdateElderlyProfile() throws Exception {
        Integer id = createTestElderlyProfile();

        mockMvc.perform(put("/api/elderly/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.elderlyName").value("Updated Test Elderly"))
            .andExpect(jsonPath("$.data.riskLevel").value("high"));
    }

    @Test
    @DisplayName("delete elderly profile")
    void shouldDeleteElderlyProfile() throws Exception {
        Integer id = createTestElderlyProfile();

        mockMvc.perform(delete("/api/elderly/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/elderly/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(404));
    }

    private Integer createTestElderlyProfile() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/elderly")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
    }

    private void createSummaryHealthRecords(Integer elderlyId) {
        jdbcTemplate.update("""
            INSERT INTO health_record
              (elderly_id, systolic_pressure, diastolic_pressure, blood_sugar,
               heart_rate, temperature, record_time, remark, create_time, update_time, deleted)
            VALUES
              (?, 130, 82, 6.20, 78, 36.5, '2026-07-07 09:00:00', 'Old record', NOW(), NOW(), 0),
              (?, 150, 96, 8.10, 90, 37.0, '2026-07-08 09:00:00', 'Latest record', NOW(), NOW(), 0)
            """, elderlyId, elderlyId);
    }

    private void createSummaryHealthWarnings(Integer elderlyId) {
        jdbcTemplate.update("""
            INSERT INTO health_warning
              (elderly_id, warning_type, warning_level, warning_content, status,
               warning_time, create_time, update_time, deleted)
            VALUES
              (?, 'blood_pressure', 'medium', 'Old warning', 'resolved', '2026-07-05 09:00:00', NOW(), NOW(), 0),
              (?, 'blood_sugar', 'medium', 'Recent warning 1', 'pending', '2026-07-06 09:00:00', NOW(), NOW(), 0),
              (?, 'heart_rate', 'medium', 'Recent warning 2', 'processing', '2026-07-07 09:00:00', NOW(), NOW(), 0),
              (?, 'temperature', 'high', 'Recent warning 3', 'pending', '2026-07-08 09:00:00', NOW(), NOW(), 0)
            """, elderlyId, elderlyId, elderlyId, elderlyId);
    }

    private ElderlyProfileDTO createRequest() {
        ElderlyProfileDTO dto = new ElderlyProfileDTO();
        dto.setElderlyName("Test Elderly");
        dto.setGender("male");
        dto.setAge(76);
        dto.setIdCard(TEST_ID_CARD);
        dto.setPhoneNumber("13999990001");
        dto.setAddress("Test Area");
        dto.setEmergencyContact("Test Contact");
        dto.setEmergencyPhone("13899990001");
        dto.setMedicalHistory("Hypertension");
        dto.setAllergyHistory("None");
        dto.setRiskLevel("medium");
        return dto;
    }

    private ElderlyProfileDTO updateRequest() {
        ElderlyProfileDTO dto = createRequest();
        dto.setElderlyName("Updated Test Elderly");
        dto.setAge(77);
        dto.setRiskLevel("high");
        return dto;
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
            CREATE TABLE IF NOT EXISTS health_record (
              id BIGINT PRIMARY KEY AUTO_INCREMENT,
              elderly_id BIGINT NOT NULL,
              systolic_pressure INT DEFAULT NULL,
              diastolic_pressure INT DEFAULT NULL,
              blood_sugar DECIMAL(5,2) DEFAULT NULL,
              heart_rate INT DEFAULT NULL,
              temperature DECIMAL(4,1) DEFAULT NULL,
              record_time DATETIME NOT NULL,
              remark VARCHAR(500) DEFAULT NULL,
              create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
              update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
              deleted TINYINT NOT NULL DEFAULT 0,
              KEY idx_health_record_elderly_id (elderly_id),
              KEY idx_health_record_time (record_time)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """);
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS health_warning (
              id BIGINT PRIMARY KEY AUTO_INCREMENT,
              elderly_id BIGINT NOT NULL,
              warning_type VARCHAR(50) NOT NULL,
              warning_level VARCHAR(20) NOT NULL,
              warning_content VARCHAR(500) NOT NULL,
              status VARCHAR(20) NOT NULL DEFAULT 'pending',
              handle_result VARCHAR(500) DEFAULT NULL,
              warning_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
              handle_time DATETIME DEFAULT NULL,
              create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
              update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
              deleted TINYINT NOT NULL DEFAULT 0,
              KEY idx_health_warning_elderly_id (elderly_id),
              KEY idx_health_warning_level (warning_level),
              KEY idx_health_warning_status (status),
              KEY idx_health_warning_time (warning_time)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """);
    }

    private void cleanTestData() {
        jdbcTemplate.update("""
            DELETE hw FROM health_warning hw
            INNER JOIN elderly_profile ep ON hw.elderly_id = ep.id
            WHERE ep.id_card = ?
            """, TEST_ID_CARD);
        jdbcTemplate.update("""
            DELETE hr FROM health_record hr
            INNER JOIN elderly_profile ep ON hr.elderly_id = ep.id
            WHERE ep.id_card = ?
            """, TEST_ID_CARD);
        jdbcTemplate.update("DELETE FROM elderly_profile WHERE id_card = ?", TEST_ID_CARD);
    }
}
