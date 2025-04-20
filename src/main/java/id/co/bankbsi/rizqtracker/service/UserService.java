package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.dto.request.LoginRequest;
import id.co.bankbsi.rizqtracker.dto.request.RegisterRequest;
import id.co.bankbsi.rizqtracker.dto.response.UserDetailResponse;
import id.co.bankbsi.rizqtracker.dto.response.UserRegistrationResponse;
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
    public UserRegistrationResponse register(RegisterRequest req) {
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

        return UserRegistrationResponse.from(savedUser, savedAccount);
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

    public UserDetailResponse getCurrentUserDetails(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user with id: " + userId));

        return mapToUserDetailResponse(user, account);
    }

    private UserDetailResponse mapToUserDetailResponse(User user, Account account) {
        UserDetailResponse response = new UserDetailResponse();
        response.setSuccess(true);
        response.setMessage("User detail retrieved successfully");

        UserDetailResponse.UserDetail userDetail = new UserDetailResponse.UserDetail();
        userDetail.setEmail(user.getEmail());
        userDetail.setFullName(user.getFullName());
        userDetail.setPhoneNumber(user.getPhoneNumber());
        userDetail.setAvatarUrl(user.getAvatarUrl());
        userDetail.setCreatedAt(user.getCreatedAt());
        userDetail.setUpdatedAt(user.getUpdatedAt());

        UserDetailResponse.UserAccount userAccount = new UserDetailResponse.UserAccount();
        userAccount.setAccountNumber(account.getAccountNumber());
        userAccount.setBalance(account.getBalance());

        userDetail.setAccount(userAccount);
        response.setData(userDetail);

        return response;
    }
}
