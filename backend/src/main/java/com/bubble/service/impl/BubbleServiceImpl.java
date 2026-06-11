package com.bubble.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubble.dto.BubbleCreateDTO;
import com.bubble.entity.Bubble;
import com.bubble.entity.BubbleLabel;
import com.bubble.entity.BubbleMember;
import com.bubble.entity.Conversation;
import com.bubble.entity.Label;
import com.bubble.entity.Message;
import com.bubble.mapper.BubbleLabelMapper;
import com.bubble.mapper.BubbleMapper;
import com.bubble.mapper.BubbleMemberMapper;
import com.bubble.mapper.ConversationMapper;
import com.bubble.mapper.LabelMapper;
import com.bubble.mapper.MessageMapper;
import com.bubble.service.BubbleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BubbleServiceImpl extends ServiceImpl<BubbleMapper, Bubble> implements BubbleService {

    @Autowired
    private BubbleMemberMapper bubbleMemberMapper;

    @Autowired
    private BubbleLabelMapper bubbleLabelMapper;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Override
    @Transactional
    public Bubble createBubble(BubbleCreateDTO dto, Long userId) {
        // 1. 创建 Bubble 记录
        Bubble bubble = new Bubble();
        BeanUtils.copyProperties(dto, bubble);
        bubble.setCreatorId(userId);
        bubble.setCurrentMember(1);
        save(bubble);

        // 2. 添加创建者为 owner
        BubbleMember member = new BubbleMember();
        member.setBubbleId(bubble.getId());
        member.setUserId(userId);
        member.setRole("owner");
        bubbleMemberMapper.insert(member);

        // 3. 创建群聊会话
        Conversation conversation = new Conversation();
        conversation.setType("bubble");
        conversation.setTargetId(bubble.getId());
        conversationMapper.insert(conversation);

        // 4. 保存标签
        saveBubbleLabels(bubble.getId(), dto.getAllowTags(), "allow");
        saveBubbleLabels(bubble.getId(), dto.getBanTags(), "ban");
        saveBubbleLabels(bubble.getId(), dto.getBubbleLabelTags(), "label");

        return bubble;
    }

    /**
     * 保存 Bubble 标签（先查找或创建 Label，再创建关联）
     */
    private void saveBubbleLabels(Long bubbleId, List<String> tags, String type) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        for (String tagName : tags) {
            // 查找或创建 Label
            LambdaQueryWrapper<Label> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Label::getName, tagName);
            Label label = labelMapper.selectOne(wrapper);
            if (label == null) {
                label = new Label();
                label.setName(tagName);
                labelMapper.insert(label);
            }

            // 创建 Bubble-Label 关联
            BubbleLabel bubbleLabel = new BubbleLabel();
            bubbleLabel.setBubbleId(bubbleId);
            bubbleLabel.setLabelId(label.getId());
            bubbleLabel.setType(type);
            bubbleLabelMapper.insert(bubbleLabel);
        }
    }

    @Override
    public IPage<Bubble> getBubbleList(Page<Bubble> page) {
        return getBubbleList(page, null, null);
    }

    @Override
    public IPage<Bubble> getBubbleList(Page<Bubble> page, Long userId, Boolean joined) {
        LambdaQueryWrapper<Bubble> wrapper = new LambdaQueryWrapper<>();
        if (joined != null && userId != null) {
            LambdaQueryWrapper<BubbleMember> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.eq(BubbleMember::getUserId, userId);
            List<Long> joinedBubbleIds = bubbleMemberMapper.selectList(memberWrapper)
                    .stream()
                    .map(BubbleMember::getBubbleId)
                    .toList();

            if (joined) {
                if (joinedBubbleIds.isEmpty()) {
                    return page;
                }
                wrapper.in(Bubble::getId, joinedBubbleIds);
            } else if (!joinedBubbleIds.isEmpty()) {
                wrapper.notIn(Bubble::getId, joinedBubbleIds);
            }
        }
        wrapper.orderByDesc(Bubble::getCreateTime);
        IPage<Bubble> bubblePage = page(page, wrapper);

        // 为每个 Bubble 填充消息总数和最新一条消息内容
        for (Bubble bubble : bubblePage.getRecords()) {
            LambdaQueryWrapper<Conversation> convWrapper = new LambdaQueryWrapper<>();
            convWrapper.eq(Conversation::getType, "bubble")
                       .eq(Conversation::getTargetId, bubble.getId());
            Conversation conversation = conversationMapper.selectOne(convWrapper);
            if (conversation != null) {
                Integer count = messageMapper.countByConversationId(conversation.getId());
                bubble.setMessageCount(count != null ? count : 0);

                Message latestMsg = messageMapper.selectLatestByConversationId(conversation.getId());
                bubble.setLastMessage(latestMsg != null ? latestMsg.getContent() : null);
            } else {
                bubble.setMessageCount(0);
                bubble.setLastMessage(null);
            }
        }

        return bubblePage;
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
