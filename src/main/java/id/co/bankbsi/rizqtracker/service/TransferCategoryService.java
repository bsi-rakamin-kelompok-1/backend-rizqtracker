package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.repository.TransferCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferCategoryService {
    @Autowired
    private TransferCategoryRepository transferCategoryRepository;
}
