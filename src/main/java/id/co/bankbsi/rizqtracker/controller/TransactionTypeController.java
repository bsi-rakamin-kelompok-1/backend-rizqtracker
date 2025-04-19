package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.response.TransactionTypeResponse;
import id.co.bankbsi.rizqtracker.service.TransactionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transaction-types")
public class TransactionTypeController {
    @Autowired
    private TransactionTypeService transactionTypeService;

    @GetMapping
    public ResponseEntity<TransactionTypeResponse> getAllTransactionTypes() {
        TransactionTypeResponse response = new TransactionTypeResponse();
        response.setSuccess(true);
        response.setMessage("Transaction types retrieved successfully");
        response.setData(this.transactionTypeService.getAllTransactionTypes());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
