package com.boong.boklog.response;

import com.boong.boklog.domain.Post;
import lombok.Builder;
import lombok.Getter;

/**
 * Service 정책을 위한 class
 */
@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    @Builder
    public PostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title.substring(0, Math.min(title.length(), 10));
        this.content = content;
    }

}
