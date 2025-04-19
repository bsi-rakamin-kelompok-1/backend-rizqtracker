package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.response.TransferCategoryResponse;
import id.co.bankbsi.rizqtracker.service.TransferCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transfer-categories")
public class TransferCategoryController {
    @Autowired
    private TransferCategoryService transferCategoryService;

    @GetMapping
    public ResponseEntity<TransferCategoryResponse> getAllTransferCategories() {
        TransferCategoryResponse response = new TransferCategoryResponse();
        response.setSuccess(true);
        response.setMessage("Transfer categories retrieved successfully");
        response.setData(this.transferCategoryService.getAllTransferCategories());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
