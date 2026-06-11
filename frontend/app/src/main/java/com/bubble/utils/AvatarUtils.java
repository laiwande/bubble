package com.bubble.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * DiceBear avatar URL helper.
 */
public class AvatarUtils {

    private static final String BASE_URL = "https://api.dicebear.com/7.x/";
    private static final String STYLE = "adventurer";
    private static final int[] LOCAL_COLORS = {
            Color.rgb(238, 132, 150),
            Color.rgb(91, 150, 224),
            Color.rgb(246, 179, 86),
            Color.rgb(116, 190, 157),
            Color.rgb(163, 128, 222),
            Color.rgb(235, 116, 100),
            Color.rgb(82, 178, 190),
            Color.rgb(232, 145, 203)
    };

    public static String getAvatarUrl(String seed) {
        return getAvatarUrl(seed, STYLE);
    }

    public static String getAvatarUrl(String seed, String style) {
        String safeSeed = seed;
        if (safeSeed == null || safeSeed.trim().isEmpty()) {
            safeSeed = String.valueOf(System.currentTimeMillis());
        }
        return BASE_URL + style + "/png?seed=" + Uri.encode(safeSeed.trim());
    }

    public static String getRandomAvatarUrl() {
        String randomSeed = String.valueOf(System.currentTimeMillis() + Math.random() * 10000);
        return getAvatarUrl(randomSeed);
    }

    public static void loadCuteAvatar(ImageView imageView, String seed) {
        String safeSeed = normalizeSeed(seed);
        Drawable fallback = createLocalAvatarDrawable(imageView.getContext(), safeSeed);
        Glide.with(imageView.getContext())
                .load(getAvatarUrl(safeSeed))
                .placeholder(fallback)
                .error(fallback)
                .circleCrop()
                .into(imageView);
    }

    private static String normalizeSeed(String seed) {
        if (seed == null || seed.trim().isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }
        return seed.trim();
    }

    public static Drawable createLocalAvatarDrawable(Context context, String seed) {
        int size = (int) (96 * context.getResources().getDisplayMetrics().density);
        if (size <= 0) {
            size = 96;
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        int hash = seed == null ? 0 : seed.hashCode();
        int color = LOCAL_COLORS[(hash & 0x7fffffff) % LOCAL_COLORS.length];

        float center = size / 2f;
        float radius = size / 2f;
        paint.setColor(color);
        canvas.drawCircle(center, center, radius, paint);

        paint.setColor(Color.argb(235, 255, 255, 255));
        canvas.drawCircle(center, size * 0.36f, size * 0.17f, paint);
        RectF body = new RectF(size * 0.22f, size * 0.56f, size * 0.78f, size * 0.98f);
        canvas.drawOval(body, paint);

        paint.setColor(Color.argb(95, 255, 255, 255));
        canvas.drawCircle(size * 0.72f, size * 0.24f, size * 0.08f, paint);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static final String STYLE_FUN_EMOJI = "fun-emoji";
    public static final String STYLE_ADVENTURER = "adventurer";
    public static final String STYLE_BOTTS = "bottts";
    public static final String STYLE_LORELEI = "lorelei";
    public static final String STYLE_PIXEL_ART = "pixel-art";
    public static final String STYLE_NOTIONISTS = "notionists";
}
