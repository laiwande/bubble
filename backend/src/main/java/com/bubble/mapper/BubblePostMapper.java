package com.bubble.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubble.entity.BubblePost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BubblePostMapper extends BaseMapper<BubblePost> {

    @Select("SELECT p.*, u.nickname, u.avatar FROM bubble_post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.bubble_id = #{bubbleId} " +
            "ORDER BY p.create_time DESC")
    IPage<BubblePost> selectPostsWithUser(Page<BubblePost> page, @Param("bubbleId") Long bubbleId);
}
