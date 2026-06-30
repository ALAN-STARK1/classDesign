package com.example.indras.community.controller;

import com.example.indras.common.api.ApiResponse;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.context.UserContext;
import com.example.indras.community.dto.CommunityCommentCreateRequest;
import com.example.indras.community.dto.CommunityPostCreateRequest;
import com.example.indras.community.dto.CommunityPostFromAiRequest;
import com.example.indras.community.service.CommunityService;
import com.example.indras.community.vo.CommunityCommentVO;
import com.example.indras.community.vo.CommunityPostDetailVO;
import com.example.indras.community.vo.CommunityPostListItemVO;
import com.example.indras.community.vo.PostImageUploadVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/post-images")
    public ApiResponse<PostImageUploadVO> uploadPostImage(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(communityService.uploadPostImage(UserContext.requireUserId(), file));
    }

    @PostMapping("/posts")
    public ApiResponse<CommunityPostDetailVO> createPost(@Valid @RequestBody CommunityPostCreateRequest request) {
        return ApiResponse.success(communityService.createPost(UserContext.requireUserId(), request));
    }

    @PostMapping("/posts/from-ai-recipe/{aiRecipeId}")
    public ApiResponse<CommunityPostDetailVO> createPostFromAiRecipe(@PathVariable Long aiRecipeId,
                                                                     @Valid @RequestBody CommunityPostFromAiRequest request) {
        return ApiResponse.success(communityService.createPostFromAiRecipe(UserContext.requireUserId(), aiRecipeId, request));
    }

    @GetMapping("/posts")
    public ApiResponse<PageResult<CommunityPostListItemVO>> pagePosts(PageQuery query,
                                                                        @RequestParam(required = false) String status) {
        return ApiResponse.success(communityService.pagePosts(UserContext.requireUserId(), query, status));
    }

    @GetMapping("/posts/{id}")
    public ApiResponse<CommunityPostDetailVO> postDetail(@PathVariable Long id) {
        return ApiResponse.success(communityService.postDetail(UserContext.requireUserId(), id));
    }

    @PostMapping("/posts/{id}/comments")
    public ApiResponse<CommunityCommentVO> createComment(@PathVariable Long id,
                                                         @Valid @RequestBody CommunityCommentCreateRequest request) {
        return ApiResponse.success(communityService.createComment(UserContext.requireUserId(), id, request));
    }

    @PostMapping("/posts/{id}/like")
    public ApiResponse<Boolean> likePost(@PathVariable Long id) {
        return ApiResponse.success(communityService.likePost(UserContext.requireUserId(), id));
    }

    @PostMapping("/posts/{id}/favorite")
    public ApiResponse<Boolean> favoritePost(@PathVariable Long id) {
        return ApiResponse.success(communityService.favoritePost(UserContext.requireUserId(), id));
    }
}
