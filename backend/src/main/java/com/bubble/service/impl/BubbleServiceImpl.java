package com.bubble.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubble.dto.BubbleCreateDTO;
import com.bubble.entity.Bubble;
import com.bubble.entity.BubbleMember;
import com.bubble.mapper.BubbleMapper;
import com.bubble.mapper.BubbleMemberMapper;
import com.bubble.service.BubbleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BubbleServiceImpl extends ServiceImpl<BubbleMapper, Bubble> implements BubbleService {

    @Autowired
    private BubbleMemberMapper bubbleMemberMapper;

    @Override
    @Transactional
    public Bubble createBubble(BubbleCreateDTO dto, Long userId) {
        Bubble bubble = new Bubble();
        BeanUtils.copyProperties(dto, bubble);
        bubble.setCreatorId(userId);
        bubble.setCurrentMember(1);
        save(bubble);

        BubbleMember member = new BubbleMember();
        member.setBubbleId(bubble.getId());
        member.setUserId(userId);
        member.setRole("owner");
        bubbleMemberMapper.insert(member);

        return bubble;
    }

    @Override
    public IPage<Bubble> getBubbleList(Page<Bubble> page) {
        LambdaQueryWrapper<Bubble> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Bubble::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Bubble getBubbleDetail(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public void joinBubble(Long bubbleId, Long userId) {
        Bubble bubble = getById(bubbleId);
        if (bubble.getCurrentMember() >= bubble.getMaxMember()) {
            throw new RuntimeException("群组已满员");
        }

        LambdaQueryWrapper<BubbleMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BubbleMember::getBubbleId, bubbleId)
               .eq(BubbleMember::getUserId, userId);
        if (bubbleMemberMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("已加入该群组");
        }

        BubbleMember member = new BubbleMember();
        member.setBubbleId(bubbleId);
        member.setUserId(userId);
        member.setRole("member");
        bubbleMemberMapper.insert(member);

        bubble.setCurrentMember(bubble.getCurrentMember() + 1);
        updateById(bubble);
    }

    @Override
    @Transactional
    public void leaveBubble(Long bubbleId, Long userId) {
        LambdaQueryWrapper<BubbleMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BubbleMember::getBubbleId, bubbleId)
               .eq(BubbleMember::getUserId, userId);
        bubbleMemberMapper.delete(wrapper);

        Bubble bubble = getById(bubbleId);
        bubble.setCurrentMember(bubble.getCurrentMember() - 1);
        updateById(bubble);
    }
}
