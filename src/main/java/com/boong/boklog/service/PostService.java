package com.boong.boklog.service;

import com.boong.boklog.domain.Post;
import com.boong.boklog.repository.PostRepository;
import com.boong.boklog.request.PostCreate;
import com.boong.boklog.request.PostEdit;
import com.boong.boklog.request.PostSearch;
import com.boong.boklog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post write(PostCreate postCreate) {
        // postCreate -> Entity
        Post post = Post.builder()
                        .title(postCreate.getTitle())
                        .content(postCreate.getContent())
                        .build();
        // repository.save(postCreate);
        return postRepository.save(post);
    }

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        return PostResponse.builder()
                           .id(post.getId())
                           .title(post.getTitle())
                           .content(post.getContent())
                           .build();
    }

    public List<PostResponse> getPosts(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                             .map(PostResponse::new)
                             .collect(Collectors.toList());
    }

    @Transactional
    public void edit(Long id, PostEdit edit) {
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 글입니다."));

        post.update(edit.getTitle(), edit.getContent());
    }
}
