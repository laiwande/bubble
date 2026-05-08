package com.bubble.ui.model;

import java.util.ArrayList;
import java.util.List;

public class CardItem {
    private String title;
    private String subtitle;
    private List<TagItem> tags;
    private int avatarRightCount;
    
    // 详情页字段
    private String year;
    private String month;
    private String day;
    private String location;
    private String partnerNumber;
    private String description;
    private List<String> wishTags;
    private List<String> banTags;

    // 构造函数（列表显示用）
    public CardItem(String title, String subtitle, List<TagItem> tags, int avatarRightCount) {
        this.title = title;
        this.subtitle = subtitle;
        this.tags = tags;
        this.avatarRightCount = avatarRightCount;
        this.wishTags = new ArrayList<>();
        this.banTags = new ArrayList<>();
    }

    // 完整构造函数（带详情页数据）
    public CardItem(String title, String subtitle, List<TagItem> tags, int avatarRightCount,
                    String year, String month, String day, String location, 
                    String partnerNumber, String description,
                    List<String> wishTags, List<String> banTags) {
        this.title = title;
        this.subtitle = subtitle;
        this.tags = tags;
        this.avatarRightCount = avatarRightCount;
        this.year = year;
        this.month = month;
        this.day = day;
        this.location = location;
        this.partnerNumber = partnerNumber;
        this.description = description;
        this.wishTags = wishTags != null ? wishTags : new ArrayList<>();
        this.banTags = banTags != null ? banTags : new ArrayList<>();
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public List<TagItem> getTags() { return tags; }
    public int getAvatarRightCount() { return avatarRightCount; }
    
    // 详情页 getter
    public String getYear() { return year != null ? year : ""; }
    public String getMonth() { return month != null ? month : ""; }
    public String getDay() { return day != null ? day : ""; }
    public String getLocation() { return location != null ? location : ""; }
    public String getPartnerNumber() { return partnerNumber != null ? partnerNumber : "1"; }
    public String getDescription() { return description != null ? description : ""; }
    public List<String> getWishTags() { return wishTags; }
    public List<String> getBanTags() { return banTags; }

    public static class TagItem {
        private String text;
        private boolean isCheck;

        public TagItem(String text, boolean isCheck) {
            this.text = text;
            this.isCheck = isCheck;
        }

        public String getText() { return text; }
        public boolean isCheck() { return isCheck; }
    }
}
