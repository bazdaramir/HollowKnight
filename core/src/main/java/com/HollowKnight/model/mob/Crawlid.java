package com.HollowKnight.model.mob;

import com.HollowKnight.model.Block;
import com.HollowKnight.model.Knight;
import com.HollowKnight.model.enums.CrawlidStation;
import com.HollowKnight.model.manager.AudioManager;
import com.badlogic.gdx.utils.Array;

public class Crawlid extends Enemy {

    private static final float MOVE_SPEED     = 300f;
    private static final int   MAX_HEALTH     = 2;
    private static final int   CONTACT_DAMAGE = 1;
    private static final float WIDTH          = 40f;
    private static final float HEIGHT         = 24f;

    private float soundTimer = 0f;

    public Crawlid(float startX, float startY) {
        super(startX, startY, WIDTH, HEIGHT,
            MOVE_SPEED, MAX_HEALTH, CONTACT_DAMAGE,
            false, 0f);
    }

    private boolean isOnScreen(Knight knight) {
        if (knight == null) return false;
        float distX = Math.abs(position.x - knight.position.x);
        float distY = Math.abs(position.y - knight.position.y);
        return distX < 1100f && distY < 700f;
    }

    @Override
    protected void decideMovement(float delta, Array<Block> blocks, Knight knight) {
        super.decideMovement(delta, blocks, knight);

        if (!isDeadState() && Math.abs(velocity.x) > 0.1f) {
            soundTimer -= delta;
            if (soundTimer <= 0) {
                if (isOnScreen(knight)) {
                    AudioManager.getInstance().CrawlerSoundHandler("run");
                }
                soundTimer = 0.5f;
            }
        }
    }

    public CrawlidStation getStation() {
        if (isDeadState()) {
            return diedGrounded() ? CrawlidStation.DEATH_LAND : CrawlidStation.DEATH_AIR;
        }

        if (isPreTurning()) {
            return CrawlidStation.PRE_TURN;
        }

        if (isTurning()) {
            return CrawlidStation.TURN;
        }

        return CrawlidStation.WALK;
    }
}
