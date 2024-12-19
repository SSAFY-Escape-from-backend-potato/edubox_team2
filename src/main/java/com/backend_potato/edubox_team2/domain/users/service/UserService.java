package com.backend_potato.edubox_team2.domain.users.service;

import com.backend_potato.edubox_team2.domain.users.entity.LoginRequestDTO;
import com.backend_potato.edubox_team2.domain.users.entity.ProfileUpdateRequestDTO;
import com.backend_potato.edubox_team2.domain.users.entity.SignupRequestDTO;
import com.backend_potato.edubox_team2.domain.users.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    void createUser(SignupRequestDTO signupRequestDTO);
    void updateProfile(MultipartFile image, ProfileUpdateRequestDTO profileUpdateRequestDTO, HttpServletRequest request);

    User authenticate(LoginRequestDTO loginRequestDTO);

    void removeAccessToken(String accessToken);
    String verifyEmailLink(String token);
    void softDeleteUserByEmail(String email);
    void hardDeleteUsers();
    void restoreUser(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByProfileAddress(String profileAddress);
    void storeAccessToken(String email, String accessToken);
    boolean isValidAccessToken(String accessToken);
}
