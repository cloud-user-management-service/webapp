package com.myweb.webapp.controller;

import org.glassfish.jaxb.core.api.impl.NameConverter.Standard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myweb.webapp.config.PasswordEncoderConfig;
import com.myweb.webapp.config.RestAuthenticationEntryPoint;
import com.myweb.webapp.config.SecurityConfig;
import com.myweb.webapp.dto.CustomUserDetails;
import com.myweb.webapp.dto.UserRequestDto;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.exceptions.HandleBadRequestException;
import com.myweb.webapp.repository.UserRepository;
import com.myweb.webapp.security.AuthenticationFilter;
import com.myweb.webapp.service.UserService;
import com.myweb.webapp.service.impl.MyUserDetailsService;
import com.myweb.webapp.service.impl.UserServiceImpl;

import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

import java.util.UUID;
import java.time.LocalDateTime;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Do not add the security filters
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordEncoder passwordEncoder;  // Mock the password encoder
   
    @MockBean
    private UserService userService; //Mock the user service

    @InjectMocks
    private UserController userController;

    private User mockUser;
    private CustomUserDetails mockUserDetails;
    private String base64Credentials;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setEmail("testuser@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        mockUser.setPassword("password123");
        mockUser.setAccountCreated(LocalDateTime.now());
        mockUser.setAccountUpdated(LocalDateTime.now());

        String username = mockUser.getEmail();
        String password = mockUser.getPassword();
        String auth = username + ":" + password;
        base64Credentials = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        mockUserDetails = new CustomUserDetails(mockUser,passwordEncoder.encode(mockUser.getPassword()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateUser() throws Exception {

        when(userService.createUser(any(Map.class))).thenReturn(mockUser);

        mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"testuser@example.com\", \"first_name\":\"Test\", \"last_name\":\"User\", \"password\":\"password123\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetUser_Unauthorized() throws Exception {

        mockMvc.perform(get("/v1/user/self"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetUser_Success() throws Exception {

        mockMvc.perform(get("/v1/user/self")
                        .requestAttr("userDetails", mockUserDetails) // Adding the mockUserDetails to mock authorized user
                        .header("Authorization", "Basic " + base64Credentials))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser_Success() throws Exception {

        mockMvc.perform(put("/v1/user/self")
                        .requestAttr("userDetails", mockUserDetails) // Adding the mockUserDetails to mock authorized user
                        .header("Authorization", "Basic " + base64Credentials)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first_name\":\"NewFirstName\", \"last_name\":\"NewLastName\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateUser_UnauthorizedField() throws Exception {

        mockMvc.perform(put("/v1/user/self")
                        .header("Authorization", "Basic " + base64Credentials)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first_name\":\"NewFirstName\", \"last_name\":\"NewLastName\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateUser_BadRequest() throws Exception {

        mockMvc.perform(put("/v1/user/self")
                        .requestAttr("userDetails", mockUserDetails) // Adding the mockUserDetails to mock authorized user
                        .header("Authorization", "Basic " + base64Credentials)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first_name\":\"NewFirstName\", \"last_name\":\"NewLastName\", \"email\":\"update@email\"}"))
                .andExpect(status().isBadRequest());
    }
}
