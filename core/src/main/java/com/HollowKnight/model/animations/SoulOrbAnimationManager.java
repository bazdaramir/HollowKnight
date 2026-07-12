package com.HollowKnight.model.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

public class SoulOrbAnimationManager {
    private final Array<Texture> fillFrames;

    public SoulOrbAnimationManager() {
        fillFrames = new Array<>();
        fillFrames.add(new Texture(Gdx.files.internal("ui/HUD/HUD Cln_032.png")));

        for (int i = 237; i <= 254; i++) {
            fillFrames.add(new Texture(Gdx.files.internal("ui/HUD/HUD Cln_" + i + ".png")));
        }
        fillFrames.add(new Texture(Gdx.files.internal("ui/HUD/HUD Cln_100.png")));
    }

    public Texture getFrameByPercentage(float percentage) {
        if (fillFrames.isEmpty()) return null;
        percentage = MathUtils.clamp(percentage, 0f, 1f);
        int index = (int) (percentage * (fillFrames.size - 1));
        return fillFrames.get(index);
    }

    public void dispose() {
        for (Texture t : fillFrames) {
            t.dispose();
        }
        fillFrames.clear();
    }
}
