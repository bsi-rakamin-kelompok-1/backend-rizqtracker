package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.dto.response.RecentAccountsResponse;
import id.co.bankbsi.rizqtracker.dto.response.UserAccountResponse;
import id.co.bankbsi.rizqtracker.exception.ResourceNotFoundException;
import id.co.bankbsi.rizqtracker.model.Account;
import id.co.bankbsi.rizqtracker.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public UserAccountResponse checkAccountExists(Long accountNumber) {
        UserAccountResponse response = new UserAccountResponse();

        Account account = this.accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account number not found"));


        UserAccountResponse.Detail detail = new UserAccountResponse.Detail();
        detail.setFullName(account.getUser().getFullName());
        detail.setAccountNumber(account.getAccountNumber());

        response.setData(detail);
        response.setSuccess(true);
        response.setMessage("Account number found");

        return response;
    }

    public RecentAccountsResponse getTopFiveRecentRecipients(Integer userId) {
        RecentAccountsResponse response = new RecentAccountsResponse();

        // Get sender account for the current user
        Account senderAccount = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

        // Raw SQL query to find recent transactions based on model structure
        String sql = """
            SELECT DISTINCT ON (ra.account_number)
                ra.account_number as account_number,
                u.full_name as full_name,
                t.created_at as created_at
            FROM transaction_histories t
            JOIN accounts ra ON t.recipient_account = ra.account_number
            JOIN users u ON ra.user_id = u.id
            JOIN transaction_types tt ON t.transaction_type_id = tt.id
            WHERE t.sender_account = :senderAccountNumber
            AND tt.name = 'transfer'
            AND t.recipient_account IS NOT NULL
            AND t.is_deleted = false
            ORDER BY ra.account_number, t.created_at DESC
            LIMIT 5
        """;

        // Execute native query with sender account number parameter
        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("senderAccountNumber", senderAccount.getAccountNumber())
                .getResultList();

        System.out.println("Query results size: " + results.size());

        // Process results
        List<UserAccountResponse.Detail> recentRecipients = new ArrayList<>();

        for (Object[] row : results) {
            UserAccountResponse.Detail detail = new UserAccountResponse.Detail();
            detail.setAccountNumber(((Number) row[0]).longValue());
            detail.setFullName((String) row[1]);
            recentRecipients.add(detail);
        }

        response.setSuccess(true);
        response.setMessage("Success get top five recent recipient accounts");
        response.setData(recentRecipients);

        return response;
    }

}
