package com.bubble.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubble.common.result.Result;
import com.bubble.dto.BubbleCreateDTO;
import com.bubble.entity.Bubble;
import com.bubble.entity.BubblePost;
import com.bubble.service.BubbleService;
import com.bubble.service.BubblePostService;
import com.bubble.vo.BubbleDetailVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bubble")
public class BubbleController {

    @Autowired
    private BubbleService bubbleService;

    @Autowired
    private BubblePostService bubblePostService;

    @PostMapping("/create")
    public Result<Bubble> createBubble(@RequestBody BubbleCreateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Bubble bubble = bubbleService.createBubble(dto, userId);
        return Result.success(bubble);
    }

    @GetMapping("/list")
    public Result<IPage<Bubble>> getBubbleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Boolean joined,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Bubble> pageParam = new Page<>(page, size);
        IPage<Bubble> result = bubbleService.getBubbleList(pageParam, userId, joined);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Bubble> getBubbleDetail(@PathVariable Long id) {
        Bubble bubble = bubbleService.getBubbleDetail(id);
        return Result.success(bubble);
    }

    @PostMapping("/{id}/join")
    public Result<Void> joinBubble(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        bubbleService.joinBubble(id, userId);
        return Result.success();
    }

    @PostMapping("/{id}/leave")
    public Result<Void> leaveBubble(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        bubbleService.leaveBubble(id, userId);
        return Result.success();
    }

    @GetMapping("/{id}/full")
    public Result<BubbleDetailVO> getFullDetail(@PathVariable Long id) {
        BubbleDetailVO vo = new BubbleDetailVO();
        vo.setBubble(bubbleService.getBubbleDetail(id));
        Page<BubblePost> postPage = new Page<>(1, 3);
        vo.setRecentPosts(bubblePostService.getPostList(id, postPage).getRecords());
        return Result.success(vo);
    }
}
