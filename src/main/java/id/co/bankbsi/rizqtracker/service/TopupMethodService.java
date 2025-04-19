package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.model.TopupMethod;
import id.co.bankbsi.rizqtracker.repository.TopupMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopupMethodService {
    @Autowired
    private TopupMethodRepository topupMethodRepository;

    public List<TopupMethod> getAllTopupMethods() {
        return this.topupMethodRepository.findAll();
    }
}
