package com.example.back.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.GlobalResponseDTO;
import com.example.back.dto.PageResponseDTO;
import com.example.back.dto.mappers.ScheduledTransferMapper;
import com.example.back.dto.transaction.transaction.ScheduledTransactionResponseDTO;
import com.example.back.services.TransactionScheduledService;

@RestController
@RequestMapping("/admin/scheduled-transfers")
public class ScheduledTransactionController {
    private final TransactionScheduledService transactionScheduledService;
    private final ScheduledTransferMapper scheduledTransferMapper;

    public ScheduledTransactionController(TransactionScheduledService transactionScheduledService,
            ScheduledTransferMapper scheduledTransferMapper) {
        this.transactionScheduledService = transactionScheduledService;
        this.scheduledTransferMapper = scheduledTransferMapper;
    }

    @GetMapping("/failed")
    public ResponseEntity<PageResponseDTO<ScheduledTransactionResponseDTO>> getFailedTransfers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "25") Integer size) {

        Page<ScheduledTransactionResponseDTO> dtoPage = transactionScheduledService
                .getFailedScheduledTransfers(page, size)
                .map(scheduledTransferMapper::toDto);

        return ResponseEntity.ok(new PageResponseDTO<>(dtoPage));
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<GlobalResponseDTO> retryFailedTransfer(@PathVariable Long id) {
        transactionScheduledService.retryFailedScheduledTransfer(id);
        return ResponseEntity.ok(new GlobalResponseDTO("Transferencia reintentada con éxito", HttpStatus.OK.value()));
    }
}
