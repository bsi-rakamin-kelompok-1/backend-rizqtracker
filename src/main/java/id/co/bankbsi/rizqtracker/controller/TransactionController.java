package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.request.TopupRequest;
import id.co.bankbsi.rizqtracker.dto.request.TransferRequest;
import id.co.bankbsi.rizqtracker.dto.response.TopupResponse;
import id.co.bankbsi.rizqtracker.dto.response.TransactionResponse;
import id.co.bankbsi.rizqtracker.dto.response.TransferResponse;
import id.co.bankbsi.rizqtracker.model.Transaction;
import id.co.bankbsi.rizqtracker.service.PdfGeneratorService;
import id.co.bankbsi.rizqtracker.service.TransactionService;
import id.co.bankbsi.rizqtracker.util.SecurityUtility;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {
    @Autowired
    private SecurityUtility securityUtility;

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping
    public ResponseEntity<TransactionResponse> getAllTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort_by,
            @RequestParam(defaultValue = "desc") String sort_type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String transaction_type,
            @RequestParam(required = false) String transfer_category,
            @RequestParam(required = false) String topup_method
    ) {
        Sort sort = sort_type.equalsIgnoreCase("asc") ?
                Sort.by(sort_by).ascending() : Sort.by(sort_by).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Transaction> transactionsPage = this.transactionService
                .searchAndFilterTransactions(
                        this.securityUtility.getCurrentUserId(),
                        search,
                        transaction_type,
                        transfer_category,
                        topup_method,
                        pageable
                );

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

    @GetMapping("/generate-pdf")
    public ResponseEntity<Resource> generateMonthlyPdf(
            @RequestParam(name = "period", required = true) String periodString) {
        try {
            YearMonth period = YearMonth.parse(periodString, DateTimeFormatter.ofPattern("yyyy-MM"));
            
            Integer userId = securityUtility.getCurrentUserId();
            
            byte[] pdfContent = pdfGeneratorService.generateMonthlyTransactionReport(userId, period);
            
            ByteArrayResource resource = new ByteArrayResource(pdfContent);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"transaction-summary-" + periodString + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfContent.length)
                    .body(resource);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/available-periods")
    public ResponseEntity<?> getAvailablePeriods() {
        Integer userId = securityUtility.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getAvailableTransactionPeriods(userId));
    }
}
