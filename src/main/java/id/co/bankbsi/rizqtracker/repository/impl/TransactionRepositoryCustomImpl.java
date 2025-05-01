package id.co.bankbsi.rizqtracker.repository.impl;

import id.co.bankbsi.rizqtracker.model.Transaction;
import id.co.bankbsi.rizqtracker.repository.TransactionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Transaction> findTransactionsWithFilters(
            Integer userId,
            String keyword,
            String transactionType,
            String transferCategory,
            String topupMethod,
            Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = query.from(Transaction.class);

        List<Predicate> predicates = buildPredicates(cb, transaction, userId, keyword,
                transactionType, transferCategory, topupMethod);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            for (Sort.Order order : pageable.getSort()) {
                Path<Object> path = transaction.get(order.getProperty());
                orders.add(order.isAscending() ? cb.asc(path) : cb.desc(path));
            }
            query.orderBy(orders);
        } else {
            query.orderBy(cb.desc(transaction.get("createdAt")));
        }

        TypedQuery<Transaction> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Transaction> results = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Transaction> countRoot = countQuery.from(Transaction.class);

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, userId, keyword,
                transactionType, transferCategory, topupMethod);

        countQuery.select(cb.count(countRoot));
        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<Transaction> transaction,
            Integer userId,
            String keyword,
            String transactionType,
            String transferCategory,
            String topupMethod) {

        List<Predicate> predicates = new ArrayList<>();

        Join<Object, Object> senderAccount = transaction.join("senderAccount", JoinType.INNER);
        Join<Object, Object> senderUser = senderAccount.join("user", JoinType.INNER);
        Join<Object, Object> transactionTypeJoin = transaction.join("transactionType", JoinType.INNER);

        Join<Object, Object> recipientAccount = transaction.join("recipientAccount", JoinType.LEFT);
        Join<Object, Object> recipientUser = recipientAccount.join("user", JoinType.LEFT);
        Join<Object, Object> transferCategoryJoin = transaction.join("transferCategory", JoinType.LEFT);
        Join<Object, Object> topupMethodJoin = transaction.join("topupMethod", JoinType.LEFT);

        // User ID filter - include both sender and recipient transactions
        Predicate userIsSender = cb.equal(senderUser.get("id"), userId);
        Predicate userIsRecipient = cb.and(
            cb.isNotNull(recipientAccount.get("id")),
            cb.equal(recipientUser.get("id"), userId)
        );
        predicates.add(cb.or(userIsSender, userIsRecipient));

        predicates.add(cb.equal(transaction.get("isDeleted"), false));

        // Search by full name or notes
        if (keyword != null && !keyword.trim().isEmpty()) {
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";

            Predicate senderNameLike = cb.like(cb.lower(senderUser.get("fullName")), likePattern);

            Predicate recipientNameLike = cb.and(
                    cb.isNotNull(recipientAccount.get("id")),
                    cb.like(cb.lower(recipientUser.get("fullName")), likePattern)
            );

            Predicate notesLike = cb.like(cb.lower(transaction.get("notes")), likePattern);

            predicates.add(cb.or(senderNameLike, recipientNameLike, notesLike));
        }

        if (transactionType != null && !transactionType.trim().isEmpty()) {
            predicates.add(cb.equal(transactionTypeJoin.get("name"), transactionType));
        }

        if (transferCategory != null && !transferCategory.trim().isEmpty()) {
            predicates.add(cb.and(
                    cb.isNotNull(transaction.get("transferCategory")),
                    cb.equal(transferCategoryJoin.get("name"), transferCategory)
            ));
        }

        if (topupMethod != null && !topupMethod.trim().isEmpty()) {
            predicates.add(cb.and(
                    cb.isNotNull(transaction.get("topupMethod")),
                    cb.equal(topupMethodJoin.get("name"), topupMethod)
            ));
        }

        return predicates;
    }
}