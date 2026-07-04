package com.HollowKnight.model.animations;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleActor extends Actor {
    private ParticleEffect effect;

    public ParticleActor(ParticleEffect effect) {
        this.effect = effect;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        effect.update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        effect.draw(batch);
        if (effect.isComplete()) {
            effect.reset();
        }
    }
}
