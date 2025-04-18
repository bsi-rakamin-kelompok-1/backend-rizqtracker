package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.repository.TopupMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopupMethodService {
    @Autowired
    private TopupMethodRepository topupMethodRepository;
}
