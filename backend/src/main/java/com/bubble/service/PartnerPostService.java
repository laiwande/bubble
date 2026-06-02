package com.bubble.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubble.dto.PartnerPostCreateDTO;
import com.bubble.entity.PartnerPost;

public interface PartnerPostService extends IService<PartnerPost> {
    PartnerPost createPost(PartnerPostCreateDTO dto, Long userId);
}
