package com.boong.boklog.controller;

import com.boong.boklog.request.PostCreate;
import com.boong.boklog.request.PostEdit;
import com.boong.boklog.request.PostSearch;
import com.boong.boklog.response.PostResponse;
import com.boong.boklog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public void post(@RequestBody @Valid PostCreate request) {
        request.validate();
        postService.write(request);
    }

    @GetMapping("/posts")
    public List<PostResponse> getPosts(@ModelAttribute PostSearch postSearch) {
        return postService.getPosts(postSearch);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable(name = "postId") Long id) {
        PostResponse response = postService.get(id);
        return response;
    }

    @PatchMapping("/posts/{postId}")
    public PostResponse edit(@PathVariable(name = "postId") Long id, @RequestBody @Valid PostEdit postEdit) {
        return postService.edit(id, postEdit);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable(name = "postId") Long id) {
        postService.delete(id);
    }

}
