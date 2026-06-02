package com.bubble.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubble.dto.PartnerPostCreateDTO;
import com.bubble.entity.PartnerPost;
import com.bubble.mapper.PartnerPostMapper;
import com.bubble.service.PartnerPostService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class PartnerPostServiceImpl extends ServiceImpl<PartnerPostMapper, PartnerPost> implements PartnerPostService {

    @Override
    public PartnerPost createPost(PartnerPostCreateDTO dto, Long userId) {
        PartnerPost post = new PartnerPost();
        BeanUtils.copyProperties(dto, post);
        post.setUserId(userId);
        if (post.getPartnerNumber() == null) {
            post.setPartnerNumber(0);
        }
        post.setStatus(1); // 默认进行中
        save(post);
        return post;
    }
}
