package com.HollowKnight.model.mob;

import com.HollowKnight.model.Block;
import com.HollowKnight.model.Knight;
import com.HollowKnight.model.enums.CrystalGuardianStation;
import com.HollowKnight.model.manager.AudioManager;
import com.badlogic.gdx.utils.Array;

public class CrystalGuardian extends Enemy {

    private static final int   MAX_HEALTH       = 6;
    private static final int   CONTACT_DAMAGE   = 1;
    private static final int   LASER_DAMAGE     = 2;
    private static final float WIDTH            = 50f;
    private static final float HEIGHT           = 60f;

    private static final float VISION_WIDTH     = 600f;
    private static final float VISION_HEIGHT    = 30f;
    private static final float ENRAGE_SPEED     = 180f;
    private static final float SHOOT_DURATION   = 0.5f;
    private static final float ENRAGE_DURATION  = 2.0f;
    private static final float POST_ENRAGE_COOLDOWN = 1.0f;

    private CrystalGuardianStation station = CrystalGuardianStation.IDLE;
    private float stateTimer = 0f;
    private float idleCooldown = 0f;
    private float startX;

    public CrystalGuardian(float startX, float startY) {
        super(startX, startY, WIDTH, HEIGHT, ENRAGE_SPEED, MAX_HEALTH, CONTACT_DAMAGE, false, 0f);
        this.startX = startX;
    }

    private boolean isOnScreen(Knight knight) {
        if (knight == null) return false;
        float distX = Math.abs(position.x - knight.position.x);
        float distY = Math.abs(position.y - knight.position.y);
        return distX < 1100f && distY < 700f;
    }

    @Override
    protected void decideMovement(float delta, Array<Block> blocks, Knight knight) {
        if (idleCooldown > 0) idleCooldown -= delta;

        switch (station) {
            case SHOOT:
                velocity.x = 0;
                stateTimer -= delta;
                if (stateTimer <= 0) {
                    fireLaser(knight);

                    if (isOnScreen(knight)) {
                        AudioManager.getInstance().CrystallsSoundHandler("laser");
                        AudioManager.getInstance().CrystallsSoundHandler("run");
                    }

                    station = CrystalGuardianStation.ENRAGED;
                    stateTimer = ENRAGE_DURATION;
                }
                return;

            case ENRAGED:
                isChasing = true;
                if (knight != null) {
                    float knightCenter = knight.position.x + knight.hitbox.width / 2f;
                    float myCenter = position.x + hitbox.width / 2f;
                    isFacingRight = knightCenter > myCenter;
                }
                velocity.x = isFacingRight ? moveSpeed : -moveSpeed;
                stateTimer -= delta;

                if (stateTimer <= 0 || !hasGroundAhead(blocks, delta) || hitWallThisFrame) {
                    velocity.x = 0;
                    isChasing = false;
                    station = CrystalGuardianStation.IDLE;
                    idleCooldown = POST_ENRAGE_COOLDOWN;
                }
                return;

            case IDLE:
            default:
                if (Math.abs(position.x - startX) > 2f) {
                    isFacingRight = startX > position.x;
                    velocity.x = isFacingRight ? moveSpeed : -moveSpeed;

                    if (!hasGroundAhead(blocks, delta) || hitWallThisFrame) {
                        velocity.x = 0;
                        startX = position.x;
                    }
                } else {
                    velocity.x = 0;
                    position.x = startX;
                    if (idleCooldown <= 0 && knight != null && canSeeKnight(knight, VISION_WIDTH, VISION_HEIGHT, blocks)) {
                        station = CrystalGuardianStation.SHOOT;
                        stateTimer = SHOOT_DURATION;
                    }
                }
        }
    }

    private void fireLaser(Knight knight) {
        if (knight != null) knight.takeDamage(LASER_DAMAGE);
    }

    public CrystalGuardianStation getStation() {
        if (isDeadState()) return diedGrounded() ? CrystalGuardianStation.DEATH_LAND : CrystalGuardianStation.DEATH_AIR;
        if (station == CrystalGuardianStation.IDLE && Math.abs(velocity.x) > 0.1f) return CrystalGuardianStation.ENRAGED;
        return station;
    }
}
