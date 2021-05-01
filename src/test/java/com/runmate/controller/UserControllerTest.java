package com.runmate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.dto.AuthRequest;
import com.runmate.domain.dto.user.UserModificationDto;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext ctx;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    UserRepository userRepository;
    User user;
    ObjectMapper mapper = new ObjectMapper();
    static String ADDRESS = "you@you.com";

    String token;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilter(new JwtAuthenticationFilter(jwtProvider))
                .addFilter(new CharacterEncodingFilter("UTF-8"))
                .build();

        user = userRepository.findByEmail(ADDRESS);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(user.getEmail());
        authRequest.setPassword(user.getPassword());

        String jsonBody = mapper.writeValueAsString(authRequest);

        MvcResult result = mockMvc.perform(post("/api/auth/local/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andReturn();

        token = result.getResponse().getHeader("Authorization").replace("Bearer ", "");
    }

    @Test
    public void When_Modify_UserDomain_Expect_Status_OK() throws Exception {
        String requestBody = "{\n" +
                "\t\"id\":1,\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"username\":\"you\",\n" +
                "\t\"region\":{\n" +
                "\t\t\"si\":\"seoul\",\n" +
                "\t\t\"gu\":\"nowon\",\n" +
                "\t\t\"gun\":null\n" +
                "\t},\n" +
                "\t\"introduction\":\"메일 뛰자!\"\n" +
                "}";

        mockMvc.perform(put("/api/users/" + user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void When_Modify_InvalidUserDomain_Expect_Status_ClientError_Body_ErrorMessage() throws Exception {
        String invalidRequestBody = "{\n" +
                "\t\"id\":1,\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"region\":null,\n" +
                "\t\"introduction\":\"메일 뛰자!\"\n" +
                "}";

        mockMvc.perform(put("/api/users/" + user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBody)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void When_Get_User_Expect_Status_OK_Body_UserJson() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(user.getEmail());
        authRequest.setPassword(user.getPassword());

        String jsonBody = mapper.writeValueAsString(authRequest);

        MvcResult result = mockMvc.perform(post("/api/auth/local/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getHeader("Authorization").replace("Bearer ", "");

        mockMvc.perform(get("/api/users/" + user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(notNullValue())))
                .andExpect(jsonPath("$.error", is(nullValue())));
    }
}