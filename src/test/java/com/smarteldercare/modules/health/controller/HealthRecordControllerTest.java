package com.smarteldercare.modules.health.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.smarteldercare.modules.health.dto.HealthRecordDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
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
class HealthRecordControllerTest {

    private static final String TEST_ID_CARD = "TEST_HEALTH_RECORD_ELDERLY";

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
    @DisplayName("create health record")
    void shouldCreateHealthRecord() throws Exception {
        mockMvc.perform(post("/api/health-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.elderlyId").value(elderlyId))
            .andExpect(jsonPath("$.data.systolicPressure").value(145))
            .andExpect(jsonPath("$.data.remark").value("Blood pressure is above normal range"));
    }

    @Test
    @DisplayName("create warnings after creating health record")
    void shouldCreateWarningsAfterCreatingHealthRecord() throws Exception {
        HealthRecordDTO dto = createRequest();
        dto.setSystolicPressure(165);
        dto.setDiastolicPressure(101);
        dto.setBloodSugar(new BigDecimal("10.20"));
        dto.setHeartRate(112);
        dto.setTemperature(new BigDecimal("38.2"));

        mockMvc.perform(post("/api/health-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        Integer warningCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM health_warning WHERE elderly_id = ? AND status = 'pending' AND deleted = 0",
            Integer.class,
            elderlyId
        );
        Integer highWarningCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM health_warning WHERE elderly_id = ? AND warning_level = 'high' AND deleted = 0",
            Integer.class,
            elderlyId
        );

        Assertions.assertThat(warningCount).isEqualTo(4);
        Assertions.assertThat(highWarningCount).isEqualTo(4);
        Assertions.assertThat(queryElderlyRiskLevel()).isEqualTo("high");
    }

    @Test
    @DisplayName("list health records")
    void shouldListHealthRecords() throws Exception {
        Integer id = createTestHealthRecord();

        mockMvc.perform(get("/api/health-records")
                .param("page", "1")
                .param("size", "10")
                .param("elderlyId", elderlyId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.records", hasSize(1)))
            .andExpect(jsonPath("$.data.records[0].id").value(id))
            .andExpect(jsonPath("$.data.records[0].elderlyId").value(elderlyId));
    }

    @Test
    @DisplayName("list health record trend")
    void shouldListHealthRecordTrend() throws Exception {
        createTestHealthRecord();
        createOldHealthRecord();

        mockMvc.perform(get("/api/health-records/trend")
                .param("elderlyId", elderlyId.toString())
                .param("days", "7"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].recordTime").value("2026-07-08 09:00:00"))
            .andExpect(jsonPath("$.data[0].systolicPressure").value(145))
            .andExpect(jsonPath("$.data[0].diastolicPressure").value(92))
            .andExpect(jsonPath("$.data[0].bloodSugar").value(7.80))
            .andExpect(jsonPath("$.data[0].heartRate").value(88))
            .andExpect(jsonPath("$.data[0].temperature").value(36.7));
    }

    @Test
    @DisplayName("get health record detail")
    void shouldGetHealthRecordDetail() throws Exception {
        Integer id = createTestHealthRecord();

        mockMvc.perform(get("/api/health-records/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(id))
            .andExpect(jsonPath("$.data.heartRate").value(88));
    }

    @Test
    @DisplayName("delete health record")
    void shouldDeleteHealthRecord() throws Exception {
        Integer id = createTestHealthRecord();

        mockMvc.perform(delete("/api/health-records/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/health-records/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(404));
    }

    private Integer createTestHealthRecord() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/health-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
    }

    private void createOldHealthRecord() {
        jdbcTemplate.update("""
            INSERT INTO health_record
              (elderly_id, systolic_pressure, diastolic_pressure, blood_sugar,
               heart_rate, temperature, record_time, remark, create_time, update_time, deleted)
            VALUES
              (?, ?, ?, ?, ?, ?, DATE_SUB(NOW(), INTERVAL 20 DAY), ?, NOW(), NOW(), 0)
            """,
            elderlyId, 120, 80, new BigDecimal("5.60"), 75, new BigDecimal("36.4"), "Old record");
    }

    private HealthRecordDTO createRequest() {
        HealthRecordDTO dto = new HealthRecordDTO();
        dto.setElderlyId(elderlyId);
        dto.setSystolicPressure(145);
        dto.setDiastolicPressure(92);
        dto.setBloodSugar(new BigDecimal("7.80"));
        dto.setHeartRate(88);
        dto.setTemperature(new BigDecimal("36.7"));
        dto.setRecordTime(LocalDateTime.of(2026, 7, 8, 9, 0, 0));
        dto.setRemark("Blood pressure is above normal range");
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
            "Health Record Test Elderly", "male", 76, TEST_ID_CARD, "13999990002", "Test Area",
            "Test Contact", "13899990002", "Hypertension", "None", "medium");
        return jdbcTemplate.queryForObject(
            "SELECT id FROM elderly_profile WHERE id_card = ? AND deleted = 0",
            Long.class,
            TEST_ID_CARD
        );
    }

    private String queryElderlyRiskLevel() {
        return jdbcTemplate.queryForObject(
            "SELECT risk_level FROM elderly_profile WHERE id = ?",
            String.class,
            elderlyId
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
