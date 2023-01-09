package com.boong.boklog.controller;

import com.boong.boklog.request.PostCreate;
import com.boong.boklog.request.PostSearch;
import com.boong.boklog.response.PostResponse;
import com.boong.boklog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

//    application/x-www-form-urlencoded
//    @PostMapping("/posts")
//    public String post(@RequestParam String title, @RequestParam String content) {
//        log.info("title={}, content={}", title, content);
//        return "Hello get";
//    }

//    @PostMapping("/posts")
//    public String post(@RequestParam Map<String, String> params) {
//        log.info("params={}", params);
//        return "Hello get";
//    }

//    @PostMapping("/posts")
//    public String post(@ModelAttribute PostCreate params) {
//        log.info("params={}", params.toString());
//        return "Hello get";
//    }

//    application-json
//    @PostMapping("/posts")
//    public Map<String, String> post(@RequestBody @Valid PostCreate params
////                                    ,BindingResult result
//    ) throws Exception {
//        log.info("params={}", params.toString());
//
////        var title = params.getTitle();
////        if (title == null || title.equals("")) {
////            // error
////            throw new Exception("타이틀 값 없음.");
////        }
////
////        var content = params.getContent();
////        if (content == null || title.equals("")) {
////            // error
////        }
////
////        if (result.hasErrors()) {
////            List<FieldError> filedErrors = result.getFieldErrors();
////            FieldError firstFieldError = filedErrors.get(0);
////            String fieldName = firstFieldError.getField();
////            String errorMsg = firstFieldError.getDefaultMessage();
////
////            Map<String, String> error = new HashMap<>();
////            error.put(fieldName, errorMsg);
////            return error;
////        }
//
//        return Map.of();
//    }

//    @PostMapping("/posts")
//    public Map<String, String> post(@RequestBody @Valid PostCreate request) {
//        log.info("params={}" , request.toString());
//
//        // Repository를 직접호출해서 생성하는 경우도 있지만 가능하다면 Service Layer를 만들어서 처리하는것을 추천
//        // repository.save(params)
//        postService.write(request);
//
//        return Map.of();
//    }

    // POST -> 200 or 201
    // Case1. 저장한 데이터 Entity -> response로 응답
//    @PostMapping("/posts")
//    public Post post(@RequestBody @Valid PostCreate request) {
//        log.info("params={}" , request.toString());
//        return postService.write(request);
//    }

    // Case2. 저장한 데이터의 Primary_id -> response로 응답
    // -> Client에서는 수신한 id를 글 조회 API를 통해서 데이터를 수신받음
//    @PostMapping("/posts")
//    public Map<String, Long> post(@RequestBody @Valid PostCreate request) {
//        log.info("params={}" , request.toString());
//        Long postId =  postService.write(request);
//        return Map.of("postId", postId);
//    }

    // Case3. 응답 필요 없음 -> 클라이언트에서 모든 POST(글) 데이터 Context를 관리함
    // Bad Case : 서버에서 먼저 fix 하는것은 옳지 않음 -> 서버에서는 차라리 유연한 대응이 좋음
    // 한번에 일괄적으로 잘 처리되는 케이스는 없음 -> 잘 관리하는 형태가 중요
    @PostMapping("/posts")
    public void post(@RequestBody @Valid PostCreate request) {
        log.info("params={}" , request.toString());
        postService.write(request);
    }

    /**
     * /posts -> 글 전체 조회 (검색 + 페이징)
     * /posts/{postId} -> 글 한개만 조회
     *
     * @return
     */
    @GetMapping("/posts")
    public List<PostResponse> getPosts(@ModelAttribute PostSearch postSearch) {
        return postService.getPosts(postSearch);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable(name = "postId") Long id) {
        PostResponse response = postService.get(id);
        return response;
    }

}
