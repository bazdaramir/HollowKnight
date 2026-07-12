package com.HollowKnight.model.mob;

import com.HollowKnight.model.Block;
import com.HollowKnight.model.Knight;
import com.HollowKnight.model.enums.HuskHornheadStation;
import com.HollowKnight.model.manager.AudioManager;
import com.badlogic.gdx.utils.Array;

public class HuskHornhead extends Enemy {

    private static final float MOVE_SPEED      = 50f;
    private static final int   MAX_HEALTH      = 4;
    private static final int   CONTACT_DAMAGE  = 1;
    private static final float WIDTH           = 44f;
    private static final float HEIGHT          = 30f;

    private static final float VISION_WIDTH      = 220f;
    private static final float VISION_HEIGHT     = 60f;
    private static final float LUNGE_SPEED       = 300f;
    private static final float ANTICIPATE_TIME   = 0.5f;
    private static final float COOLDOWN_TIME     = 0.4f;

    private HuskHornheadStation station = HuskHornheadStation.WALK;
    private float stateTimer = 0f;

    private float walkSoundTimer = 0f;

    public HuskHornhead(float startX, float startY) {
        super(startX, startY, WIDTH, HEIGHT, MOVE_SPEED, MAX_HEALTH, CONTACT_DAMAGE, false, 0f);
    }

    private boolean isOnScreen(Knight knight) {
        if (knight == null) return false;
        float distX = Math.abs(position.x - knight.position.x);
        float distY = Math.abs(position.y - knight.position.y);
        return distX < 1100f && distY < 700f;
    }

    @Override
    protected void decideMovement(float delta, Array<Block> blocks, Knight knight) {
        switch (station) {
            case ANTICIPATE:
                velocity.x = 0;
                stateTimer -= delta;
                if (stateTimer <= 0) {
                    if (knight != null) {
                        float knightCenter = knight.position.x + knight.hitbox.width / 2f;
                        float myCenter = position.x + hitbox.width / 2f;
                        isFacingRight = knightCenter > myCenter;
                    }
                    station = HuskHornheadStation.LUNGE;

                    if (isOnScreen(knight)) {
                        AudioManager.getInstance().HuskHornHeadSoundHandler("run");
                    }
                }
                return;

            case LUNGE:
                isChasing = true;
                velocity.x = isFacingRight ? LUNGE_SPEED : -LUNGE_SPEED;

                if (!hasGroundAhead(blocks, delta) || hitWallThisFrame) {
                    velocity.x = 0;
                    isChasing = false;
                    station = HuskHornheadStation.COOLDOWN;
                    stateTimer = COOLDOWN_TIME;
                }
                return;

            case COOLDOWN:
                velocity.x = 0;
                stateTimer -= delta;
                if (stateTimer <= 0) {
                    station = HuskHornheadStation.WALK;
                }
                return;

            case WALK:
            default:
                if (knight != null && canSeeKnight(knight, VISION_WIDTH, VISION_HEIGHT, blocks)) {
                    station = HuskHornheadStation.ANTICIPATE;
                    stateTimer = ANTICIPATE_TIME;
                    velocity.x = 0;
                    return;
                }
                doPatrol(blocks, delta);

                walkSoundTimer -= delta;
                if (walkSoundTimer <= 0 && Math.abs(velocity.x) > 0.1f) {
                    if (isOnScreen(knight)) {
                        AudioManager.getInstance().HuskHornHeadSoundHandler("walk");
                    }
                    walkSoundTimer = 0.6f;
                }
        }
    }

    public HuskHornheadStation getStation() {
        if (isDeadState()) return diedGrounded() ? HuskHornheadStation.DEATH_LAND : HuskHornheadStation.DEATH_AIR;
        if (station == HuskHornheadStation.LUNGE || station == HuskHornheadStation.ANTICIPATE || station == HuskHornheadStation.COOLDOWN) {
            return station;
        }
        if (isPreTurning() || isTurning()) return HuskHornheadStation.TURN;
        return HuskHornheadStation.WALK;
    }
}
