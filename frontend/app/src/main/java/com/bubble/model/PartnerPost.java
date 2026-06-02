package com.bubble.model;

import com.google.gson.annotations.SerializedName;

public class PartnerPost {
    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("topicName")
    private String topicName;

    @SerializedName("address")
    private String address;

    @SerializedName("activityDate")
    private String activityDate;

    @SerializedName("partnerNumber")
    private Integer partnerNumber;

    @SerializedName("description")
    private String description;

    @SerializedName("wishTags")
    private String wishTags;

    @SerializedName("banTags")
    private String banTags;

    @SerializedName("status")
    private Integer status;

    @SerializedName("createTime")
    private String createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getActivityDate() { return activityDate; }
    public void setActivityDate(String activityDate) { this.activityDate = activityDate; }

    public Integer getPartnerNumber() { return partnerNumber; }
    public void setPartnerNumber(Integer partnerNumber) { this.partnerNumber = partnerNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWishTags() { return wishTags; }
    public void setWishTags(String wishTags) { this.wishTags = wishTags; }

    public String getBanTags() { return banTags; }
    public void setBanTags(String banTags) { this.banTags = banTags; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
