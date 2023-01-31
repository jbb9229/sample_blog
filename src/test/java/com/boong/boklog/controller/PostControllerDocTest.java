package com.boong.boklog.controller;

import com.boong.boklog.domain.Post;
import com.boong.boklog.repository.PostRepository;
import com.boong.boklog.request.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.boklog.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class PostControllerDocTest {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("게시글 단건 조회 문서화")
    void getPostDocumented() throws Exception {
        // given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        // expected
        this.mockMvc.perform(get("/posts/{postId}", 1L)
                                 .accept(APPLICATION_JSON))
                                 .andExpect(status().isOk())
                                 .andDo(document("post-inquiry", pathParameters(
                                            parameterWithName("postId").description("게시글의 ID")
                                         ),
                                         responseFields(
                                                 fieldWithPath("id").description("게시글의 ID"),
                                                 fieldWithPath("title").description("게시글의 제목"),
                                                 fieldWithPath("content").description("게시글의 내용")
                                         )
                                 ));
    }

    @Test
    @DisplayName("게시글 등록 문서화")
    void postPostsDocumented() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = mapper.writeValueAsString(request);

        // expected
        this.mockMvc.perform(post("/posts")
                                .contentType(APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-create",
                        requestFields(
                                fieldWithPath("title").description("게시글의 제목")
                                        .attributes(key("constraint").value("게시글에 바보를 입력하지 마세요.")),
                                fieldWithPath("content").description("게시글의 내용").optional()
                        )
                ));
    }

}
