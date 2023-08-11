package com.example.demo.controller;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.security.test.context.support.WithMockUser;


import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@Transactional
class UserControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpMockMvcForRestDocs(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentationContextProvider){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @DisplayName("로그인")
    public void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("테스트");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andDo(document("Users-login",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields (
                                fieldWithPath("userName").description("이름").type(JsonFieldType.STRING)
                        )
                ))
                .andDo(print());
    }

    @Test
    @DisplayName("유저 가입 테스트")
    public void testAddUser() throws Exception {
        Member member = new Member();
        member.setUserId(100);
        member.setUsername("테스트");
        member.setPassword("1234");
        member.setPasswordCheck("1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andDo(document("Users-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields (
                                fieldWithPath("userId").description("아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("username").description("이름").type(JsonFieldType.STRING),
                                fieldWithPath("password").description("패스워드").type(JsonFieldType.STRING),
                                fieldWithPath("passwordCheck").description("패스워드 체크").type(JsonFieldType.STRING)
                        )
                        ))
                .andDo(print());
    }

    @Test
    @DisplayName("유저 조회 테스트")
    public void testGetUser() throws Exception {
        Member member = new Member();
        member.setUserId(100);
        member.setUsername("테스트");
        member.setPassword("1234");
        member.setPasswordCheck("1234");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/user/{id}", 100))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("테스트"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("1234"))
                .andDo(document("Users-findById",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("조회할 유저 아이디")
                        ),
                        responseFields(
                                fieldWithPath("userId").description("아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("username").description("이름").type(JsonFieldType.STRING),
                                fieldWithPath("password").description("패스워드").type(JsonFieldType.STRING),
                                fieldWithPath("passwordCheck").description("패스워드 체크").type(JsonFieldType.STRING)
                        )))
                .andDo(print());
    }

    @Test
    @DisplayName("유저 변경 테스트")
    public void testUpdateUser() throws Exception {
        Member member = new Member();
        member.setUserId(100);
        member.setUsername("테스트");
        member.setPassword("1234");
        member.setPasswordCheck("1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());

        member.setUsername("변경");
        mockMvc.perform(RestDocumentationRequestBuilders.put("/user/{id}", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andDo(document("Users-update",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("이름을 변경할 유저 아이디")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("username").description("변경된 이름").type(JsonFieldType.STRING),
                                fieldWithPath("password").description("패스워드").type(JsonFieldType.STRING),
                                fieldWithPath("passwordCheck").description("패스워드 체크").type(JsonFieldType.STRING)
                        )))
                .andDo(print());
    }

    @Test
    @DisplayName("유저 삭제 테스트")
    public void testDeleteUser() throws Exception {
        Member member = new Member();
        member.setUserId(100);
        member.setUsername("테스트");
        member.setPassword("1234");
        member.setPasswordCheck("1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/user/{id}", 100))
                .andExpect(status().isOk())
                .andDo(document("Users-delete",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("삭제할 아이디")
                        )
                        ))
                .andDo(print());

        mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", 100))
                .andExpect(status().isNotFound());
    }
}
