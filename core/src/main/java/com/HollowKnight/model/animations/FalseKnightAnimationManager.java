package com.HollowKnight.model.animations;

import com.HollowKnight.model.enums.FalseKnightStation;
import com.HollowKnight.model.mob.FalseKnight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;

public class FalseKnightAnimationManager {

    private final Animation<TextureRegion> idleAnim;
    private final Animation<TextureRegion> runAnticAnim, runAnim;
    private final Animation<TextureRegion> jumpAnticAnim, jumpAnim, jumpAttackAnim, landAnim;
    private final Animation<TextureRegion> attackAnticAnim, attackAnim, attackRecoverAnim;
    private final Animation<TextureRegion> stunRecoverAnim;
    private final Animation<TextureRegion> deathHitAnim, deathFallAnim, deathLandAnim;

    private static final float PHASE2_ANIM_SPEED_MULTIPLIER = 1.4f;

    private final Array<Texture> ownedTextures = new Array<>();
    private final IdentityMap<FalseKnight, StateClock> clocks = new IdentityMap<>();

    private static class StateClock {
        FalseKnightStation previousStation = null;
        float stateStartTime = 0f;
    }

    public FalseKnightAnimationManager() {
        Array<TextureRegion> idle = new Array<>();
        for (int i = 0; i <= 4; i++) idle.add(load(String.format("ui/FalseKnight/Idle_%03d.png", i)));
        idleAnim = new Animation<>(0.15f, idle, Animation.PlayMode.LOOP);

        Array<TextureRegion> runAntic = new Array<>();
        for (int i = 0; i <= 1; i++) runAntic.add(load(String.format("ui/FalseKnight/Run Antic_%03d.png", i)));
        runAnticAnim = new Animation<>(0.1f, runAntic, Animation.PlayMode.NORMAL);

        Array<TextureRegion> run = new Array<>();
        for (int i = 0; i <= 4; i++) run.add(load(String.format("ui/FalseKnight/Run_%03d.png", i)));
        runAnim = new Animation<>(0.08f, run, Animation.PlayMode.LOOP);

        Array<TextureRegion> attackAntic = new Array<>();
        for (int i = 0; i <= 5; i++) attackAntic.add(load(String.format("ui/FalseKnight/Attack Antic_%03d.png", i)));
        attackAnticAnim = new Animation<>(0.08f, attackAntic, Animation.PlayMode.NORMAL);

        Array<TextureRegion> attack = new Array<>();
        for (int i = 0; i <= 2; i++) attack.add(load(String.format("ui/FalseKnight/Attack_%03d.png", i)));
        attackAnim = new Animation<>(0.05f, attack, Animation.PlayMode.NORMAL);

        Array<TextureRegion> attackRec = new Array<>();
        for (int i = 0; i <= 4; i++) attackRec.add(load(String.format("ui/FalseKnight/Attack Recover_%03d.png", i)));
        attackRecoverAnim = new Animation<>(0.1f, attackRec, Animation.PlayMode.NORMAL);

        Array<TextureRegion> jumpAntic = new Array<>();
        jumpAntic.add(load("ui/FalseKnight/Jump Antic.png"));
        jumpAnticAnim = new Animation<>(0.1f, jumpAntic, Animation.PlayMode.NORMAL);

        Array<TextureRegion> jump = new Array<>();
        for (int i = 0; i <= 3; i++) jump.add(load(String.format("ui/FalseKnight/Jump_%03d.png", i)));
        jumpAnim = new Animation<>(0.1f, jump, Animation.PlayMode.NORMAL);

        Array<TextureRegion> jumpAttack = new Array<>();
        for (int i = 0; i <= 7; i++) jumpAttack.add(load(String.format("ui/FalseKnight/Jump Attack_%03d.png", i)));
        jumpAttackAnim = new Animation<>(0.08f, jumpAttack, Animation.PlayMode.NORMAL);

        Array<TextureRegion> land = new Array<>();
        for (int i = 0; i <= 4; i++) land.add(load(String.format("ui/FalseKnight/Land_%03d.png", i)));
        landAnim = new Animation<>(0.08f, land, Animation.PlayMode.NORMAL);

        Array<TextureRegion> stunRec = new Array<>();
        for (int i = 0; i <= 4; i++) stunRec.add(load(String.format("ui/FalseKnight/Stun Recover_%03d.png", i)));
        stunRecoverAnim = new Animation<>(0.15f, stunRec, Animation.PlayMode.NORMAL);

        Array<TextureRegion> deathHit = new Array<>();
        for (int i = 0; i <= 2; i++) deathHit.add(load(String.format("ui/FalseKnight/DeathHit_%03d.png", i)));
        deathHitAnim = new Animation<>(0.12f, deathHit, Animation.PlayMode.NORMAL);

        Array<TextureRegion> deathFall = new Array<>();
        for (int i = 0; i <= 2; i++) deathFall.add(load(String.format("ui/FalseKnight/DeathFall_%03d.png", i)));
        deathFallAnim = new Animation<>(0.12f, deathFall, Animation.PlayMode.NORMAL);

        Array<TextureRegion> deathLand = new Array<>();
        for (int i = 0; i <= 10; i++) deathLand.add(load(String.format("ui/FalseKnight/DeathLand_%03d.png", i)));
        deathLandAnim = new Animation<>(0.12f, deathLand, Animation.PlayMode.NORMAL);
    }

