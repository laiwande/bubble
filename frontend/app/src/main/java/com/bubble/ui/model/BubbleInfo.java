package com.bubble.ui.model;

import java.io.Serializable;

public class BubbleInfo implements Serializable {
    private String name;
    private String content;
    private int cardBackgroundResId;

    public BubbleInfo(String name, String content, int cardBackgroundResId) {
        this.name = name;
        this.content = content;
        this.cardBackgroundResId = cardBackgroundResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCardBackgroundResId() {
        return cardBackgroundResId;
    }

    public void setCardBackgroundResId(int cardBackgroundResId) {
        this.cardBackgroundResId = cardBackgroundResId;
    }
}
