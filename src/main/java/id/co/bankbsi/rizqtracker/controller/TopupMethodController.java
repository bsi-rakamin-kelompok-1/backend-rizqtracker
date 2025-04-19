package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.response.TopupMethodResponse;
import id.co.bankbsi.rizqtracker.dto.response.TransactionTypeResponse;
import id.co.bankbsi.rizqtracker.service.TopupMethodService;
import id.co.bankbsi.rizqtracker.service.TransactionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/topup-methods")
public class TopupMethodController {
    @Autowired
    private TopupMethodService topupMethodService;

    @GetMapping
    public ResponseEntity<TopupMethodResponse> getAllTransactionTypes() {
        TopupMethodResponse response = new TopupMethodResponse();
        response.setSuccess(true);
        response.setMessage("Topup methods retrieved successfully");
        response.setData(this.topupMethodService.getAllTopupMethods());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
