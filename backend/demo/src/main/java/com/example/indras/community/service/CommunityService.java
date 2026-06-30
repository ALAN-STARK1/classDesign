package com.example.indras.community.service;

import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.community.dto.CommunityCommentCreateRequest;
import com.example.indras.community.dto.CommunityPostCreateRequest;
import com.example.indras.community.dto.CommunityPostFromAiRequest;
import com.example.indras.community.vo.CommunityCommentVO;
import com.example.indras.community.vo.CommunityPostDetailVO;
import com.example.indras.community.vo.CommunityPostListItemVO;
import com.example.indras.community.vo.PostImageUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface CommunityService {

    PostImageUploadVO uploadPostImage(Long userId, MultipartFile file);

    CommunityPostDetailVO createPost(Long userId, CommunityPostCreateRequest request);

    CommunityPostDetailVO createPostFromAiRecipe(Long userId, Long aiRecipeId, CommunityPostFromAiRequest request);

    PageResult<CommunityPostListItemVO> pagePosts(Long userId, PageQuery query, String status);

    CommunityPostDetailVO postDetail(Long userId, Long id);

    CommunityCommentVO createComment(Long userId, Long postId, CommunityCommentCreateRequest request);

    boolean likePost(Long userId, Long postId);

    boolean favoritePost(Long userId, Long postId);
}
