package com.boong.boklog.controller;

import com.boong.boklog.domain.Post;
import com.boong.boklog.repository.PostRepository;
import com.boong.boklog.request.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void cleanRepository() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts 요청 시 Hello get을 출력한다.")
    void test() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                                       .title("제목입니다.")
                                       .content("내용입니다.")
                                       .build();
        String json = mapper.writeValueAsString(request);
        // post title
        // post content
        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청 시 title 값은 필수다.")
    void test2() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                                       .content("내용입니다.")
                                       .build();
        String json = mapper.writeValueAsString(request);

        // post title
        // post content
        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
//                        .content("{\"title\": \"\", \"content\": \"내용입니다.\"}")
//                        null 이여도 NotBlank가 동작할까? -> 정상 동작
                        .content(json)
                )
//                .andExpect(status().isOk())
                .andExpect(status().isBadRequest())
//                .andExpect(content().string("Hello get"))
//                .andExpect(jsonPath("$.title").value("타이틀을 입력해주세요."))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("타이틀을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청 시 DB에 값이 저장된다.")
    void test3() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                                       .title("제목입니다.")
                                       .content("내용입니다.")
                                       .build();
        String json = mapper.writeValueAsString(request);
        // when
        mockMvc.perform(post("/posts")
                                .contentType(APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());
        // then
        assertEquals(1L, postRepository.count());

        Post post = postRepository.findAll().get(0);
        assertEquals(request.getTitle(), post.getTitle());
        assertEquals(request.getContent(), post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test4() throws Exception {
        // given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(get("/posts/{postId}", post.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());
    }

//    @Test
//    @DisplayName("글 여러개 조회")
//    void test5() throws Exception {
//        // given
//        Post post = postRepository.save(Post.builder()
//                                            .title("foo1")
//                                            .content("bar1")
//                                            .build());
//
//        Post post2 = postRepository.save(Post.builder()
//                                             .title("foo2")
//                                             .content("bar2")
//                                             .build());
//
//        // expected
//        mockMvc.perform(get("/posts")
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()", Matchers.is(2)))
//                .andExpect(jsonPath("$[0].id").value(post.getId()))
//                .andExpect(jsonPath("$[0].title").value(post.getTitle()))
//                .andExpect(jsonPath("$[0].content").value(post.getContent()))
//                .andExpect(jsonPath("$[1].id").value(post2.getId()))
//                .andExpect(jsonPath("$[1].title").value(post2.getTitle()))
//                .andExpect(jsonPath("$[1].content").value(post2.getContent()))
//                .andDo(print());
//    }

    @Test
    @DisplayName("글 여러개 조회")
    void test5() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("Title of - " + i)
                        .content("Content of - " + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/posts?page=1&size=5")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(5)))
                .andExpect(jsonPath("$[0].title").value("Title of - 30"))
                .andExpect(jsonPath("$[0].content").value("Content of - 30"))
                .andDo(print());
    }

    @Test
    @DisplayName("페이지를 0으로 요청해도 1로 조회")
    void test6() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("Title of - " + i)
                        .content("Content of - " + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/posts?page=0&size=5")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(5)))
                .andExpect(jsonPath("$[0].title").value("Title of - 30"))
                .andExpect(jsonPath("$[0].content").value("Content of - 30"))
                .andDo(print());
    }

}