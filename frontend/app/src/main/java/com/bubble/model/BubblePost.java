package com.bubble.model;

import com.google.gson.annotations.SerializedName;

public class BubblePost {
    @SerializedName("id")
    private Long id;

    @SerializedName("bubbleId")
    private Long bubbleId;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("content")
    private String content;

    @SerializedName("images")
    private String images;

    @SerializedName("likeCount")
    private Integer likeCount;

    @SerializedName("commentCount")
    private Integer commentCount;

    @SerializedName("createTime")
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBubbleId() {
        return bubbleId;
    }

    public void setBubbleId(Long bubbleId) {
        this.bubbleId = bubbleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
