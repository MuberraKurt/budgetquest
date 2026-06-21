package com.budgetquest.transactionservice.controller;

import com.budgetquest.transactionservice.dto.TransactionRequest;
import com.budgetquest.transactionservice.dto.TransactionResponse;
import com.budgetquest.transactionservice.entity.TransactionType;
import com.budgetquest.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public List<TransactionResponse> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category
    ) {
        return service.list(jwt.getSubject(), month, year, type, category);
    }

    @GetMapping("/{id}")
    public TransactionResponse get(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        return service.get(jwt.getSubject(), id);
    }

    @PostMapping
    public TransactionResponse create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransactionRequest request
    ) {
        return service.create(jwt.getSubject(), request);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request
    ) {
        return service.update(jwt.getSubject(), id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        service.delete(jwt.getSubject(), id);
    }
}
