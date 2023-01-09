package com.boong.boklog.repository;

import com.boong.boklog.domain.Post;
import com.boong.boklog.domain.QPost;
import com.boong.boklog.request.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.boong.boklog.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PostSearch postSearch) {
        return jpaQueryFactory.selectFrom(post)
                              .limit(postSearch.getSize())
                              .offset(postSearch.getOffset())
                              .orderBy(post.id.desc())
                              .fetch();
    }
}
