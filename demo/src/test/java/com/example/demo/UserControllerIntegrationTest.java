package com.example.demo;

import antlr.preprocessor.Preprocessor;
import com.example.demo.model.Member;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public class UserControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpMockMvcForRestDocs(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentationContextProvider){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @BeforeEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testAddUser() throws Exception {
        Member member = new Member();
        member.setUserId(100);
        member.setUsername("테스트");
        member.setPassword("1234");
        member.setPasswordCheck("1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("Users-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields ()
                        ));
    }

    @Test
    public void testGetUser() throws Exception {
        Member member = new Member();
        member.setUserId(100);
        member.setUsername("테스트");
        member.setPassword("1234");
        member.setPasswordCheck("1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", 100))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("테스트"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("1234"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        Member member = new Member();
        member.setUserId(100);
        member.setUsername("테스트");
        member.setPassword("1234");
        member.setPasswordCheck("1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        member.setUsername("변경");
        mockMvc.perform(MockMvcRequestBuilders.put("/user/{id}", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        member.setPassword("0000");
        mockMvc.perform(MockMvcRequestBuilders.put("/user/{id}", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteUser() throws Exception {
        Member member = new Member();
        member.setUserId(100);
        member.setUsername("테스트");
        member.setPassword("1234");
        member.setPasswordCheck("1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/user/{id}", 100))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", 100))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
