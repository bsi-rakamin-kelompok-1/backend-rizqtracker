package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.request.TransactionRequest;
import id.co.bankbsi.rizqtracker.dto.response.BaseResponse;
import id.co.bankbsi.rizqtracker.dto.response.TransactionResponse;
import id.co.bankbsi.rizqtracker.model.Transaction;
import id.co.bankbsi.rizqtracker.service.TransactionService;
import id.co.bankbsi.rizqtracker.util.JwtUtil;
import id.co.bankbsi.rizqtracker.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {
    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<BaseResponse> getAllTransactions() {

        return ResponseEntity.ok(new BaseResponse(true, "userId: " + securityUtil.getCurrentUserId()));
    }

    @PostMapping("/v1/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestParam(name = "type") String type,
            @Valid @RequestBody TransactionRequest req) {
        Transaction newTransaction = this.transactionService.createTransaction(type, req);

        TransactionResponse response = new TransactionResponse();
        response.setSuccess(true);
        response.setMessage("Transaction created successfully");
        response.setData(newTransaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
