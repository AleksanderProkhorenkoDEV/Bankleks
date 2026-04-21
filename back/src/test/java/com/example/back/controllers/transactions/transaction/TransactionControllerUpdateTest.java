package com.example.back.controllers.transactions.transaction;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;

import com.example.back.controllers.TransactionController;
import com.example.back.dto.transaction.transaction.UpdateConceptRequestDTO;

@WebMvcTest(controllers = TransactionController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class TransactionControllerUpdateTest extends TransactionControllerBase {

    @Test
    void shouldUpdateWithValidFields() throws Exception {
        UpdateConceptRequestDTO request = new UpdateConceptRequestDTO("Test concept update", 1L);

        doNothing().when(transactionServices).updateConcept(request);

        mockMvc.perform(patch("/transaction/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Transacción actualizada")))
                .andExpect(jsonPath("$.status", is(204)));

        verify(transactionServices).updateConcept(request);
    }

    @Test
    void shouldReturnBadRequestIfConceptIsNull() throws Exception {
        UpdateConceptRequestDTO request = new UpdateConceptRequestDTO(null, 1L);

        mockMvc.perform(patch("/transaction/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfConceptIsTooShort() throws Exception {
        UpdateConceptRequestDTO request = new UpdateConceptRequestDTO("ab", 1L);

        mockMvc.perform(patch("/transaction/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfConceptIsTooLong() throws Exception {
        UpdateConceptRequestDTO request = new UpdateConceptRequestDTO("a".repeat(255), 1L);

        mockMvc.perform(patch("/transaction/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfTransactionIdIsNull() throws Exception {
        UpdateConceptRequestDTO request = new UpdateConceptRequestDTO("concepto válido", null);

        mockMvc.perform(patch("/transaction/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
