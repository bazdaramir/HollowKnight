package com.HollowKnight.model.animations;

import com.HollowKnight.model.enums.CrystalGuardianStation;
import com.HollowKnight.model.mob.CrystalGuardian;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;

public class CrystalGuardianAnimationManager {

    private final Animation<TextureRegion> idleAnim, shootAnim, runAnim;
    private final Animation<TextureRegion> deathLandAnim, deathAirAnim;

    private final Animation<TextureRegion> laserAnim;

    private final Array<Texture> ownedTextures = new Array<>();
    private final IdentityMap<CrystalGuardian, StateClock> clocks = new IdentityMap<>();

    private static class StateClock {
        CrystalGuardianStation previousStation = null;
        float stateStartTime = 0f;
    }

    public CrystalGuardianAnimationManager() {
        Array<TextureRegion> idle = new Array<>();
        for (int i = 0; i <= 4; i++) idle.add(load(String.format("ui/CrystalGuardian/Idle_%03d.png", i)));
        idleAnim = new Animation<>(0.15f, idle, Animation.PlayMode.LOOP);



        Array<TextureRegion> shoot = new Array<>();
        for (int i = 0; i <= 6; i++) shoot.add(load(String.format("ui/CrystalGuardian/Shoot_%03d.png", i)));
        shootAnim = new Animation<>(0.05f, shoot, Animation.PlayMode.NORMAL);



        Array<TextureRegion> run = new Array<>();
        for (int i = 0; i <= 5; i++) run.add(load(String.format("ui/CrystalGuardian/Run_%03d.png", i)));
        runAnim = new Animation<>(0.06f, run, Animation.PlayMode.LOOP);



        Array<TextureRegion> deathLand = new Array<>();
        for (int i = 0; i <= 2; i++) deathLand.add(load(String.format("ui/CrystalGuardian/Death Land_%03d.png", i)));
        deathLandAnim = new Animation<>(0.12f, deathLand, Animation.PlayMode.NORMAL);



        Array<TextureRegion> deathAir = new Array<>();
        for (int i = 0; i <= 2; i++) deathAir.add(load(String.format("ui/CrystalGuardian/Death Air_%03d.png", i)));
        deathAirAnim = new Animation<>(0.12f, deathAir, Animation.PlayMode.NORMAL);


        Texture laserTex = new Texture(Gdx.files.internal("ui/CrystalGuardian/CrystalLaser.png"));
        ownedTextures.add(laserTex);

        int laserFramesCount = 15;

        int frameWidth = laserTex.getWidth() / laserFramesCount;
        int frameHeight = laserTex.getHeight();

        TextureRegion[][] tmpLaser = TextureRegion.split(laserTex, frameWidth, frameHeight);
        Array<TextureRegion> laserFramesArray = new Array<>();
        for (int i = 0; i < laserFramesCount; i++) {
            laserFramesArray.add(tmpLaser[0][i]);
        }

        laserAnim = new Animation<>(0.04f, laserFramesArray, Animation.PlayMode.NORMAL);
    }


    public TextureRegion getLaserFrame(CrystalGuardian guardian, float globalStateTime) {
        StateClock clock = clocks.get(guardian);
        float t = (clock != null) ? (globalStateTime - clock.stateStartTime) : 0f;

        TextureRegion baseFrame = laserAnim.getKeyFrame(t, false);
        TextureRegion frame = new TextureRegion(baseFrame);

        if (guardian.isFacingRight && frame.isFlipX()) frame.flip(true, false);
        if (!guardian.isFacingRight && !frame.isFlipX()) frame.flip(true, false);

        return frame;
    }

    public TextureRegion getFrame(CrystalGuardian guardian, float globalStateTime) {
        CrystalGuardianStation station = guardian.getStation();

        StateClock clock = clocks.get(guardian);
        if (clock == null) {
            clock = new StateClock();
            clocks.put(guardian, clock);
        }
        if (station != clock.previousStation) {
            clock.stateStartTime = globalStateTime;
            clock.previousStation = station;
        }
        float t = globalStateTime - clock.stateStartTime;

        TextureRegion baseFrame;
        switch (station) {
            case SHOOT:      baseFrame = shootAnim.getKeyFrame(t, false);     break;
            case ENRAGED:    baseFrame = runAnim.getKeyFrame(t, true);        break;
            case DEATH_LAND: baseFrame = deathLandAnim.getKeyFrame(t, false); break;
            case DEATH_AIR:  baseFrame = deathAirAnim.getKeyFrame(t, false);  break;
            case IDLE:
            default:         baseFrame = idleAnim.getKeyFrame(t, true);       break;
        }

        TextureRegion frame = new TextureRegion(baseFrame);
        boolean shouldFaceRight = guardian.isFacingRight;

        if (shouldFaceRight && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (!shouldFaceRight && frame.isFlipX()) {
            frame.flip(true, false);
        }

        return frame;

    }

    public void forget(CrystalGuardian guardian) { clocks.remove(guardian); }

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

