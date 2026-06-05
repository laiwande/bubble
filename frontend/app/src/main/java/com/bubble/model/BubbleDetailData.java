package com.bubble.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BubbleDetailData {
    @SerializedName("bubble")
    private Bubble bubble;

    @SerializedName("recentPosts")
    private List<BubblePost> recentPosts;

    public Bubble getBubble() {
        return bubble;
    }

    public void setBubble(Bubble bubble) {
        this.bubble = bubble;
    }

    public List<BubblePost> getRecentPosts() {
        return recentPosts;
    }

    public void setRecentPosts(List<BubblePost> recentPosts) {
        this.recentPosts = recentPosts;
    }
}
