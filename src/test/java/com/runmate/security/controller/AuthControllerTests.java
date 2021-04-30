package com.runmate.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.dto.AuthRequest;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTests {
    MockMvc mockMvc;
    ObjectMapper mapper=new ObjectMapper();

    @Autowired
    WebApplicationContext ctx;
    @Autowired
    JwtProvider provider;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        mockMvc= MockMvcBuilders.webAppContextSetup(ctx)
                .addFilter(new JwtAuthenticationFilter(provider))
                .build();
    }

    @Test
    public void joinAndLogin() throws Exception {
        User user=new User();
        user.setEmail("kyo@kyo.com");
        user.setPassword("1234");
        user.setRegion(new Region("si","gu","gun"));
        user.setUsername("kyo");

        String jsonBody=mapper.writeValueAsString(user);

        assertNotNull(mockMvc);
        mockMvc.perform(post("/api/auth/local/new")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk());

        AuthRequest authRequest=new AuthRequest();
        authRequest.setEmail("kyo@kyo.com");
        authRequest.setPassword("1234");

        jsonBody=mapper.writeValueAsString(authRequest);

        MvcResult mvcResult=mockMvc.perform(post("/api/auth/local/login")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization")).andReturn();

        String token=mvcResult.getResponse().getHeader("Authorization").replace("Bearer ","");
        assertEquals(jwtProvider.validate(token),true);
        assertEquals(jwtProvider.getClaim(token),user.getEmail());

        assertNotNull(userRepository.findByEmail(user.getEmail()));
    }
}
