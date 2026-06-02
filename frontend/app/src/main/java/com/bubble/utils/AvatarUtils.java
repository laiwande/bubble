package com.bubble.utils;

/**
 * DiceBear 头像生成工具类
 * 提供多种可爱的随机头像风格
 */
public class AvatarUtils {

    private static final String BASE_URL = "https://api.dicebear.com/7.x/";
    private static final String STYLE = "adventurer"; // 可爱卡通风格，可选：fun-emoji, bottts, lorelei, pixel-art

    /**
     * 根据用户ID生成头像URL（同一用户始终返回相同头像）
     * @param seed 用户ID、邮箱或用户名（作为随机种子）
     * @return 头像URL
     */
    public static String getAvatarUrl(String seed) {
        if (seed == null || seed.trim().isEmpty()) {
            seed = String.valueOf(System.currentTimeMillis());
        }
        return BASE_URL + STYLE + "/png?seed=" + seed.replace(" ", "%20");
    }

    /**
     * 生成指定风格的头像URL
     * @param seed 用户标识
     * @param style 风格名称
     * @return 头像URL
     */
    public static String getAvatarUrl(String seed, String style) {
        if (seed == null || seed.trim().isEmpty()) {
            seed = String.valueOf(System.currentTimeMillis());
        }
        return BASE_URL + style + "/png?seed=" + seed.replace(" ", "%20");
    }

    /**
     * 获取完全随机的头像URL（每次调用不同）
     * @return 随机头像URL
     */
    public static String getRandomAvatarUrl() {
        String randomSeed = String.valueOf(System.currentTimeMillis() + Math.random() * 10000);
        return getAvatarUrl(randomSeed);
    }

    // 可用风格常量
    public static final String STYLE_FUN_EMOJI = "fun-emoji";
    public static final String STYLE_ADVENTURER = "adventurer";
    public static final String STYLE_BOTTS = "bottts";
    public static final String STYLE_LORELEI = "lorelei";
    public static final String STYLE_PIXEL_ART = "pixel-art";
    public static final String STYLE_NOTIONISTS = "notionists";
}
