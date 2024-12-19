package com.backend_potato.edubox_team2.domain.users.controller;

import com.backend_potato.edubox_team2.domain.users.entity.SignupRequestDTO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
public class UserRestControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;
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

}
