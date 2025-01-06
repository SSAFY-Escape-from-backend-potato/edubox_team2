package com.backend_potato.edubox_team2.domain.users.controller;

import com.backend_potato.edubox_team2.domain.users.entity.*;
import com.backend_potato.edubox_team2.domain.users.service.EmailService;
import com.backend_potato.edubox_team2.domain.users.service.UserService;
import com.backend_potato.edubox_team2.global.jwt.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import com.backend_potato.edubox_team2.global.jwt.JwtTokenUtil;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class UserRestControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UserService userService;
    @Mock
    private JwtFilter jwtFilter;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @InjectMocks
    private UserRestController userRestController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController).build();
        objectMapper = new ObjectMapper();
    }
    @Test
    void testUserSignup() throws Exception {
        // Mock input
        SignupRequestDTO signupRequestDTO = SignupRequestDTO.builder()
                .email("spancer1@naver.com")
                .pw("rlatngus@1")
                .confirmPw("rlatngus@1")
                .build();

        // Mock the service method
        Mockito.doNothing().when(userService).createUser(any(SignupRequestDTO.class));

        // Perform the request
        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void testVerifyLink() throws Exception {
        // Mock input
        String token = "mockToken";
        String expectedMessage = "Email successfully verified. You can now log in.";

        // Mock the service behavior
        Mockito.when(userService.verifyEmailLink(token)).thenReturn(expectedMessage);

        // Perform the request
        mockMvc.perform(get("/api/user/verify-link")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void testLogout() throws Exception {
        // Mock the service behavior
        String mockAccessToken = "mockAccessToken";
        Mockito.when(jwtFilter.resolveToken(any(HttpServletRequest.class))).thenReturn(mockAccessToken);
        Mockito.when(jwtTokenUtil.isValidToken(mockAccessToken)).thenReturn(true);

        // Mock service behavior
        Mockito.doNothing().when(userService).removeAccessToken(mockAccessToken);

        // Perform the logout request
        mockMvc.perform(post("/api/user/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
    }

    @Test
    void testLogin() throws Exception{
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder().email("spancer1@naver.com").pw("rlatngus@1").build();
        User user = User.builder().email("spancer1@naver.com").pw("$2a$10$ASpkxwQE92kmMr1Te/LiUemPvXQ5XNNhIK20NfEpKtNp3WAeGcV4a").role(Role.USER).build();

        Mockito.when(userService.authenticate(loginRequestDTO)).thenReturn(user);
        Mockito.when(jwtTokenUtil.generateAccessToken(eq(user), any(Long.class))).thenReturn("mockAccessToken");
        Mockito.when(jwtTokenUtil.generateRefreshToken(eq(user), any(Long.class))).thenReturn("mockRefreshToken");


        Mockito.when(userService.authenticate(loginRequestDTO)).thenReturn(user);
        Mockito.when(jwtTokenUtil.generateAccessToken(eq(user), any(Long.class))).thenReturn("mockAccessToken");
        Mockito.when(jwtTokenUtil.generateRefreshToken(eq(user), any(Long.class))).thenReturn("mockRefreshToken");

        // When & Then
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer mockAccessToken"));
    }

    @Test
    void testCheckEmailDuplicate() throws Exception{
        String email = "spancer1@naver.com";

        Mockito.when(userService.existsByEmail(email)).thenReturn(true);

        mockMvc.perform(get("/api/user/check-email")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        email = "spancer1947@gmail.com";

        Mockito.when(userService.existsByEmail(email)).thenReturn(false);

        mockMvc.perform(get("/api/user/check-email")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testCheckNicknameDuplicate() throws Exception{
        String nickname = "cwstiger";

        Mockito.when(userService.existsByNickname(nickname)).thenReturn(true);

        mockMvc.perform(get("/api/user/check-nickname")
                .param("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        nickname = "KSH00610";

        Mockito.when(userService.existsByNickname(nickname)).thenReturn(false);

        mockMvc.perform(get("/api/user/check-nickname")
                        .param("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testCheckProfileAddressDuplicate() throws Exception{
        String profileAddress = "IamPotato";

        Mockito.when(userService.existsByProfileAddress(profileAddress)).thenReturn(false);

        mockMvc.perform(get("/api/user/check-profileAddress")
                .param("profileAddress",profileAddress))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testUpdateProfile() throws Exception{
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder().email("spancer1@naver.com").pw("rlatngus@1").build();
        User user = User.builder().email("spancer1@naver.com").pw("$2a$10$ASpkxwQE92kmMr1Te/LiUemPvXQ5XNNhIK20NfEpKtNp3WAeGcV4a").role(Role.USER).build();

        Mockito.when(userService.authenticate(loginRequestDTO)).thenReturn(user);
        Mockito.when(jwtTokenUtil.generateAccessToken(eq(user), any(Long.class))).thenReturn("mockAccessToken");
        Mockito.when(jwtTokenUtil.generateRefreshToken(eq(user), any(Long.class))).thenReturn("mockRefreshToken");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer mockAccessToken"));

        ProfileUpdateRequestDTO profileUpdateRequestDTO = ProfileUpdateRequestDTO.builder()
                .nickname("NewNickname")
                .profileAddress("KSH")
                .discription("Hello")
                .build();
        Mockito.doNothing().when(userService).updateProfile(any(MultipartFile.class), eq(profileUpdateRequestDTO), any(HttpServletRequest.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String profileUpdateJson = objectMapper.writeValueAsString(profileUpdateRequestDTO);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "test-image-content".getBytes());
        MockMultipartFile requestPart = new MockMultipartFile("request", "request", "application/json", profileUpdateJson.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/update-profile")
                        .file(imageFile)
                        .file(requestPart)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .header("Authorization", "Bearer mockAccessToken"))
                .andExpect(status().isOk());
    }
}
