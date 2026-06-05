package com.bubble.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubble.common.result.Result;
import com.bubble.dto.PartnerPostCreateDTO;
import com.bubble.entity.PartnerPost;
import com.bubble.service.PartnerPostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/partner")
public class PartnerPostController {

    @Autowired
    private PartnerPostService partnerPostService;

    @PostMapping("/create")
    public Result<PartnerPost> createPost(@RequestBody PartnerPostCreateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PartnerPost post = partnerPostService.createPost(dto, userId);
        return Result.success(post);
    }

    @GetMapping("/list")
    public Result<IPage<PartnerPost>> getPostList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PartnerPost> pageParam = new Page<>(page, size);
        IPage<PartnerPost> result = partnerPostService.getPostList(pageParam);
        return Result.success(result);
    }
}
