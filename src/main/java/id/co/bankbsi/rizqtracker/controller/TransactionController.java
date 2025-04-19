package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.request.TopupRequest;
import id.co.bankbsi.rizqtracker.dto.request.TransferRequest;
import id.co.bankbsi.rizqtracker.dto.response.TopupResponse;
import id.co.bankbsi.rizqtracker.dto.response.TransactionResponse;
import id.co.bankbsi.rizqtracker.dto.response.TransferResponse;
import id.co.bankbsi.rizqtracker.model.Transaction;
import id.co.bankbsi.rizqtracker.service.TransactionService;
import id.co.bankbsi.rizqtracker.util.SecurityUtility;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {
    @Autowired
    private SecurityUtility securityUtility;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<TransactionResponse> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort_by,
            @RequestParam(defaultValue = "desc") String sort_type
    ) {
        Sort sort = sort_type.equalsIgnoreCase("asc") ?
                Sort.by(sort_by).ascending() : Sort.by(sort_by).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Transaction> transactionsPage = this.transactionService
                .getAllTransactionsByUserId(this.securityUtility.getCurrentUserId(), pageable);

        TransactionResponse response = new TransactionResponse();
        response.setSuccess(true);
        response.setMessage("Transactions retrieved successfully");
        response.setData(transactionsPage);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody TransferRequest req) {
        Transaction newTransfer = this.transactionService
                .createTransfer(req, this.securityUtility.getCurrentUserId());

        TransferResponse response = TransferResponse.from(newTransfer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/topup")
    public ResponseEntity<TopupResponse> createTopup(
            @Valid @RequestBody TopupRequest req) {
        Transaction newTopup = this.transactionService
                .createTopup(req, this.securityUtility.getCurrentUserId());

        TopupResponse response = TopupResponse.from(newTopup);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
