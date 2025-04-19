package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {
    @GetMapping
    public ResponseEntity<BaseResponse> getAllTransactions() {
        return ResponseEntity.ok(new BaseResponse(true, "List of all transactions tests"));
    }
}
