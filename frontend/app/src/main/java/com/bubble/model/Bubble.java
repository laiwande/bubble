package com.bubble.model;

import com.google.gson.annotations.SerializedName;

public class Bubble {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("creatorId")
    private Long creatorId;

    @SerializedName("cardSkin")
    private String cardSkin;

    @SerializedName("ageMin")
    private Integer ageMin;

    @SerializedName("ageMax")
    private Integer ageMax;

    @SerializedName("genderRatio")
    private String genderRatio;

    @SerializedName("maxMember")
    private Integer maxMember;

    @SerializedName("currentMember")
    private Integer currentMember;

    @SerializedName("description")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCardSkin() {
        return cardSkin;
    }

    public void setCardSkin(String cardSkin) {
        this.cardSkin = cardSkin;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public Integer getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }

    public String getGenderRatio() {
        return genderRatio;
    }

    public void setGenderRatio(String genderRatio) {
        this.genderRatio = genderRatio;
    }

    public Integer getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(Integer maxMember) {
        this.maxMember = maxMember;
    }

    public Integer getCurrentMember() {
        return currentMember;
    }

    public void setCurrentMember(Integer currentMember) {
        this.currentMember = currentMember;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
