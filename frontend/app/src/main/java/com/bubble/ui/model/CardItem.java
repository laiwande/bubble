package com.bubble.ui.model;

import java.util.List;

public class CardItem {
    private String title;
    private String subtitle;
    private List<TagItem> tags;
    private int avatarRightCount;

    public CardItem(String title, String subtitle, List<TagItem> tags, int avatarRightCount) {
        this.title = title;
        this.subtitle = subtitle;
        this.tags = tags;
        this.avatarRightCount = avatarRightCount;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public List<TagItem> getTags() { return tags; }
    public int getAvatarRightCount() { return avatarRightCount; }

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
