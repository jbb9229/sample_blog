package com.boong.boklog.request;

import lombok.Builder;
import lombok.Data;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Data
@Builder
public class PostSearch {

    private static final int MAX_SIZE = 2000;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 20;

    public long getOffset() {
        return (max(1, page) - 1) * min(size, MAX_SIZE);
    }

}
