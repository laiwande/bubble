package com.bubble.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubble.common.result.Result;
import com.bubble.dto.PostCreateDTO;
import com.bubble.entity.BubblePost;
import com.bubble.service.BubblePostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class BubblePostController {

    @Autowired
    private BubblePostService bubblePostService;

    @PostMapping("/create")
    public Result<BubblePost> createPost(@RequestBody PostCreateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BubblePost post = bubblePostService.createPost(dto, userId);
        return Result.success(post);
    }

    @GetMapping("/list/{bubbleId}")
    public Result<IPage<BubblePost>> getPostList(
            @PathVariable Long bubbleId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<BubblePost> pageParam = new Page<>(page, size);
        IPage<BubblePost> result = bubblePostService.getPostList(bubbleId, pageParam);
        return Result.success(result);
    }

    @PostMapping("/{postId}/like")
    public Result<Void> likePost(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        bubblePostService.likePost(postId, userId);
        return Result.success();
    }

    @PostMapping("/{postId}/comment")
    public Result<Void> commentPost(
            @PathVariable Long postId,
            @RequestParam String content,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        bubblePostService.commentPost(postId, userId, content);
        return Result.success();
    }
}
