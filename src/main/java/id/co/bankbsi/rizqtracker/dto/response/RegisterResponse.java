package id.co.bankbsi.rizqtracker.dto.response;

import id.co.bankbsi.rizqtracker.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterResponse extends BaseResponse {
    private UserData data;

    @Data
    public static class UserData {
        private String email;
        private String fullName;
        private String phoneNumber;
        private String avatarUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    public static RegisterResponse fromUser(User user) {
        RegisterResponse response = new RegisterResponse();
        response.setStatus(true);
        response.setMessage("User created successfully");

        UserData userData = new UserData();
        userData.setEmail(user.getEmail());
        userData.setFullName(user.getFullName());
        userData.setPhoneNumber(user.getPhoneNumber());
        userData.setAvatarUrl(user.getAvatarUrl());
        userData.setCreatedAt(user.getCreatedAt());
        userData.setUpdatedAt(user.getUpdatedAt());

        response.setData(userData);
        return response;
    }
}