    public TextureRegion getFrame(FalseKnight boss, float globalStateTime) {
        FalseKnightStation station = boss.getStation();

        StateClock clock = clocks.get(boss);
        if (clock == null) {
            clock = new StateClock();
            clocks.put(boss, clock);
        }

        if (station != clock.previousStation) {
            clock.stateStartTime = globalStateTime;
            clock.previousStation = station;
        }
        float t = globalStateTime - clock.stateStartTime;

        if (boss.isPhase2() && isCombatAnimation(station)) {
            t *= PHASE2_ANIM_SPEED_MULTIPLIER;
        }

        TextureRegion baseFrame;

        switch (station) {
            case RUN_ANTIC:      baseFrame = runAnticAnim.getKeyFrame(t, false);     break;
            case RUN:            baseFrame = runAnim.getKeyFrame(t, true);           break;
            case JUMP_ANTIC:     baseFrame = jumpAnticAnim.getKeyFrame(t, false);    break;
            case JUMP:
            case DEFENSIVE_LEAP: baseFrame = jumpAnim.getKeyFrame(t, false);         break;
            case JUMP_ATTACK:    baseFrame = jumpAttackAnim.getKeyFrame(t, false);   break;
            case LAND:           baseFrame = landAnim.getKeyFrame(t, false);         break;
            case ATTACK_ANTIC:   baseFrame = attackAnticAnim.getKeyFrame(t, false);  break;
            case ATTACK:         baseFrame = attackAnim.getKeyFrame(t, false);       break;
            case ATTACK_RECOVER: baseFrame = attackRecoverAnim.getKeyFrame(t, false);break;
            case STUN_RECOVER:   baseFrame = stunRecoverAnim.getKeyFrame(t, false);  break;

            case DEATH_HIT:       baseFrame = deathHitAnim.getKeyFrame(t, false);    break;
            case DEATH_FALL:      baseFrame = deathFallAnim.getKeyFrame(t, false);   break;
            case DEATH:           baseFrame = deathLandAnim.getKeyFrame(t, false);   break;

            case STUN:           baseFrame = stunRecoverAnim.getKeyFrame(0, false);  break;

            case IDLE:
            default:             baseFrame = idleAnim.getKeyFrame(t, true);          break;
        }

        TextureRegion frame = new TextureRegion(baseFrame);

        if (!boss.isFacingRight) {
            frame.flip(true, false);
        }

        return frame;
    }

    private boolean isCombatAnimation(FalseKnightStation station) {
        switch (station) {
            case RUN_ANTIC:
            case RUN:
            case ATTACK_ANTIC:
            case ATTACK:
            case ATTACK_RECOVER:
            case JUMP_ANTIC:
            case JUMP:
            case JUMP_ATTACK:
            case LAND:
            case DEFENSIVE_LEAP:
                return true;
            default:
                return false;
        }
    }

    public void forget(FalseKnight boss) { clocks.remove(boss); }

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
