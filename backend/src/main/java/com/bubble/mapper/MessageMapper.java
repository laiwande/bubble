package com.bubble.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubble.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Select("SELECT m.*, u.nickname, u.avatar FROM message m " +
            "LEFT JOIN user u ON m.sender_id = u.id " +
            "WHERE m.conversation_id = #{conversationId} " +
            "ORDER BY m.create_time DESC")
    IPage<Message> selectMessagesWithSender(Page<Message> page, @Param("conversationId") Long conversationId);

    @Select("SELECT COUNT(*) FROM message mr " +
            "WHERE mr.conversation_id = #{conversationId} " +
            "AND mr.id > (SELECT MAX(mr2.message_id) FROM message_read mr2 " +
            "WHERE mr2.user_id = #{userId}) " +
            "AND mr.sender_id != #{userId}")
    Integer countUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
}
