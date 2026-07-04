package com.HollowKnight.model.mob;

import com.HollowKnight.model.enums.CrawlidStation;

public class Crawlid extends Enemy {

    private static final float MOVE_SPEED     = 300f;
    private static final int   MAX_HEALTH     = 2;
    private static final int   CONTACT_DAMAGE = 1;
    private static final float WIDTH          = 40f;
    private static final float HEIGHT         = 24f;

    public Crawlid(float startX, float startY) {
        super(startX, startY, WIDTH, HEIGHT,
            MOVE_SPEED, MAX_HEALTH, CONTACT_DAMAGE,
            false, 0f);
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
