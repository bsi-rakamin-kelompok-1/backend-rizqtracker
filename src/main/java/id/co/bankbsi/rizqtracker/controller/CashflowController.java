package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.response.CashflowExpenseResponse;
import id.co.bankbsi.rizqtracker.dto.response.CashflowIncomeResponse;
import id.co.bankbsi.rizqtracker.dto.response.CashflowSummaryResponse;
import id.co.bankbsi.rizqtracker.service.TransactionService;
import id.co.bankbsi.rizqtracker.util.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/cashflow")
public class CashflowController {
    @Autowired
    private SecurityUtility securityUtility;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<CashflowSummaryResponse> getCashflowSummary(
            @RequestParam(defaultValue = "week") String period) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = calculateStartDate(period, end);
        Integer userId = this.securityUtility.getCurrentUserId();

        CashflowSummaryResponse response = this.transactionService.getCashflowSummary(userId, start, end);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/income")
    public ResponseEntity<CashflowIncomeResponse> getIncomeCashflow(
            @RequestParam(defaultValue = "week") String period) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = calculateStartDate(period, end);
        Integer userId = this.securityUtility.getCurrentUserId();

        CashflowIncomeResponse response = this.transactionService.getIncomeCashflow(userId, start, end);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/expense")
    public ResponseEntity<CashflowExpenseResponse> getExpenseCashflow(
            @RequestParam(defaultValue = "week") String period) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = calculateStartDate(period, end);
        Integer userId = this.securityUtility.getCurrentUserId();

        CashflowExpenseResponse response = this.transactionService.getExpenseCashflow(userId, start, end);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private LocalDateTime calculateStartDate(String period, LocalDateTime end) {
        return switch (period.toLowerCase()) {
            case "week" -> end.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                    .withHour(0).withMinute(0).withSecond(0);
            case "month" -> end.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            case "three_months" -> end.minusMonths(3).withHour(0).withMinute(0).withSecond(0);
            default -> end.minusDays(7).withHour(0).withMinute(0).withSecond(0);
        };
    }
}
