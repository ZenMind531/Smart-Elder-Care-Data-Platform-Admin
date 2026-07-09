package com.smarteldercare.modules.health.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.smarteldercare.modules.health.dto.HealthWarningDTO;
import com.smarteldercare.modules.health.dto.HealthWarningStatusDTO;
import java.time.LocalDateTime;
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
class HealthWarningControllerTest {

    private static final String TEST_ID_CARD = "TEST_HEALTH_WARNING_ELDERLY";

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
    @DisplayName("create health warning")
    void shouldCreateHealthWarning() throws Exception {
        mockMvc.perform(post("/api/health-warnings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.elderlyId").value(elderlyId))
            .andExpect(jsonPath("$.data.elderlyName").value("Health Warning Test Elderly"))
            .andExpect(jsonPath("$.data.warningType").value("blood_pressure"))
            .andExpect(jsonPath("$.data.warningLevel").value("high"))
            .andExpect(jsonPath("$.data.status").value("pending"));
    }

    @Test
    @DisplayName("list health warnings")
    void shouldListHealthWarnings() throws Exception {
        Number id = createTestHealthWarning();

        mockMvc.perform(get("/api/health-warnings")
                .param("page", "1")
                .param("size", "10")
                .param("elderlyId", elderlyId.toString())
                .param("warningLevel", "high")
                .param("status", "pending"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.records", hasSize(1)))
            .andExpect(jsonPath("$.data.records[0].id").value(id))
            .andExpect(jsonPath("$.data.records[0].elderlyId").value(elderlyId))
            .andExpect(jsonPath("$.data.records[0].warningLevel").value("high"));
    }

    @Test
    @DisplayName("get health warning detail")
    void shouldGetHealthWarningDetail() throws Exception {
        Number id = createTestHealthWarning();

        mockMvc.perform(get("/api/health-warnings/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(id))
            .andExpect(jsonPath("$.data.warningContent").value("Systolic pressure is above normal range"));
    }

    @Test
    @DisplayName("update health warning status")
    void shouldUpdateHealthWarningStatus() throws Exception {
        Number id = createTestHealthWarning();
        HealthWarningStatusDTO dto = new HealthWarningStatusDTO();
        dto.setStatus("resolved");
        dto.setHandleResult("Reviewed and contacted family");

        mockMvc.perform(patch("/api/health-warnings/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.status").value("resolved"))
            .andExpect(jsonPath("$.data.handleResult").value("Reviewed and contacted family"))
            .andExpect(jsonPath("$.data.handleTime").isNotEmpty());

        org.assertj.core.api.Assertions.assertThat(queryElderlyRiskLevel()).isEqualTo("low");
    }

    @Test
    @DisplayName("delete health warning")
    void shouldDeleteHealthWarning() throws Exception {
        Number id = createTestHealthWarning();

        mockMvc.perform(delete("/api/health-warnings/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/health-warnings/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(404));
    }

    private Number createTestHealthWarning() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/health-warnings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
    }

    private HealthWarningDTO createRequest() {
        HealthWarningDTO dto = new HealthWarningDTO();
        dto.setElderlyId(elderlyId);
        dto.setWarningType("blood_pressure");
        dto.setWarningLevel("high");
        dto.setWarningContent("Systolic pressure is above normal range");
        dto.setWarningTime(LocalDateTime.of(2026, 7, 8, 10, 0, 0));
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
            "Health Warning Test Elderly", "male", 78, TEST_ID_CARD, "13999990003", "Test Area",
            "Test Contact", "13899990003", "Hypertension", "None", "high");
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
        jdbcTemplate.update("DELETE FROM elderly_profile WHERE id_card = ?", TEST_ID_CARD);
    }
}
