package com.bubble.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubble.dto.PostCreateDTO;
import com.bubble.entity.BubblePost;
import com.bubble.entity.PostComment;
import com.bubble.entity.PostLike;
import com.bubble.mapper.BubblePostMapper;
import com.bubble.mapper.PostCommentMapper;
import com.bubble.mapper.PostLikeMapper;
import com.bubble.service.BubblePostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BubblePostServiceImpl extends ServiceImpl<BubblePostMapper, BubblePost> implements BubblePostService {

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Override
    public BubblePost createPost(PostCreateDTO dto, Long userId) {
        BubblePost post = new BubblePost();
        BeanUtils.copyProperties(dto, post);
        post.setUserId(userId);
        save(post);
        return post;
    }

    @Override
    public IPage<BubblePost> getPostList(Long bubbleId, Page<BubblePost> page) {
        return getBaseMapper().selectPostsWithUser(page, bubbleId);
    }

    @Override
    @Transactional
    public void likePost(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
               .eq(PostLike::getUserId, userId);
        PostLike existLike = postLikeMapper.selectOne(wrapper);

        if (existLike != null) {
            postLikeMapper.deleteById(existLike.getId());
            BubblePost post = getById(postId);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            updateById(post);
        } else {
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            postLikeMapper.insert(like);

            BubblePost post = getById(postId);
            post.setLikeCount(post.getLikeCount() + 1);
            updateById(post);
        }
    }

    @Override
    public void commentPost(Long postId, Long userId, String content) {
        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        postCommentMapper.insert(comment);

        BubblePost post = getById(postId);
        post.setCommentCount(post.getCommentCount() + 1);
        updateById(post);
    }
}
