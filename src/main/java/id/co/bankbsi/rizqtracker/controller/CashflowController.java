package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.response.IncomeCashflowResponse;
import id.co.bankbsi.rizqtracker.service.TransactionService;
import id.co.bankbsi.rizqtracker.util.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@RestController
@RequestMapping("/v1/cashflow")
public class CashflowController {
    @Autowired
    private SecurityUtility securityUtility;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/income")
    public ResponseEntity<IncomeCashflowResponse> getIncomeCashflow(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime start = Optional.ofNullable(startDate)
                .map(date -> LocalDateTime.of(date, LocalTime.MIN))
                .orElse(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).minusDays(7));

        LocalDateTime end = Optional.ofNullable(endDate)
                .map(date -> LocalDateTime.of(date, LocalTime.MAX))
                .orElse(LocalDateTime.now());

        Integer userId = this.securityUtility.getCurrentUserId();

        IncomeCashflowResponse response = this.transactionService.getIncomeCashflow(userId, start, end);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
