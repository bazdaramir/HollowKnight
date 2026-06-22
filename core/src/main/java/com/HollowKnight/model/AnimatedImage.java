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
    private boolean isLeft;

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

            drawable.setRegion(animation.getKeyFrame(stateTime, false));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float offsetX = 0;

        if (animation.isAnimationFinished(stateTime)) {
            float timeSinceFinish = stateTime - animation.getAnimationDuration();


            float wave = MathUtils.sin(timeSinceFinish * 5f) * 3f;

            offsetX = isLeft ? wave : -wave;
        }

        float originalX = getX();
        setX(originalX + offsetX);

        super.draw(batch, parentAlpha);

        setX(originalX);
    }
}
