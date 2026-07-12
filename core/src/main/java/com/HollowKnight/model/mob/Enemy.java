package com.HollowKnight.model.mob;

import com.HollowKnight.model.Block;
import com.HollowKnight.model.Knight;
import com.HollowKnight.model.enums.KnightState;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {

    public Vector2   position;
    public Vector2   velocity;
    public Rectangle hitbox;
    public boolean   isFacingRight = true;

    protected final float   moveSpeed;
    protected final int     maxHealth;
    protected final int     contactDamage;
    protected final boolean canChasePlayer;
    protected final float   aggroRange;

    private int health;

    protected float knockbackTimer = 0f;

    protected float attackPauseTimer = 0f;
    protected static final float ATTACK_PAUSE_DURATION = 1f;
    protected static final float GRAVITY        = 1500f;
    protected static final float MAX_FALL_SPEED = 1000f;
    private   static final float MAX_DELTA_TIME = 1f / 30f;
    private   static final float GROUND_STICK   = 10f;

    private static final float LEDGE_PROBE_DROP = 20f;
    private final Rectangle ledgeProbe = new Rectangle();
    private final Rectangle visionRect  = new Rectangle();

    protected static final float PATROL_DURATION  = 3.0f;
    protected static final float REST_DURATION    = 0.5f;

    protected static final float PRE_TURN_DURATION = 0.18f;

    protected static final float TURN_DURATION     = 0.15f;

    protected float patrolTimer  = 0f;
    protected float restTimer    = 0f;
    protected float preTurnTimer = 0f;
    protected float turnTimer    = 0f;

    private boolean pendingDirectionFlip = false;

    private static final float CONTACT_DAMAGE_COOLDOWN = 0.5f;
    private float contactDamageTimer = 0f;

    protected static final float DEATH_DURATION = 0.6f;
    private boolean isDead             = false;
    private boolean wasGroundedAtDeath = true;
    private float   deathTimer         = 0f;

    protected boolean onGround        = false;
    protected boolean isChasing       = false;
    protected boolean hitWallThisFrame = false;

    public Enemy(float startX, float startY, float width, float height,
                 float moveSpeed, int maxHealth, int contactDamage,
                 boolean canChasePlayer, float aggroRange) {
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(0, 0);
        this.hitbox   = new Rectangle(startX, startY, width, height);

        this.moveSpeed      = moveSpeed;
        this.maxHealth      = maxHealth;
        this.health         = maxHealth;
        this.contactDamage  = contactDamage;
        this.canChasePlayer = canChasePlayer;
        this.aggroRange     = aggroRange;

    }


    public final void update(float delta, Array<Block> blocks, Knight knight) {
        if (isDead) { deathTimer -= delta; return; }

        delta = Math.min(delta, MAX_DELTA_TIME);
        if (blocks == null) blocks = new Array<>();

        if (knockbackTimer > 0) knockbackTimer -= delta;
        if (contactDamageTimer > 0) contactDamageTimer -= delta;
        if (attackPauseTimer > 0) attackPauseTimer -= delta;

        if (knockbackTimer <= 0) {
            decideMovement(delta, blocks, knight);
        }

        applyGravity(delta);
        resolveVerticalMovement(delta, blocks);
        resolveHorizontalMovement(delta, blocks);
        checkContactDamage(knight);
    }

    public void takeDamage(int amount, float sourceX) {
        if (isDead) return;
        health -= amount;
        if (health <= 0) {
            health = 0;
            die();
        } else {
            knockbackTimer = 0.25f;
            float myCenterX = position.x + hitbox.width / 2f;
            velocity.x = (sourceX < myCenterX) ? 350f : -350f;
            velocity.y = 250f;
            onGround = false;
        }
    }

    public void takeDamage(int amount) {
        takeDamage(amount, position.x);
    }

    protected void decideMovement(float delta, Array<Block> blocks, Knight knight) {
        if (attackPauseTimer > 0) {
            velocity.x = 0;
            return;
        }

        isChasing = canChasePlayer && knight != null && withinAggroRange(knight);

        if (isChasing) {
            float knightCenter = knight.position.x + knight.hitbox.width / 2f;
            float myCenter     = position.x + hitbox.width / 2f;
            isFacingRight = knightCenter > myCenter;
            velocity.x = isFacingRight ? moveSpeed : -moveSpeed;
            return;
        }

        doPatrol(blocks, delta);
    }

    private void checkContactDamage(Knight knight) {
        if (knight == null || contactDamageTimer > 0 || knight.getState() == KnightState.DEATH) return;

        if (hitbox.overlaps(knight.hitbox)) {
            float enemyCenterX = position.x + hitbox.width / 2f;
            knight.takeDamage(contactDamage, enemyCenterX);

            contactDamageTimer = CONTACT_DAMAGE_COOLDOWN;
            attackPauseTimer = ATTACK_PAUSE_DURATION;
        }
    }

    protected void doPatrol(Array<Block> blocks, float delta) {
        tickPatrolTimers(delta);

        if (preTurnTimer > 0 || turnTimer > 0 || restTimer > 0) {
            velocity.x = 0;
            return;
        }

        velocity.x = isFacingRight ? moveSpeed : -moveSpeed;

        if (!hasGroundAhead(blocks, delta)) {
            velocity.x = 0;
            beginPreTurn();
        }
    }

    protected boolean hasGroundAhead(Array<Block> blocks, float delta) {
        float nextStepX = velocity.x * delta;
        float probeWidth = 2f;
        float probeX = isFacingRight
            ? (hitbox.x + hitbox.width + nextStepX)
            : (hitbox.x + nextStepX - probeWidth);

        ledgeProbe.set(probeX, hitbox.y - LEDGE_PROBE_DROP, probeWidth, LEDGE_PROBE_DROP + 1f);

        for (Block block : blocks) {
            if (block.isSolid && ledgeProbe.overlaps(block.rect)) {
                return true;
            }
        }
        return false;
    }


    protected void beginPreTurn() {
        if (preTurnTimer > 0) return;
        preTurnTimer = PRE_TURN_DURATION;
        pendingDirectionFlip = true;
        velocity.x = 0;
    }

    protected void commitTurn() {
        isFacingRight        = !isFacingRight;
        pendingDirectionFlip = false;
        turnTimer            = TURN_DURATION;
    }


    protected void tickPatrolTimers(float delta) {
        if (preTurnTimer > 0) {
            preTurnTimer -= delta;
            if (preTurnTimer <= 0 && pendingDirectionFlip) {
                preTurnTimer = 0;
                commitTurn();
            }
            return;
        }

        if (turnTimer > 0) {
            turnTimer -= delta;
            return;
        }

        if (restTimer > 0) {
            restTimer -= delta;
            return;
        }

        patrolTimer += delta;
        if (patrolTimer >= PATROL_DURATION) {
            patrolTimer = 0f;
            restTimer   = REST_DURATION;
        }
    }


    protected void reverseDirection() {
        beginPreTurn();
    }


    protected boolean withinAggroRange(Knight knight) {
        float dx = (knight.position.x + knight.hitbox.width / 2f)
            - (position.x + hitbox.width / 2f);
        return Math.abs(dx) <= aggroRange;
    }

    protected boolean canSeeKnight(Knight knight, float visionWidth, float visionHeight, Array<Block> blocks) {
        if (knight == null) return false;
        float rectX = isFacingRight ? hitbox.x + hitbox.width : hitbox.x - visionWidth;
        float rectY = hitbox.y + hitbox.height / 2f - visionHeight / 2f;
        visionRect.set(rectX, rectY, visionWidth, visionHeight);
        if (!visionRect.overlaps(knight.hitbox)) return false;
        return hasClearLineOfSight(knight, blocks);
    }

    private boolean hasClearLineOfSight(Knight knight, Array<Block> blocks) {
        float fromX = hitbox.x + hitbox.width / 2f;
        float fromY = hitbox.y + hitbox.height / 2f;
        float toX   = knight.position.x + knight.hitbox.width / 2f;
        float toY   = knight.position.y + knight.hitbox.height / 2f;
        final int SAMPLES = 10;
        for (int i = 1; i < SAMPLES; i++) {
            float t  = i / (float) SAMPLES;
            float sx = fromX + (toX - fromX) * t;
            float sy = fromY + (toY - fromY) * t;
            for (Block block : blocks) {
                if (block.isSolid && block.rect.contains(sx, sy)) return false;
            }
        }
        return true;
    }


    protected void applyGravity(float delta) {
        if (onGround) {
            velocity.y = -GROUND_STICK;
        } else {
            velocity.y -= GRAVITY * delta;
            velocity.y  = Math.max(velocity.y, -MAX_FALL_SPEED);
        }
    }


    private void resolveVerticalMovement(float delta, Array<Block> blocks) {
        position.y += velocity.y * delta;
        hitbox.y    = position.y;
        onGround    = false;

        Block chosen = null;
        float minPen = Float.MAX_VALUE;
        boolean landing = velocity.y <= 0;

        for (Block block : blocks) {
            if (!block.isSolid || !hitbox.overlaps(block.rect)) continue;
            float pen = landing
                ? (block.rect.y + block.rect.height) - position.y
                : (position.y + hitbox.height) - block.rect.y;

            if (pen > 0 && pen < minPen) {
                minPen = pen;
                chosen = block;
            }
        }

        if (chosen != null) {
            if (landing) {
                position.y = chosen.rect.y + chosen.rect.height;
                onGround = true;
            }
            else {
                position.y = chosen.rect.y - hitbox.height;
            }
            velocity.y = 0;
            hitbox.y   = position.y;
        }
    }

    private void resolveHorizontalMovement(float delta, Array<Block> blocks) {
        position.x += velocity.x * delta;
        hitbox.x    = position.x;
        hitWallThisFrame = false;

        Block chosen     = null;
        float minPen     = Float.MAX_VALUE;
        boolean blockOnRight = false;

        for (Block block : blocks) {
            if (!block.isSolid || !hitbox.overlaps(block.rect)) continue;

            float pen;
            boolean right;
            if (velocity.x > 0) {
                pen = (position.x + hitbox.width) - block.rect.x;
                right = true;
            } else if (velocity.x < 0) {
                pen = (block.rect.x + block.rect.width) - position.x;
                right = false;
            } else { continue; }

            if (pen > 0 && pen < minPen) {
                minPen = pen;
                chosen = block;
                blockOnRight = right;
            }
        }

        if (chosen != null) {
            position.x = blockOnRight ? chosen.rect.x - hitbox.width
                : chosen.rect.x + chosen.rect.width;
            velocity.x = 0;
            hitbox.x   = position.x;
            hitWallThisFrame = true;

            if (!isChasing) {
                reverseDirection();
            }
        }
    }



    protected void die() {
        isDead             = true;
        wasGroundedAtDeath = onGround;
        deathTimer         = DEATH_DURATION;
        velocity.set(0, 0);
    }

    public boolean isAlive()           { return !isDead; }
    public boolean isDeadState()       { return isDead; }
    public boolean diedGrounded()      { return wasGroundedAtDeath; }
    public boolean isReadyForRemoval() { return isDead && deathTimer <= 0; }

    public boolean isPreTurning()      { return preTurnTimer > 0; }

    public boolean isTurning()         { return turnTimer > 0; }
    public boolean isResting()         { return restTimer > 0; }
    public int     getHealth()         { return health; }

    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

}
