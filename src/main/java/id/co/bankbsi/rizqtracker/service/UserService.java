package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.dto.request.LoginRequest;
import id.co.bankbsi.rizqtracker.dto.request.RegisterRequest;
import id.co.bankbsi.rizqtracker.dto.response.UserRegistrationResult;
import id.co.bankbsi.rizqtracker.exception.PasswordMismatchException;
import id.co.bankbsi.rizqtracker.exception.ResourceNotFoundException;
import id.co.bankbsi.rizqtracker.exception.UserAlreadyExistsException;
import id.co.bankbsi.rizqtracker.model.Account;
import id.co.bankbsi.rizqtracker.model.User;
import id.co.bankbsi.rizqtracker.repository.AccountRepository;
import id.co.bankbsi.rizqtracker.repository.UserRepository;
import id.co.bankbsi.rizqtracker.util.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserRegistrationResult register(RegisterRequest req) {
        if (!Objects.equals(req.getPassword(), req.getConfirmPassword())) {
            throw new PasswordMismatchException("Password and confirm password do not match");
        }

        if (this.userRepository.existsByEmail(req.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (this.userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new UserAlreadyExistsException("Phone number already exists");
        }

        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setPassword(this.passwordEncoder.encode(req.getPassword()));
        newUser.setFullName(req.getFullName());
        newUser.setPhoneNumber(req.getPhoneNumber());

        User savedUser = this.userRepository.save(newUser);

        Account newAccount = new Account();
        newAccount.setUser(savedUser);

        Account savedAccount = this.accountRepository.save(newAccount);

        return UserRegistrationResult.from(savedUser, savedAccount);
    }

    public String login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(req.getEmail());
        User user = this.userRepository.findUserByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + req.getEmail()));

        return jwtUtility.generateToken(userDetails, user.getId());
    }
}
