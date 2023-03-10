package com.boong.boklog.service;

import com.boong.boklog.domain.Post;
import com.boong.boklog.exception.PostNotFound;
import com.boong.boklog.repository.PostRepository;
import com.boong.boklog.request.PostCreate;
import com.boong.boklog.request.PostEdit;
import com.boong.boklog.request.PostSearch;
import com.boong.boklog.response.PostResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void test1() {
        // given
        PostCreate postCreate = PostCreate.builder()
                                          .title("제목입니다.")
                                          .content("내용입니다.")
                                          .build();
        // when
        postService.write(postCreate);

        // then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals(post.getTitle(), postCreate.getTitle());
        assertEquals(post.getContent(), postCreate.getContent());
    }

    @Test
    @DisplayName("글 한 페이지 조회")
    void test2() {
        // given
        List<Post> requestPosts = IntStream.range(1, 21)
                                           .mapToObj(i -> Post.builder()
                                                              .title("Title of - " + i)
                                                              .content("Content of - " + i)
                                                              .build())
                                           .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                                          .page(1)
                                          .build();

        // when
        List<PostResponse> posts = postService.getPosts(postSearch);

        // then
        assertEquals(20L, posts.size());
        assertEquals("Title of - 20", posts.get(0).getTitle());
    }

    @Test
    @DisplayName("글 한개 조회")
    void test3() {
        // given
        Post requestPost = Post.builder()
                .title("foobar1234567890")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        // when
        PostResponse postResponse = postService.get(requestPost.getId());

        // then
        assertNotNull(postResponse);
        assertEquals(1L, postRepository.count());
        assertEquals(postResponse.getTitle(), requestPost.getTitle().substring(0, 10));
        assertEquals(postResponse.getContent(), requestPost.getContent());
    }

    @Test
    @DisplayName("글 한개 조회시 조회할 글이 없는 경우")
    void test7() {
        // given
        Post requestPost = Post.builder()
                .title("foobar1234567890")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        // when
        assertThrows(PostNotFound.class, () -> postService.get(requestPost.getId() + 1L));
    }

    @Test
    @DisplayName("글 제목 수정")
    void test4() {
        // given
        Post post = Post.builder()
                        .title("boklog")
                        .content("nice")
                        .build();

        postRepository.save(post);

        PostEdit edit = PostEdit.builder()
                                .title("BOKLOG")
                                .build();

        // when
        postService.edit(post.getId(), edit);

        // then
        Post editedPost = postRepository.findById(post.getId()).orElseThrow(PostNotFound::new);
        assertEquals(editedPost.getTitle(), "BOKLOG");
        assertEquals(editedPost.getContent(), "nice");
    }

    @Test
    @DisplayName("글 내용 수정")
    void test5() {
        // given
        Post post = Post.builder()
                .title("boklog")
                .content("nice")
                .build();

        postRepository.save(post);

        PostEdit edit = PostEdit.builder()
                .content("NICE")
                .build();

        // when
        postService.edit(post.getId(), edit);

        // then
        Post editedPost = postRepository.findById(post.getId()).orElseThrow(PostNotFound::new);
        assertEquals(editedPost.getTitle(), "boklog");
        assertEquals(editedPost.getContent(), "NICE");
    }

    @Test
    @DisplayName("글 내용 수정 시 게시글이 없는 경우")
    void testPostUpdateNotFound() {
        // given
        Post post = Post.builder()
                .title("boklog")
                .content("nice")
                .build();

        postRepository.save(post);

        PostEdit edit = PostEdit.builder()
                .content("NICE")
                .build();

        // when
        assertThrows(PostNotFound.class, () -> postService.edit(post.getId() + 1L, edit));
    }

    @Test
    @DisplayName("게시글 삭제")
    void test6() {
        // given
        Post post = Post.builder()
                .title("boklog")
                .content("nice")
                .build();

        postRepository.save(post);

        // when
        postService.delete(post.getId());

        // then
        assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("게시글 삭제 시 삭제할 게시글이 존재하지 않는 경우")
    void testPostDeleteNotFound() {
        // given
        Post post = Post.builder()
                .title("boklog")
                .content("nice")
                .build();

        postRepository.save(post);

        // when
        assertThrows(PostNotFound.class, () -> postService.delete(post.getId() + 1L));
    }

}