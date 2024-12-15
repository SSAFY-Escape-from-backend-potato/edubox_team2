package com.backend_potato.edubox_team2.domain.users.service;

import com.backend_potato.edubox_team2.domain.users.entity.LoginRequestDTO;
import com.backend_potato.edubox_team2.domain.users.entity.ProfileUpdateRequestDTO;
import com.backend_potato.edubox_team2.domain.users.entity.SignupRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    void createUser(SignupRequestDTO signupRequestDTO);

    String getUserByEmailAndPw(LoginRequestDTO loginRequestDTO);

    void updateProfile(MultipartFile image, ProfileUpdateRequestDTO profileUpdateRequestDTO);

}
