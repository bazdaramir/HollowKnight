package com.HollowKnight.model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AnimatedImage extends Image {
    private Animation<TextureRegion> animation;
    private float stateTime = 0;
    private TextureRegionDrawable drawable;
    private boolean isLeft; // برای تشخیص اینکه پوینتر سمت چپ دکمه است یا راست

    public AnimatedImage(Animation<TextureRegion> animation, boolean isLeft) {
        this.animation = animation;
        this.isLeft = isLeft;
        this.drawable = new TextureRegionDrawable(animation.getKeyFrame(0));
        setDrawable(drawable);
    }

    public void resetAnimation() {
        stateTime = 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isVisible()) {
            stateTime += delta;

            // تغییر مهم: پارامتر دوم را false کردیم تا انیمیشن فقط یک بار پخش شود
            drawable.setRegion(animation.getKeyFrame(stateTime, false));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float offsetX = 0;

        // بررسی می‌کنیم که آیا انیمیشن ورود تمام شده است یا نه
        if (animation.isAnimationFinished(stateTime)) {
            // محاسبه زمانی که از اتمام انیمیشن گذشته است
            float timeSinceFinish = stateTime - animation.getAnimationDuration();

            // تولید یک موج سینوسی نرم (عدد 5f سرعت نوسان و 3f دامنه جابجایی بر حسب پیکسل است)
            // چون از 0 شروع می‌شود، هیچ پرش یا قطعی‌ای در انیمیشن حس نخواهید کرد
            float wave = MathUtils.sin(timeSinceFinish * 5f) * 3f;

            // قرینه کردن حرکت برای پوینترهای چپ و راست (تا با هم به سمت متن حرکت کنند)
            offsetX = isLeft ? wave : -wave;
        }

        // ذخیره موقعیت اصلی
        float originalX = getX();

        // جابجا کردن موقت فقط برای زمان رسم (Draw) تا با Table تداخل نکند
        setX(originalX + offsetX);

        // رسم تصویر
        super.draw(batch, parentAlpha);

        // برگرداندن به موقعیت اصلی
        setX(originalX);
    }
}
