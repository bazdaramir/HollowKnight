package com.HollowKnight.model.animations;

import com.HollowKnight.model.enums.ZoteStation;
import com.HollowKnight.model.mob.Zote;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;

public class ZoteAnimationManager {

    private final Animation<TextureRegion> idleAnim, talkAnim, attackAnim, fallAnim, getUpAnim, rollAnim, turnAnim;

    private final IdentityMap<Zote, StateClock> clocks = new IdentityMap<>();
    private final Array<Texture> ownedTextures = new Array<>();

    private static class StateClock {
        ZoteStation previousStation = null;
        float stateStartTime = 0f;
    }

    public ZoteAnimationManager() {
        Array<TextureRegion> idle = new Array<>();
        for (int i = 0; i <= 4; i++) idle.add(load(String.format("ui/Zote/Idle_%03d.png", i)));
        idleAnim = new Animation<>(0.15f, idle, Animation.PlayMode.LOOP);



        Array<TextureRegion> talk = new Array<>();
        for (int i = 0; i <= 4; i++) talk.add(load(String.format("ui/Zote/Talk_%03d.png", i)));
        talkAnim = new Animation<>(0.15f, talk, Animation.PlayMode.LOOP);



        Array<TextureRegion> attack = new Array<>();
        for (int i = 0; i <= 3; i++) attack.add(load(String.format("ui/Zote/Attack_%03d.png", i)));
        attackAnim = new Animation<>(0.1f, attack, Animation.PlayMode.LOOP);



        Array<TextureRegion> fall = new Array<>();
        for (int i = 0; i <= 4; i++) fall.add(load(String.format("ui/Zote/Fall_%03d.png", i)));
        fallAnim = new Animation<>(0.15f, fall, Animation.PlayMode.NORMAL);



        Array<TextureRegion> getUp = new Array<>();
        for (int i = 0; i <= 3; i++) getUp.add(load(String.format("ui/Zote/Get Up_%03d.png", i)));
        getUpAnim = new Animation<>(0.15f, getUp, Animation.PlayMode.NORMAL);



        Array<TextureRegion> roll = new Array<>();
        for (int i = 0; i <= 2; i++) roll.add(load(String.format("ui/Zote/Roll_%03d.png", i)));
        rollAnim = new Animation<>(0.1f, roll, Animation.PlayMode.NORMAL);



        Array<TextureRegion> turn = new Array<>();
        for (int i = 0; i <= 1; i++) turn.add(load(String.format("ui/Zote/Turn_%03d.png", i)));
        turnAnim = new Animation<>(0.1f, turn, Animation.PlayMode.NORMAL);
    }

    public TextureRegion getFrame(Zote zote, float globalStateTime) {
        ZoteStation station = zote.getStation();

        StateClock clock = clocks.get(zote);
        if (clock == null) {
            clock = new StateClock();
            clocks.put(zote, clock);
        }

        if (station != clock.previousStation) {
            clock.stateStartTime = globalStateTime;
            clock.previousStation = station;
        }
        float t = globalStateTime - clock.stateStartTime;

        TextureRegion baseFrame;
        switch (station) {
            case TALK:   baseFrame = talkAnim.getKeyFrame(t, true);     break;
            case ATTACK: baseFrame = attackAnim.getKeyFrame(t, true);   break;
            case FALL:   baseFrame = fallAnim.getKeyFrame(t, false);    break;
            case GET_UP: baseFrame = getUpAnim.getKeyFrame(t, false);   break;
            case ROLL:   baseFrame = rollAnim.getKeyFrame(t, false);    break;
            case TURN:   baseFrame = turnAnim.getKeyFrame(t, false);    break;
            case IDLE:
            default:     baseFrame = idleAnim.getKeyFrame(t, true);     break;
        }


        TextureRegion frame = new TextureRegion(baseFrame);


        if (zote.isFacingRight && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (!zote.isFacingRight && frame.isFlipX()) {
            frame.flip(true, false);
        }

        return frame;
    }

    private TextureRegion load(String path) {
        Texture tex = new Texture(Gdx.files.internal(path));
        ownedTextures.add(tex);
        return new TextureRegion(tex);
    }

    public void forget(Zote zote) {
        clocks.remove(zote);
    }

    public void dispose() {
        for (Texture t : ownedTextures) t.dispose();
        ownedTextures.clear();
    }
}
