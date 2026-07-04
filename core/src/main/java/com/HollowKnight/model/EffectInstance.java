package com.HollowKnight.model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A single in-flight visual effect (dash trail, slash arc, etc.).
 *
 * Lifecycle:
 *   1. Created by KnightEffectManager.trigger*() with a fixed world position.
 *   2. update(delta) ticks its local timer forward each frame.
 *   3. isFinished() returns true once the one-shot animation has played through.
 *   4. The manager removes finished instances so they stop being rendered.
 *
 * Effects are always one-shot (PlayMode.NORMAL) – they never loop.
 * Position is baked in at spawn time; effects do NOT track the Knight's movement.
 */
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
