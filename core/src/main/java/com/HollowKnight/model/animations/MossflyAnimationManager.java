package com.HollowKnight.model.animations;

import com.HollowKnight.model.enums.MossflyStation;
import com.HollowKnight.model.mob.Mossfly;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;

public class MossflyAnimationManager {

    private final Animation<TextureRegion> shakeAnim, appearAnim, turnToFlyAnim, flyAnim;
    private final Animation<TextureRegion> deathLandAnim, deathAirAnim;

    private final IdentityMap<Mossfly, StateClock> clocks = new IdentityMap<>();
    private final Array<Texture> ownedTextures = new Array<>();

    private static class StateClock {
        MossflyStation previousStation = null;
        float stateStartTime = 0f;
    }

    public MossflyAnimationManager() {


        Array<TextureRegion> shake = new Array<>();
        for (int i = 0; i <= 2; i++) shake.add(load(String.format("ui/Mossfly/Shake_%03d.png", i)));
        shakeAnim = new Animation<>(0.15f, shake, Animation.PlayMode.LOOP);



        Array<TextureRegion> appear = new Array<>();
        for (int i = 0; i <= 5; i++) appear.add(load(String.format("ui/Mossfly/Appear_%03d.png", i)));
        appearAnim = new Animation<>(0.1f, appear, Animation.PlayMode.NORMAL);



        Array<TextureRegion> turnToFly = new Array<>();
        for (int i = 0; i <= 2; i++) turnToFly.add(load(String.format("ui/Mossfly/TurnToFly_%03d.png", i)));
        turnToFlyAnim = new Animation<>(0.1f, turnToFly, Animation.PlayMode.NORMAL);



        Array<TextureRegion> fly = new Array<>();
        for (int i = 0; i <= 3; i++) fly.add(load(String.format("ui/Mossfly/Fly_%03d.png", i)));
        flyAnim = new Animation<>(0.08f, fly, Animation.PlayMode.LOOP);



        Array<TextureRegion> deathLand = new Array<>();
        for (int i = 0; i <= 3; i++) deathLand.add(load(String.format("ui/Mossfly/Death Land_%03d.png", i)));
        deathLandAnim = new Animation<>(0.12f, deathLand, Animation.PlayMode.NORMAL);



        Array<TextureRegion> deathAir = new Array<>();
        for (int i = 0; i <= 3; i++) deathAir.add(load(String.format("ui/Mossfly/Death Air_%03d.png", i)));
        deathAirAnim = new Animation<>(0.12f, deathAir, Animation.PlayMode.NORMAL);
    }

    public TextureRegion getFrame(Mossfly mossfly, float globalStateTime) {
        MossflyStation station = mossfly.getStation();

        StateClock clock = clocks.get(mossfly);
        if (clock == null) {
            clock = new StateClock();
            clocks.put(mossfly, clock);
        }
        if (station != clock.previousStation) {
            clock.stateStartTime = globalStateTime;
            clock.previousStation = station;
        }
        float t = globalStateTime - clock.stateStartTime;

        TextureRegion masterFrame;
        switch (station) {
            case APPEAR:      masterFrame = appearAnim.getKeyFrame(t, false);     break;
            case TURN_TO_FLY: masterFrame = turnToFlyAnim.getKeyFrame(t, false);  break;
            case FLY:         masterFrame = flyAnim.getKeyFrame(t, true);         break;
            case DEATH_LAND:  masterFrame = deathLandAnim.getKeyFrame(t, false);  break;
            case DEATH_AIR:   masterFrame = deathAirAnim.getKeyFrame(t, false);   break;
            case SHAKE:
            default:          masterFrame = shakeAnim.getKeyFrame(t, true);       break;
        }


        TextureRegion finalFrame = new TextureRegion(masterFrame);
        boolean shouldFaceRight = mossfly.isFacingRight;


        if (station == MossflyStation.SHAKE || station == MossflyStation.APPEAR) {
            return finalFrame;
        }

        if (shouldFaceRight && !finalFrame.isFlipX()) {
            finalFrame.flip(true, false);
        } else if (!shouldFaceRight && finalFrame.isFlipX()) {
            finalFrame.flip(true, false);
        }

        return finalFrame;
    }

    public void forget(Mossfly mossfly) { clocks.remove(mossfly); }

    public void dispose() {
        for (Texture t : ownedTextures) t.dispose();
        ownedTextures.clear();
    }

    private TextureRegion load(String path) {
        Texture tex = new Texture(Gdx.files.internal(path));
        ownedTextures.add(tex);
        return new TextureRegion(tex);
    }
}
