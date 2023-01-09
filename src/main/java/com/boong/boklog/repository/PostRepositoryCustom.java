package com.boong.boklog.repository;

import com.boong.boklog.domain.Post;
import com.boong.boklog.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);

}
