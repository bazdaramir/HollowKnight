package com.HollowKnight.model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class EffectInstance {

    public final Animation<TextureRegion> animation;
    public final float x;
    public final float y;
    public final boolean flipX;

    private float timer = 0f;

    public EffectInstance(Animation<TextureRegion> animation,
                          float x, float y, boolean flipX) {
        this.animation = animation;
        this.x         = x;
        this.y         = y;
        this.flipX     = flipX;
    }

    public void update(float delta) {
        timer += delta;
    }

    public TextureRegion getFrame() {
        return animation.getKeyFrame(timer, false);
    }
    public boolean isFinished() {
        return animation.isAnimationFinished(timer);
    }
}
