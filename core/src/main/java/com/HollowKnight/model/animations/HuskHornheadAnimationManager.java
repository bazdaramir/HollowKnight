package com.HollowKnight.model.animations;

import com.HollowKnight.model.enums.HuskHornheadStation;
import com.HollowKnight.model.mob.HuskHornhead;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;

public class HuskHornheadAnimationManager {

    private final Animation<TextureRegion> idleAnim;
    private final Animation<TextureRegion> walkAnim;
    private final Animation<TextureRegion> turnAnim;
    private final Animation<TextureRegion> anticipateAnim;
    private final Animation<TextureRegion> lungeAnim;
    private final Animation<TextureRegion> cooldownAnim;
    private final Animation<TextureRegion> deathLandAnim;
    private final Animation<TextureRegion> deathAirAnim;

    private final Array<Texture> ownedTextures = new Array<>();
    private final IdentityMap<HuskHornhead, StateClock> clocks = new IdentityMap<>();

    private static class StateClock {
        HuskHornheadStation previousStation = null;
        float stateStartTime = 0f;
    }

    public HuskHornheadAnimationManager() {
        Array<TextureRegion> idle = new Array<>();
        for (int i = 0; i <= 5; i++) idle.add(load(String.format("ui/HuskHornhead/Idle_%03d.png", i)));
        idleAnim = new Animation<>(0.12f, idle, Animation.PlayMode.LOOP);



        Array<TextureRegion> walk = new Array<>();
        for (int i = 0; i <= 6; i++) walk.add(load(String.format("ui/HuskHornhead/Walk_%03d.png", i)));
        walkAnim = new Animation<>(0.1f, walk, Animation.PlayMode.LOOP);



        Array<TextureRegion> turn = new Array<>();
        for (int i = 0; i <= 1; i++) turn.add(load(String.format("ui/HuskHornhead/Turn_%03d.png", i)));
        turnAnim = new Animation<>(0.08f, turn, Animation.PlayMode.NORMAL);



        Array<TextureRegion> anticipate = new Array<>();
        for (int i = 0; i <= 4; i++) anticipate.add(load(String.format("ui/HuskHornhead/Attack Anticipate_%03d.png", i)));
        anticipateAnim = new Animation<>(0.1f, anticipate, Animation.PlayMode.NORMAL);



        Array<TextureRegion> lunge = new Array<>();
        for (int i = 0; i <= 11; i++) lunge.add(load(String.format("ui/HuskHornhead/Attack Lunge_%03d.png", i)));
        lungeAnim = new Animation<>(0.03f, lunge, Animation.PlayMode.LOOP);



        Array<TextureRegion> cooldown = new Array<>();
        cooldown.add(load("ui/HuskHornhead/Attack Cooldown.png"));
        cooldownAnim = new Animation<>(0.1f, cooldown, Animation.PlayMode.NORMAL);



        Array<TextureRegion> deathLand = new Array<>();
        for (int i = 0; i <= 7; i++) deathLand.add(load(String.format("ui/HuskHornhead/Death Land_%03d.png", i)));
        deathLandAnim = new Animation<>(0.08f, deathLand, Animation.PlayMode.NORMAL);



        Array<TextureRegion> deathAir = new Array<>();
        deathAir.add(load("ui/HuskHornhead/Death Air.png"));
        deathAirAnim = new Animation<>(0.1f, deathAir, Animation.PlayMode.NORMAL);

    }

    public TextureRegion getFrame(HuskHornhead husk, float globalStateTime) {
        HuskHornheadStation station = husk.getStation();

        StateClock clock = clocks.get(husk);
        if (clock == null) {
            clock = new StateClock();
            clocks.put(husk, clock);
        }
        if (station != clock.previousStation) {
            clock.stateStartTime = globalStateTime;
            clock.previousStation = station;
        }
        float t = globalStateTime - clock.stateStartTime;

        TextureRegion baseFrame;
        switch (station) {
            case ANTICIPATE: baseFrame = anticipateAnim.getKeyFrame(t, false); break;
            case LUNGE:      baseFrame = lungeAnim.getKeyFrame(t, true);       break;
            case COOLDOWN:   baseFrame = cooldownAnim.getKeyFrame(t, false);   break;
            case TURN:       baseFrame = turnAnim.getKeyFrame(t, false);       break;
            case DEATH_LAND: baseFrame = deathLandAnim.getKeyFrame(t, false);  break;
            case DEATH_AIR:  baseFrame = deathAirAnim.getKeyFrame(t, false);   break;
            case IDLE:       baseFrame = idleAnim.getKeyFrame(t, true);        break;
            case WALK:
            default:         baseFrame = walkAnim.getKeyFrame(t, true);        break;
        }

        TextureRegion finalFrame = new TextureRegion(baseFrame);
        boolean shouldFaceRight = husk.isFacingRight;

        if (shouldFaceRight && !finalFrame.isFlipX()) {
            finalFrame.flip(true, false);
        } else if (!shouldFaceRight && finalFrame.isFlipX()) {
            finalFrame.flip(true, false);
        }

        return finalFrame;
    }

    public void forget(HuskHornhead husk) { clocks.remove(husk); }

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
