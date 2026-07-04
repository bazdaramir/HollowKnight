package com.HollowKnight.model.animations;

import com.HollowKnight.model.Knight;
import com.HollowKnight.model.enums.KnightState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;



public class KnightAnimationManager {

    private Animation<TextureRegion> runAnim;
    private Animation<TextureRegion> airborneAnim;
    private Animation<TextureRegion> dashAnim;
    private Animation<TextureRegion> doubleJumpAnim;
    private Animation<TextureRegion> wallSlideAnim;
    private Animation<TextureRegion> wallJumpAnim;
    private Animation<TextureRegion> landingAnim;
    private Animation<TextureRegion> slashAnim;
    private Animation<TextureRegion>            idleFrame;

    private Animation<TextureRegion> idleHurtAnim;
    private Animation<TextureRegion> deathAnim;

    private KnightState previousState  = null;
    private float       stateStartTime = 0f;



    public KnightAnimationManager() {

        Array<TextureRegion> runFrames = new Array<>();
        for (int i = 3; i <= 12; i++) {
            runFrames.add(load(String.format("ui/Knight/run/Run_%03d.png", i)));
        }
        runAnim = new Animation<>(0.05f, runFrames, Animation.PlayMode.LOOP);



        Array<TextureRegion> airFrames = new Array<>();
        for (int i = 0; i <= 11; i++) {
            airFrames.add(load(String.format("ui/Knight/airborne/airborne_%03d.png", i)));
        }
        airborneAnim = new Animation<>(0.05f, airFrames, Animation.PlayMode.NORMAL);



        Array<TextureRegion> dashFrames = new Array<>();
        for (int i = 0; i <= 11; i++) {
            dashFrames.add(load(String.format("ui/Knight/dash/dash_%03d.png", i)));
        }
        dashAnim = new Animation<>(0.06f, dashFrames, Animation.PlayMode.NORMAL);



        Array<TextureRegion> djFrames = new Array<>();
        for (int i = 0; i <= 7; i++) {
            djFrames.add(load(String.format("ui/Knight/double_jump/Double jump_%03d.png", i)));
        }
        doubleJumpAnim = new Animation<>(0.04f, djFrames, Animation.PlayMode.NORMAL);



        Array<TextureRegion> idleframes = new Array<>();
        for (int i = 0; i <= 8; i++) {
            idleframes.add(load(String.format("ui/Knight/idle/Idle_%03d.png", i)));
        }
        idleFrame = new Animation<>(0.1f, idleframes, Animation.PlayMode.NORMAL);



        Array<TextureRegion> wsFrames = new Array<>();
        for (int i = 0; i <= 3; i++) {
            wsFrames.add(load(String.format("ui/Knight/wall_slide/Wall slide_%03d.png", i)));
        }
        wallSlideAnim = new Animation<>(0.08f, wsFrames, Animation.PlayMode.LOOP);



        Array<TextureRegion> wjFrames = new Array<>();
        for (int i = 0; i <= 8; i++) {
            wjFrames.add(load(String.format("ui/Knight/walljump/Walljump_%03d.png", i)));
        }
        wallJumpAnim = new Animation<>(0.02f, wjFrames, Animation.PlayMode.NORMAL);



        Array<TextureRegion> landFrames = new Array<>();
        for (int i = 0; i <= 3; i++) {
            landFrames.add(load(String.format("ui/Knight/landing/Landing_%03d.png", i)));
        }
        landingAnim = new Animation<>(0.04f, landFrames, Animation.PlayMode.NORMAL);



        Array<TextureRegion> slash = new Array<>();
        for (int i = 0; i <= 4; i++) {
            slash.add(load(String.format("ui/Knight/slash/Slash_%03d.png", i)));
        }
        slashAnim = new Animation<>(0.1f,slash,Animation.PlayMode.NORMAL);



        Array<TextureRegion> idleHurt = new Array<>();
        for (int i = 0; i <= 11; i++) {
            idleHurt.add(load(String.format("ui/Knight/idlehurt/Idle Hurt_%03d.png", i)));
        }
        idleHurtAnim = new Animation<>(0.1f, idleHurt, Animation.PlayMode.NORMAL);



        Array<TextureRegion> death = new Array<>();
        for (int i = 1; i <= 17; i++) {
            death.add(load(String.format("ui/Knight/Death/Death_%03d.png", i)));
        }
        deathAnim = new Animation<>(0.08f, death, Animation.PlayMode.NORMAL);



    }

    public TextureRegion getFrame(Knight knight, float globalStateTime) {
        KnightState state = knight.getState();

        if (state != previousState) {
            stateStartTime = globalStateTime;
            previousState  = state;
        }
        float t = globalStateTime - stateStartTime;

        TextureRegion frame;
        switch (state) {

            case DASHING:
                frame = dashAnim.getKeyFrame(t, false);
                break;
            case IDLE_HURT:      frame = idleHurtAnim.getKeyFrame(t, false); break;
            case DEATH:          frame = deathAnim.getKeyFrame(t, false); break;
            case WALL_JUMPING:
                frame = wallJumpAnim.getKeyFrame(t, false);
                break;

            case LANDING:
                frame = landingAnim.getKeyFrame(t, false);
                break;

            case DOUBLE_JUMPING:
                frame = doubleJumpAnim.getKeyFrame(t, false);
                break;
            case CAST_SPELL:
                frame = idleFrame.getKeyFrame(t, true);

                break;

            case WALL_SLIDING:

                frame = wallSlideAnim.getKeyFrame(t, true);
                break;

            case JUMPING:
            case FALLING:
                frame = airborneAnim.getKeyFrame(t, false);
                break;

            case RUNNING:
                frame = runAnim.getKeyFrame(t, true);
                break;

            case ATTACKING:
                frame = slashAnim.getKeyFrame(t, true);
                break;


            case IDLE:
            default:
                if (knight.isFacingRight) {
                    frame = idleFrame.getKeyFrame(t, true);

                }else {
                    frame = idleFrame.getKeyFrame(t, true);
                    frame.flip(true, false);

                }
                break;
        }


        boolean shouldFaceRight = (state == KnightState.WALL_SLIDING)
            ? knight.isOnRightWall
            : knight.isFacingRight;


        if (shouldFaceRight && !frame.isFlipX()) {
            frame.flip(true, false);
        }

        if (!shouldFaceRight && frame.isFlipX()) {
            frame.flip(true, false);
        }

        return frame;
    }


    private TextureRegion load(String path) {
        return new TextureRegion(new Texture(Gdx.files.internal(path)));
    }
}
