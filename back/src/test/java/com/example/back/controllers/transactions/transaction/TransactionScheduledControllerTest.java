package com.example.back.controllers.transactions.transaction;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.back.controllers.GlobalExceptionController;
import com.example.back.controllers.ScheduledTransactionController;
import com.example.back.dto.mappers.ScheduledTransferMapper;
import com.example.back.dto.transaction.account.AccountSummaryDTO;
import com.example.back.dto.transaction.transaction.ScheduledTransactionResponseDTO;
import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.enums.RecurrenceType;
import com.example.back.enums.ScheduledTransactionType;
import com.example.back.services.JwtService;
import com.example.back.services.TransactionScheduledService;
import com.example.back.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(controllers = ScheduledTransactionController.class)
@Import(GlobalExceptionController.class)
public class TransactionScheduledControllerTest {
    @Autowired
    private ScheduledTransactionController scheduledTransactionController;

    @Autowired
    private GlobalExceptionController globalExceptionController;

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    protected TransactionScheduledService transactionScheduledService;

    @MockitoBean
    protected ScheduledTransferMapper scheduledTransferMapper;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected UserDetailsServiceImpl userDetailsServiceImpl;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(scheduledTransactionController, globalExceptionController)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    private static final String BASE_URL = "/admin/scheduled-transfers";

    private AccountSummaryDTO buildAccountSummary(Long id, String number) {
        AccountSummaryDTO dto = new AccountSummaryDTO();
        dto.setId(id);
        dto.setAccountNumber(number);
        return dto;
    }

    private ScheduledTransactionResponseDTO buildResponseDTO(Long id) {
        return new ScheduledTransactionResponseDTO(
                id,
                250.0,
                "Pago mensual",
                buildAccountSummary(1L, "ES1234567890"),
                buildAccountSummary(2L, "ES0987654321"),
                Instant.parse("2024-03-01T10:00:00Z"),
                "Europe/Madrid",
                ScheduledTransactionType.FAILED,
                RecurrenceType.BEGINNING_OF_MONTH,
                Instant.parse("2024-12-31T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z"));
    }

    @Test
    void getFailedTransfers_shouldReturnPage_withDefaultParams() throws Exception {
        ScheduledTransfer entity = new ScheduledTransfer();
        ScheduledTransactionResponseDTO dto = buildResponseDTO(1L);

        PageImpl<ScheduledTransfer> entityPage = new PageImpl<>(
                List.of(entity), PageRequest.of(0, 25), 1);

        when(transactionScheduledService.getFailedScheduledTransfers(0, 25)).thenReturn(entityPage);
        when(scheduledTransferMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].amount").value(250.0))
                .andExpect(jsonPath("$.content[0].concept").value("Pago mensual"))
                .andExpect(jsonPath("$.content[0].status").value("FAILED"))
                .andExpect(jsonPath("$.content[0].recurrence").value("BEGINNING_OF_MONTH"))
                .andExpect(jsonPath("$.content[0].targetTimezone").value("Europe/Madrid"));
    }

    @Test
    void getFailedTransfers_shouldReturnPage_withCustomParams() throws Exception {
        ScheduledTransfer entity = new ScheduledTransfer();
        ScheduledTransactionResponseDTO dto1 = buildResponseDTO(1L);
        ScheduledTransactionResponseDTO dto2 = buildResponseDTO(2L);

        PageImpl<ScheduledTransfer> entityPage = new PageImpl<>(
                List.of(entity, entity), PageRequest.of(1, 10), 2);

        when(transactionScheduledService.getFailedScheduledTransfers(1, 10)).thenReturn(entityPage);
        when(scheduledTransferMapper.toDto(entity)).thenReturn(dto1).thenReturn(dto2);

        mockMvc.perform(get(BASE_URL + "/failed")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));
    }

    @Test
    void getFailedTransfers_shouldReturnEmptyPage_whenNoFailedTransfers() throws Exception {
        PageImpl<ScheduledTransfer> emptyPage = new PageImpl<>(
                List.of(), PageRequest.of(0, 25), 0);

        when(transactionScheduledService.getFailedScheduledTransfers(0, 25)).thenReturn(emptyPage);

        mockMvc.perform(get(BASE_URL + "/failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void retryFailedTransfer_shouldReturn200_withSuccessMessage() throws Exception {
        doNothing().when(transactionScheduledService).retryFailedScheduledTransfer(1L);

        mockMvc.perform(post(BASE_URL + "/1/retry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transferencia reintentada con éxito"))
                .andExpect(jsonPath("$.status").value(200));

        verify(transactionScheduledService).retryFailedScheduledTransfer(1L);
    }

    @Test
    void retryFailedTransfer_shouldReturn404_whenTransferNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Transferencia no encontrada"))
                .when(transactionScheduledService).retryFailedScheduledTransfer(99L);

        mockMvc.perform(post(BASE_URL + "/99/retry"))
                .andExpect(status().isNotFound());
    }

    @Test
    void retryFailedTransfer_shouldReturn400_whenTransferIsNotFailed() throws Exception {
        doThrow(new IllegalStateException("La transferencia no está en estado FAILED"))
                .when(transactionScheduledService).retryFailedScheduledTransfer(1L);

        mockMvc.perform(post(BASE_URL + "/1/retry"))
                .andExpect(status().isBadRequest());
    }
}
