package com.HollowKnight.model.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AmbientAnimationManager {
    private Animation<TextureRegion> beeAnim;
    private Animation<TextureRegion> birdAnim;

    public AmbientAnimationManager() {
        Texture beeTex = new Texture(Gdx.files.internal("ui/HUD/bee_particle_anim.png"));
        TextureRegion[][] beeRegions = TextureRegion.split(beeTex, beeTex.getWidth() / 5, beeTex.getHeight());
        beeAnim = new Animation<>(0.1f, beeRegions[0]);
        beeAnim.setPlayMode(Animation.PlayMode.LOOP);

        Texture birdTex = new Texture(Gdx.files.internal("ui/HUD/glow_bug_01.png"));
        TextureRegion[][] birdRegions = TextureRegion.split(birdTex, birdTex.getWidth()/4, birdTex.getHeight());
        birdAnim = new Animation<>(0.1f, birdRegions[0]);
        birdAnim.setPlayMode(Animation.PlayMode.LOOP);
    }

    public TextureRegion getFrame(String type, float stateTime) {
        return type.equals("bee") ? beeAnim.getKeyFrame(stateTime): birdAnim.getKeyFrame(stateTime);
    }
}
