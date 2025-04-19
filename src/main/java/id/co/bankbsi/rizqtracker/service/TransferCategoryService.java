package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.model.TransferCategory;
import id.co.bankbsi.rizqtracker.repository.TransferCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferCategoryService {
    @Autowired
    private TransferCategoryRepository transferCategoryRepository;

    public List<TransferCategory> getAllTransferCategories() {
        return this.transferCategoryRepository.findAll();
    }
}
