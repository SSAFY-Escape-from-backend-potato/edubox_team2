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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
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
    private final RedisTemplate<String, String> redisTemplate;
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

        String encodedPassword = BCrypt.hashpw(signupRequestDTO.getPw(), BCrypt.gensalt());
        User user = User.builder()
                .email(signupRequestDTO.getEmail())
                .pw(encodedPassword)
                .nickname(signupRequestDTO.getEmail().split("@")[0])
                .role(Role.UNAUTH) // 초기 권한을 UNAUTH로 설정
                .build();
        userRepository.save(user);
        emailService.sendVerificationLink(signupRequestDTO.getEmail());
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
    public User authenticate(LoginRequestDTO loginRequestDTO) {
        Optional<User> optionalUser = userRepository.findActiveUserByEmail(loginRequestDTO.getEmail());
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
    @Transactional
    public String verifyEmailLink(String token) {
        String email = redisTemplate.opsForValue().get(token);
        if (email == null) {
            log.error("Invalid or expired token: {}", token);
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        if (user.getRole() != Role.UNAUTH) {
            throw new IllegalStateException("Email already verified.");
        }

        user.setRole(Role.USER);
        userRepository.save(user);
        redisTemplate.delete(token);

        log.info("Email verification successful for email: {}", email);
        return "Email successfully verified. You can now log in.";
    }

    @Transactional
    @Override
    public void softDeleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isDelete()) {
            throw new IllegalStateException("User is already soft-deleted.");
        }

        // Soft-Delete 처리
        user.setDelete(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void hardDeleteUsers() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
        List<User> usersToDelete = userRepository.findAllByIsDeleteTrueAndDeletedAtBefore(cutoffDate);
        userRepository.deleteAll(usersToDelete);
    }

    @Transactional
    @Override
    public void restoreUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.isDelete()) {
                throw new IllegalStateException("User is already active.");
            }
            user.setDelete(false);
            user.setDeletedAt(null); // 삭제 상태가 아니므로 null로 변경
            userRepository.save(user);
        }
    }

    @Transactional
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    @Transactional
    @Override
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public boolean existsByProfileAddress(String profileAddress) {
        return userRepository.existsByProfileAddress(profileAddress);
    }

    @Transactional
    @Override
    public void storeAccessToken(String email, String accessToken) {
        accessTokenStore.put(email, accessToken);
    }

    @Transactional
    @Override
    public boolean isValidAccessToken(String accessToken) {
        return accessTokenStore.containsValue(accessToken);
    }

}
