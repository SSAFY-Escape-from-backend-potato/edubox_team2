package com.backend_potato.edubox_team2.domain.users.service;

import com.backend_potato.edubox_team2.domain.users.entity.*;
import com.backend_potato.edubox_team2.domain.users.repository.UserRepository;
import com.backend_potato.edubox_team2.global.aws.S3ImageUploader;
import com.backend_potato.edubox_team2.global.jwt.JwtFilter;
import com.backend_potato.edubox_team2.global.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final S3ImageUploader s3ImageUploader;
    private final EmailService emailService;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private final Map<String, String> accessTokenStore = new ConcurrentHashMap<>();

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtFilter jwtFilter;
    //private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @Override
    public void createUser(SignupRequestDTO signupRequestDTO) {
        if (!signupRequestDTO.getEmail().matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("이메일 형식이 유효하지 않습니다.");
        }
        if (userRepository.existsByEmail(signupRequestDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (!signupRequestDTO.getPw().equals(signupRequestDTO.getConfirmPw())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        boolean isCodeValid = emailService.verifyCode(signupRequestDTO.getEmail(), signupRequestDTO.getVerificationCode());
        if (!isCodeValid) {
            throw new IllegalArgumentException("Invalid verification code.");
        }
        String encodedPassword = BCrypt.hashpw(signupRequestDTO.getPw(), BCrypt.gensalt());
        User user = User.builder()
                .email(signupRequestDTO.getEmail())
                .pw(encodedPassword)
                .nickname(signupRequestDTO.getEmail().split("@")[0])
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }

    @Transactional
    @Override
    public String getUserByEmailAndPw(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다."));
        if (!BCrypt.checkpw(loginRequestDTO.getPw(), user.getPw())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.");
        }
        String token = "토큰";
        //String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        log.info("사용자 {}({})가 로그인했습니다.", user.getNickname(), user.getEmail());

        return token;
    }

    @Override
    public void updateProfile(MultipartFile image, ProfileUpdateRequestDTO profileUpdateRequestDTO, HttpServletRequest request){
//        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String token = jwtFilter.resolveToken(request);
        if (token == null) {
            throw new IllegalArgumentException("Access token is missing or invalid");
        }
        String userEmail = jwtTokenUtil.getUserEmailFromToken(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update the image URL if a new image is provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = s3ImageUploader.upload(image);
            user.setImage(imageUrl);
        }

        // Update other profile fields
        if (profileUpdateRequestDTO.getNickname() != null) {
            user.setNickname(profileUpdateRequestDTO.getNickname());
        }
        if (profileUpdateRequestDTO.getDiscription() != null) {
            user.setDiscription(profileUpdateRequestDTO.getDiscription());
        }
        if (profileUpdateRequestDTO.getProfileAddress() != null) {
            user.setPhone(profileUpdateRequestDTO.getProfileAddress());
        }

        // Save updated user information
        userRepository.save(user);
    }

    @Override
    public void storeAccessToken(String email, String accessToken) {
        accessTokenStore.put(email, accessToken);
    }

    @Override
    public boolean isValidAccessToken(String accessToken) {
        return accessTokenStore.containsValue(accessToken);
    }

    @Override
    public User authenticate(LoginRequestDTO loginRequestDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequestDTO.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (BCrypt.checkpw(loginRequestDTO.getPw(), user.getPw())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void removeAccessToken(String accessToken) {
        accessTokenStore.values().removeIf(token -> token.equals(accessToken));
    }

    @Override
    public String saveProfileImage(MultipartFile profile) {
        return s3ImageUploader.upload(profile);
    }


//    @Override
//    public void logout(String token) {
//        jwtUtil.invalidateToken(token);
//        log.info("토큰이 로그아웃되었습니다: {}", token);
//    }
}
