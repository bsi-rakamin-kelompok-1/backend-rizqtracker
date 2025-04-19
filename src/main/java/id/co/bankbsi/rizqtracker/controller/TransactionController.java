package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.request.TopupRequest;
import id.co.bankbsi.rizqtracker.dto.request.TransferRequest;
import id.co.bankbsi.rizqtracker.dto.response.BaseResponse;
import id.co.bankbsi.rizqtracker.dto.response.TopupResponse;
import id.co.bankbsi.rizqtracker.dto.response.TransactionResponse;
import id.co.bankbsi.rizqtracker.dto.response.TransferResponse;
import id.co.bankbsi.rizqtracker.model.Transaction;
import id.co.bankbsi.rizqtracker.service.TransactionService;
import id.co.bankbsi.rizqtracker.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody TransferRequest req) {
        Transaction newTransfer = this.transactionService
                .createTransfer(req, this.securityUtil.getCurrentUserId());

        TransferResponse response = TransferResponse.from(newTransfer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/topup")
    public ResponseEntity<TopupResponse> createTopup(
            @Valid @RequestBody TopupRequest req) {
        Transaction newTopup = this.transactionService
                .createTopup(req, this.securityUtil.getCurrentUserId());

        TopupResponse response = TopupResponse.from(newTopup);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
