package com.finflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finflow.dto.CreateApplicationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/loans/products - should return list of loan products")
    void shouldReturnLoanProducts() throws Exception {
        mockMvc.perform(get("/api/loans/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].productName", is("Flexi-Personal Loan")));
    }

    @Test
    @DisplayName("POST /api/loans/applications - should create application when input is valid")
    void shouldCreateApplicationSuccessfully() throws Exception {
        CreateApplicationRequest request = new CreateApplicationRequest(
                "Jane Doe",
                "jane.doe@example.com",
                new BigDecimal("55000.00"),
                "PERSONAL",
                new BigDecimal("200000.00"),
                24
        );

        mockMvc.perform(post("/api/loans/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName", is("Jane Doe")))
                .andExpect(jsonPath("$.applicationStatus", is("SUBMITTED")));
    }

    @Test
    @DisplayName("POST /api/loans/applications - should return 400 when validation fails")
    void shouldFailValidationOnInvalidInput() throws Exception {
        CreateApplicationRequest invalidRequest = new CreateApplicationRequest(
                "", // Blank name
                "not-an-email", // Invalid email
                new BigDecimal("500.00"), // Under minimum 1000
                "PERSONAL",
                new BigDecimal("5000.00"), // Under minimum 10000
                2 // Under minimum 6 months
        );

        mockMvc.perform(post("/api/loans/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    @DisplayName("GET /api/loans/diagnostic/singleton - proves singleton bean scope")
    void shouldDemonstrateSingletonScope() throws Exception {
        mockMvc.perform(get("/api/loans/diagnostic/singleton"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scope", is("SINGLETON (Spring default)")));
    }

    @Test
    @DisplayName("Full workflow: Submit -> Evaluate -> Approve")
    void shouldRunWorkflowSuccessfully() throws Exception {
        CreateApplicationRequest request = new CreateApplicationRequest(
                "Mark Smith",
                "mark.smith@example.com",
                new BigDecimal("80000.00"),
                "HOME",
                new BigDecimal("3000000.00"),
                180
        );

        String createResponse = mockMvc.perform(post("/api/loans/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        // Evaluate
        mockMvc.perform(post("/api/loans/applications/" + id + "/evaluate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible", is(true)));

        // Approve
        mockMvc.perform(patch("/api/admin/applications/" + id + "/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationStatus", is("APPROVED")));
    }
}
