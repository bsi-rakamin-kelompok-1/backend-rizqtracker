package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.dto.request.TopupRequest;
import id.co.bankbsi.rizqtracker.dto.request.TransferRequest;
import id.co.bankbsi.rizqtracker.dto.response.BaseCashflowResponse;
import id.co.bankbsi.rizqtracker.dto.response.CashflowExpenseResponse;
import id.co.bankbsi.rizqtracker.dto.response.CashflowIncomeResponse;
import id.co.bankbsi.rizqtracker.dto.response.CashflowSummaryResponse;
import id.co.bankbsi.rizqtracker.exception.InsufficientBalanceException;
import id.co.bankbsi.rizqtracker.exception.ResourceNotFoundException;
import id.co.bankbsi.rizqtracker.model.*;
import id.co.bankbsi.rizqtracker.repository.*;
import id.co.bankbsi.rizqtracker.util.ReferenceNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private static final String TRANSFER = "transfer";
    private static final String TOPUP = "topup";

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @Autowired
    private TransferCategoryRepository transferCategoryRepository;

    @Autowired
    private TopupMethodRepository topupMethodRepository;

    public Page<Transaction> getAllTransactionsByUserId(Integer userId, Pageable pageable) {
        return this.transactionRepository.findAllBySenderAccount_User_Id(userId, pageable);
    }

    public List<Transaction> findTopupTransactionsByUserIdAndDateRange(String type, Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        return this.transactionRepository.findByTransactionType_NameAndSenderAccount_User_IdAndCreatedAtBetween(type, userId, startDate, endDate);
    }

    public List<Transaction> findTransferTransactionsByUserIdAndDateRange(String type, Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        return this.transactionRepository.findByTransactionType_NameAndRecipientAccount_User_IdAndCreatedAtBetween(type, userId, startDate, endDate);
    }

    @Transactional
    public Transaction createTransfer(TransferRequest req, Integer userId) {
        TransactionType type = this.transactionTypeRepository.findByName(TRANSFER)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction type not found"));

        Account sender = this.accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

        Account recipient = this.accountRepository.findByAccountNumber(req.getRecipientAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient account not found"));

        TransferCategory category = this.transferCategoryRepository.findByName(req.getTransferCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Transfer category not found"));

        if (Objects.equals(sender.getAccountNumber(), recipient.getAccountNumber())) {
            throw new IllegalArgumentException("Sender and recipient accounts cannot be the same");
        }

        if (sender.getBalance() < req.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }

        String referenceNumber = ReferenceNumberGenerator.generate(type.getName());

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setSenderAccount(sender);
        transaction.setRecipientAccount(recipient);
        transaction.setTransferCategory(category);
        transaction.setAmount(req.getAmount());
        transaction.setNotes(req.getNotes());
        transaction.setReferenceNumber(referenceNumber);

        sender.setBalance(sender.getBalance() - req.getAmount());
        recipient.setBalance(recipient.getBalance() + req.getAmount());

        Transaction savedTransaction = this.transactionRepository.save(transaction);

        this.accountRepository.saveAll(List.of(sender, recipient));

        return savedTransaction;
    }

    @Transactional
    public Transaction createTopup(TopupRequest req, Integer userId) {
        TransactionType type = this.transactionTypeRepository.findByName(TOPUP)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction type not found"));

        Account sender = this.accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

        TopupMethod method = this.topupMethodRepository.findByName(req.getTopupMethod())
                .orElseThrow(() -> new ResourceNotFoundException("Topup method not found"));

        String referenceNumber = ReferenceNumberGenerator.generate(type.getName());

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setSenderAccount(sender);
        transaction.setTopupMethod(method);
        transaction.setAmount(req.getAmount());
        transaction.setNotes(req.getNotes());
        transaction.setReferenceNumber(referenceNumber);

        sender.setBalance(sender.getBalance() + req.getAmount());

        Transaction savedTransaction = this.transactionRepository.save(transaction);

        this.accountRepository.save(sender);

        return savedTransaction;
    }

    public Page<Transaction> searchAndFilterTransactions(
            Integer userId,
            String keyword,
            String transactionType,
            String transferCategory,
            String topupMethod,
            Pageable pageable) {

        // If no search or filter is specified, return all transactions
        if ((keyword == null || keyword.trim().isEmpty()) &&
                transactionType == null &&
                transferCategory == null &&
                topupMethod == null) {
            return getAllTransactionsByUserId(userId, pageable);
        }

        // Use null for empty strings to make the query simpler
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        System.out.printf("Searching transactions with keyword: %s, type: %s, category: %s, method: %s%n",
                searchKeyword, transactionType, transferCategory, topupMethod);

        return this.transactionRepository.searchAndFilterTransactions(
                userId,
                searchKeyword,
                transactionType,
                transferCategory,
                topupMethod,
                pageable
        );
    }

    public CashflowIncomeResponse getIncomeCashflow(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> topupTransactions = findTopupTransactionsByUserIdAndDateRange(TOPUP, userId, startDate, endDate);

        List<Transaction> transferTransactions = findTransferTransactionsByUserIdAndDateRange(TRANSFER, userId, startDate, endDate);

        List<CashflowIncomeResponse.TopupData> topupDataList = topupTransactions.stream().map(transaction -> {
            CashflowIncomeResponse.TopupData topupData = new CashflowIncomeResponse.TopupData();
            topupData.setTransactionId(transaction.getId());
            topupData.setTopupMethod(transaction.getTopupMethod().getName());
            topupData.setAmount(transaction.getAmount());
            topupData.setNotes(transaction.getNotes());
            topupData.setCreatedAt(transaction.getCreatedAt());
            return topupData;
        }).collect(Collectors.toList());

        List<CashflowIncomeResponse.TransferData> transferDataList = transferTransactions.stream().map(transaction -> {
            CashflowIncomeResponse.TransferData transferData = new CashflowIncomeResponse.TransferData();
            transferData.setTransactionId(transaction.getId());
            transferData.setTransactionCategory(transaction.getTransferCategory().getName());
            transferData.setSenderAccountNumber(transaction.getSenderAccount().getAccountNumber());
            transferData.setSenderFullName(transaction.getSenderAccount().getUser().getFullName());
            transferData.setAmount(transaction.getAmount());
            transferData.setNotes(transaction.getNotes());
            transferData.setCreatedAt(transaction.getCreatedAt());
            return transferData;
        }).collect(Collectors.toList());

        CashflowIncomeResponse.IncomeDetails incomeDetails = new CashflowIncomeResponse.IncomeDetails();
        incomeDetails.setTopupData(topupDataList);
        incomeDetails.setTransferData(transferDataList);

        CashflowIncomeResponse response = new CashflowIncomeResponse();
        response.setSuccess(true);
        response.setMessage("Cashflow income retrieved successfully");
        response.setPeriod(BaseCashflowResponse.Period.from(startDate, endDate));
        response.setIncomeDetails(incomeDetails);

        return response;
    }

    public CashflowExpenseResponse getExpenseCashflow(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transferTransactions = this.transactionRepository.findByTransactionType_NameAndSenderAccount_User_IdAndCreatedAtBetween(
                TRANSFER, userId, startDate, endDate);

        List<CashflowExpenseResponse.TransferData> needs = new ArrayList<>();
        List<CashflowExpenseResponse.TransferData> bills = new ArrayList<>();
        List<CashflowExpenseResponse.TransferData> shopping = new ArrayList<>();
        List<CashflowExpenseResponse.TransferData> transport = new ArrayList<>();
        List<CashflowExpenseResponse.TransferData> transferOfWealth = new ArrayList<>();

        for (Transaction transaction : transferTransactions) {
            String categoryName = transaction.getTransferCategory().getName().toLowerCase();
            CashflowExpenseResponse.TransferData transferData = new CashflowExpenseResponse.TransferData();
            transferData.setTransactionId(transaction.getId());
            transferData.setRecipientAccountNumber(transaction.getRecipientAccount().getAccountNumber());
            transferData.setRecipientFullName(transaction.getRecipientAccount().getUser().getFullName());
            transferData.setAmount(transaction.getAmount());
            transferData.setNotes(transaction.getNotes());
            transferData.setCreatedAt(transaction.getCreatedAt());

            switch (categoryName) {
                case "needs" -> needs.add(transferData);
                case "bills" -> bills.add(transferData);
                case "shopping" -> shopping.add(transferData);
                case "transport" -> transport.add(transferData);
                case "transfer_of_wealth" -> transferOfWealth.add(transferData);
                default -> {
                }
            }
        }

        CashflowExpenseResponse.ExpenseDetails expenseDetails = new CashflowExpenseResponse.ExpenseDetails();
        expenseDetails.setNeeds(needs);
        expenseDetails.setBills(bills);
        expenseDetails.setShopping(shopping);
        expenseDetails.setTransport(transport);
        expenseDetails.setTransferOfWealth(transferOfWealth);

        CashflowExpenseResponse response = new CashflowExpenseResponse();
        response.setSuccess(true);
        response.setMessage("Cashflow expense retrieved successfully");
        response.setPeriod(BaseCashflowResponse.Period.from(startDate, endDate));
        response.setExpenseDetails(expenseDetails);

        return response;
    }

    public CashflowSummaryResponse getCashflowSummary(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> topupTransactions = findTopupTransactionsByUserIdAndDateRange(TOPUP, userId, startDate, endDate);
        List<Transaction> incomeTransferTransactions = findTransferTransactionsByUserIdAndDateRange(TRANSFER, userId, startDate, endDate);

        List<Transaction> expenseTransferTransactions = findTopupTransactionsByUserIdAndDateRange(TRANSFER, userId, startDate, endDate);


        long totalTopup = topupTransactions.stream()
                .mapToLong(Transaction::getAmount)
                .sum();

        long totalIncomeTransfer = incomeTransferTransactions.stream()
                .mapToLong(Transaction::getAmount)
                .sum();

        long totalNeeds = calculateCategoryTotal(expenseTransferTransactions, "needs");
        long totalBills = calculateCategoryTotal(expenseTransferTransactions, "bills");
        long totalShopping = calculateCategoryTotal(expenseTransferTransactions, "shopping");
        long totalTransport = calculateCategoryTotal(expenseTransferTransactions, "transport");
        long totalTransferOfWealth = calculateCategoryTotal(expenseTransferTransactions, "transfer_of_wealth");

        CashflowSummaryResponse response = new CashflowSummaryResponse();
        response.setSuccess(true);
        response.setMessage("Cashflow summary retrieved successfully");
        response.setPeriod(BaseCashflowResponse.Period.from(startDate, endDate));

        CashflowSummaryResponse.Summary summary = new CashflowSummaryResponse.Summary();

        CashflowSummaryResponse.Income income = new CashflowSummaryResponse.Income();
        income.setTotalTopup(totalTopup);
        income.setTotalTransfer(totalIncomeTransfer);

        CashflowSummaryResponse.Expense expense = new CashflowSummaryResponse.Expense();
        expense.setTotalNeeds(totalNeeds);
        expense.setTotalBills(totalBills);
        expense.setTotalShopping(totalShopping);
        expense.setTotalTransport(totalTransport);
        expense.setTotalTransferOfWealth(totalTransferOfWealth);

        summary.setIncome(income);
        summary.setExpense(expense);
        response.setSummary(summary);

        return response;
    }

    private long calculateCategoryTotal(List<Transaction> transactions, String categoryName) {
        return transactions.stream()
                .filter(transaction -> transaction.getTransferCategory() != null &&
                        transaction.getTransferCategory().getName().toLowerCase().equals(categoryName))
                .mapToLong(Transaction::getAmount)
                .sum();
    }
}
