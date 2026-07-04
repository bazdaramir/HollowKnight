package com.HollowKnight.model.mob;

import com.HollowKnight.model.Block;
import com.HollowKnight.model.Knight;
import com.HollowKnight.model.enums.MossflyStation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Mossfly extends Enemy {

    private static final float MOVE_SPEED = 180f;
    private static final int MAX_HEALTH = 2;
    private static final int CONTACT_DAMAGE = 1;
    private static final float WIDTH = 45f;
    private static final float HEIGHT = 45f;
    private static final float AGGRO_RANGE = 350f;

    private MossflyStation station = MossflyStation.SHAKE;
    private float stateTimer = 0f;

    public Mossfly(float startX, float startY) {
        super(startX, startY, WIDTH, HEIGHT, MOVE_SPEED, MAX_HEALTH, CONTACT_DAMAGE, true, AGGRO_RANGE);
    }

    @Override
    protected void decideMovement(float delta, Array<Block> blocks, Knight knight) {
        if (attackPauseTimer > 0) {
            velocity.set(0, 0);
            return;
        }

        switch (station) {
            case SHAKE:
                velocity.set(0, 0);
                if (knight != null && withinAggroRange(knight)) {
                    station = MossflyStation.APPEAR;
                    stateTimer = 0.6f;
                }
                break;

            case APPEAR:
                velocity.set(0, 0);
                stateTimer -= delta;
                if (stateTimer <= 0) {
                    station = MossflyStation.TURN_TO_FLY;
                    stateTimer = 0.3f;
                }
                break;

            case TURN_TO_FLY:
                velocity.set(0, 0);
                stateTimer -= delta;
                if (stateTimer <= 0) {
                    station = MossflyStation.FLY;
                }
                break;

            case FLY:
                if (knight != null) {
                    float dx = (knight.position.x + knight.hitbox.width / 2f) - (position.x + hitbox.width / 2f);
                    float dy = (knight.position.y + knight.hitbox.height / 2f) - (position.y + hitbox.height / 2f);

                    Vector2 dir = new Vector2(dx, dy).nor();
                    velocity.x = dir.x * moveSpeed;
                    velocity.y = dir.y * moveSpeed;

                    isFacingRight = velocity.x > 0;
                } else {
                    velocity.set(0, 0);
                }
                break;
        }
    }

    @Override
    protected void applyGravity(float delta) {
        if (station == MossflyStation.SHAKE) {
            super.applyGravity(delta);
        } else {
            // جاذبه رو خنثی میکنیم وفتی بیدار شده و بوته نیستش
        }
    }

    public MossflyStation getStation() {
        if (isDeadState()) return diedGrounded() ? MossflyStation.DEATH_LAND : MossflyStation.DEATH_AIR;
        return station;
    }
}
