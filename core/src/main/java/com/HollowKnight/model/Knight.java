package com.HollowKnight.model;

import com.HollowKnight.model.enums.KnightState;
import com.HollowKnight.model.mob.Enemy;
import com.HollowKnight.model.mob.FalseKnight;
import com.HollowKnight.model.mob.Zote;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Knight {


    public Vector2   position;
    public Vector2   velocity;
    public Rectangle hitbox;
    public Vector2   lastSafePosition;

    public boolean isFacingRight  = true;
    public boolean isDashing      = false;
    public boolean isOnGround     = false;
    public boolean isWallSliding  = false;
    public boolean isOnRightWall  = false;
    public boolean isOnLeftWall   = false;

    public float horizontalInput  = 0f;

    public int maxHealth = 5;
    public int health = maxHealth;
    public float hurtTimer = 0f;
    public boolean isDead = false;

    public float invincibilityTimer = 0f;
    public float freezeTimer = 0f;
    private float knockbackDirX = 0f;

    public boolean isKnockedBack = false;
    public float deathTimer = 0f;
    public float castTimer = 0f;

    public Array<VengefulSpirit> fireballs = new Array<>();
    public Array<HowlingWraiths> wraiths = new Array<>();


    // فیزیک
    private static final float MOVE_SPEED          = 400f;
    private static final float GRAVITY             = 1500f;
    private static final float FALL_GRAVITY_MULT   = 1.6f;
    private static final float MAX_FALL_SPEED      = 1000f;
    private static final float GROUND_STICK_SPEED  = 60f;
    private static final float JUMP_SPEED          = 800f;
    private static final float JUMP_CUT_MULT       = 0.45f;
    private static final float COYOTE_TIME         = 0.08f;
    private static final float JUMP_BUFFER_TIME    = 0.12f;
    private static final float DOUBLE_JUMP_SPEED   = 720f;
    private static final float DOUBLE_JUMP_ANIM    = 0.32f;
    public  static final float DASH_SPEED          = 700f;
    public  static final float DASH_DURATION       = 0.45f;
    public  static final float DASH_COOLDOWN       = 1.2f;
    private static final float WALL_SLIDE_GRAVITY  = 0.18f;
    private static final float WALL_SLIDE_MAX_FALL = 90f;
    private static final float WALL_JUMP_SPEED_Y   = 780f;
    private static final float WALL_JUMP_SPEED_X   = 380f;
    private static final float WALL_JUMP_LOCKOUT   = 0.18f;
    private static final float WALL_JUMP_ANIM      = 0.30f;
    private static final float LANDING_DURATION    = 0.16f;
    private static final float ATTACK_DURATION     = 0.20f;
    private static final float MAX_DELTA_TIME      = 1f / 30f;


    public int maxSoul = 99;
    public int soul = 0;
    public final int SOUL_PER_HIT = 40;// باید 11 باشه ولی برای دیباگ 40 گذاشتم که زودتر بتونم استفادهک نم
    public final int SOUL_FOCUS_COST = 33;

    public boolean isFocusing = false;
    private float focusTimer = 0f;
    private final float FOCUS_DURATION = 1.5f; // زمان لازم برای هیل شدن کامل

    private float elapsedTime           = 0f;
    private float coyoteTimeCounter     = 0f;
    private float jumpBufferCounter     = 0f;
    private float dashTimer             = 0f;
    private float lastDashTime          = -DASH_COOLDOWN;
    private boolean hasDoubleJump       = true;
    private float   doubleJumpAnimTimer = 0f;
    private float wallJumpLockoutTimer  = 0f;
    private float wallJumpAnimTimer     = 0f;
    private float   landingTimer        = 0f;
    private boolean wasOnGround         = false;
    private float attackTimer           = 0f;
    private KnightState currentState    = KnightState.IDLE;

    public Knight(float startX, float startY) {
        position         = new Vector2(startX, startY);
        velocity         = new Vector2(0, 0);
        hitbox           = new Rectangle(startX, startY, 20, 30);
        lastSafePosition = new Vector2(startX, startY);
    }
    public void castVengefulSpirit() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || isDashing || castTimer > 0 || isFocusing) return;

        if (soul >= SOUL_FOCUS_COST) {
            soul -= SOUL_FOCUS_COST;
            castTimer = 0.4f;
            velocity.x = 0;
            velocity.y = 0;

            float spawnX = isFacingRight ? position.x + hitbox.width : position.x - 120f;
            float spawnY = position.y - 10f;
            fireballs.add(new VengefulSpirit(spawnX, spawnY, isFacingRight));
        }
    }

    public void castHowlingWraiths() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || isDashing || castTimer > 0 || isFocusing) return;

        if (soul >= SOUL_FOCUS_COST) {
            soul -= SOUL_FOCUS_COST;
            castTimer = 0.5f;
            velocity.x = 0;
            velocity.y = 0;

            wraiths.add(new HowlingWraiths(position.x, position.y));
        }
    }


    public void releaseJump() {
        if (velocity.y > 0) velocity.y *= JUMP_CUT_MULT;
    }

    public void moveRight() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || attackTimer > 0) return;
        horizontalInput = 1f;
        isFacingRight   = true;
        if (isDashing || wallJumpLockoutTimer > 0) return;
        velocity.x = MOVE_SPEED;
    }

    public void moveLeft() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || attackTimer > 0) return;
        horizontalInput = -1f;
        isFacingRight   = false;
        if (isDashing || wallJumpLockoutTimer > 0) return;
        velocity.x = -MOVE_SPEED;
    }

    public void stopMoving() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || attackTimer > 0) return;
        horizontalInput = 0f;
        if (isDashing || wallJumpLockoutTimer > 0) return;
        velocity.x = 0;
    }

    public void requestJump() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack) return;
        if (isWallSliding) {
            performWallJump();
            return;
        }
        if (!isOnGround && coyoteTimeCounter <= 0 && hasDoubleJump && !isDashing) {
            performDoubleJump();
            return;
        }
        jumpBufferCounter = JUMP_BUFFER_TIME;
    }

    public void dash() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || isDashing) return;
        if (elapsedTime - lastDashTime < DASH_COOLDOWN) return;

        isDashing     = true;
        dashTimer     = DASH_DURATION;
        velocity.x    = isFacingRight ? DASH_SPEED : -DASH_SPEED;
        velocity.y    = 0;
        lastDashTime  = elapsedTime;
    }
    public void startFocus() {
        if (isOnGround && !isDashing && attackTimer <= 0 && hurtTimer <= 0 && health < maxHealth && soul >= SOUL_FOCUS_COST && !isDead) {
            isFocusing = true;
            velocity.x = 0;
        }
    }

    public void stopFocus() {
        isFocusing = false;
        focusTimer = 0f;
    }

    public void attack(Array<Enemy> enemies, FalseKnight boss, Zote zote) {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || attackTimer > 0 || isDashing) return;

        attackTimer = ATTACK_DURATION;
        velocity.x  = 0;

        float slashWidth = 160f;
        float slashHeight = 100f;
        float slashX = isFacingRight ? position.x + hitbox.width : position.x - slashWidth;
        float slashY = position.y + 10f;

        Rectangle slashHitbox = new Rectangle(slashX, slashY, slashWidth, slashHeight);

        boolean hitSomething = false;
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && slashHitbox.overlaps(enemy.hitbox)) {
                    enemy.takeDamage(1);
                    hitSomething = true;
                }
            }
        }
        if (boss != null && !boss.isDead && slashHitbox.overlaps(boss.hitbox)) {
            boss.takeDamage(1);
            hitSomething = true;
        }


        if (hitSomething) {
            soul = Math.min(soul + SOUL_PER_HIT, maxSoul);
        }
    }


    public void takeDamage(int amount, float hitSourceX) {
        if (isDead || invincibilityTimer > 0 || isDashing) return;

        health -= amount;

        invincibilityTimer = 2.0f;
        freezeTimer = 0.12f;

        stopFocus();
        attackTimer = 0;
        isWallSliding = false;

        if (health <= 0) {
            health = 0;
            isDead = true;
            velocity.set(0, 0);
            deathTimer = 1.5f;
        } else {
            isKnockedBack = true;
            float myCenterX = position.x + hitbox.width / 2f;
            knockbackDirX = (hitSourceX < myCenterX) ? 350f : -350f;
        }
    }

    public void takeDamage(int amount) {
        takeDamage(amount, position.x);
    }
    public boolean isFlashing() {
        return invincibilityTimer > 0 && (int)(invincibilityTimer * 15) % 2 == 0;
    }


    private KnightState resolveState() {
        // تو اینجا هربار استیتی یا استیشنی که هستش و ور میریم و اپدیت میکنیم
        if (isDead)                     return KnightState.DEATH;
        if (hurtTimer > 0)              return KnightState.IDLE_HURT;
        if (isDashing)                  return KnightState.DASHING;
        if (isFocusing)                 return KnightState.FOCUS;
        if (attackTimer > 0)            return KnightState.ATTACKING;
        if (wallJumpAnimTimer > 0)      return KnightState.WALL_JUMPING;
        if (landingTimer > 0)           return KnightState.LANDING;
        if (doubleJumpAnimTimer > 0)    return KnightState.DOUBLE_JUMPING;
        if (isWallSliding)              return KnightState.WALL_SLIDING;
        if (!isOnGround) {
            return velocity.y > 0 ? KnightState.JUMPING : KnightState.FALLING;
        }
        if (Math.abs(velocity.x) > 0.1f) return KnightState.RUNNING;
        return KnightState.IDLE;
    }

    private void takeDamageAndRespawn() {
        if (isDead || invincibilityTimer > 0) return;
        takeDamage(1, position.x);

        if (!isDead) {
            position.set(lastSafePosition);
            hitbox.setPosition(position);
            velocity.set(0, 0);

            isDashing          = false;
            isWallSliding      = false;
            isKnockedBack      = false;
            hurtTimer          = 0.5f;
            freezeTimer        = 0;

            doubleJumpAnimTimer = 0;
            attackTimer         = 0;
            wallJumpAnimTimer   = 0;
            landingTimer        = 0;
            hasDoubleJump       = true;
        }
    }

    public KnightState getState() { return currentState; }


    public void update(float delta, Array<Block> blocks,Array<Enemy> enemies,FalseKnight boss,Zote zote) {
        delta = Math.min(delta, MAX_DELTA_TIME);
        for (int i = fireballs.size - 1; i >= 0; i--) {
            VengefulSpirit vs = fireballs.get(i);
            vs.update(delta, enemies, boss, zote);
            if (vs.lifeTimer <= 0) fireballs.removeIndex(i);
        }
        for (int i = wraiths.size - 1; i >= 0; i--) {
            HowlingWraiths hw = wraiths.get(i);
            hw.update(delta, enemies, boss, zote);
            if (hw.lifeTimer <= 0) wraiths.removeIndex(i);
        }

        if (invincibilityTimer > 0) invincibilityTimer -= delta;

        // وقتی فریز باشیم هیچی انجام نمیشه
        if (freezeTimer > 0) {
            freezeTimer -= delta;
            velocity.set(0, 0);
            if (freezeTimer <= 0 && !isDead && isKnockedBack) {
                velocity.y = 550f;
                velocity.x = knockbackDirX;
                isOnGround = false;
            }
            return;
        }

        elapsedTime += delta;
        if (blocks == null) blocks = new Array<>();

        if (deathTimer           > 0) deathTimer           -= delta;
        if (hurtTimer            > 0) hurtTimer            -= delta;
        if (attackTimer          > 0) attackTimer          -= delta;
        if (wallJumpLockoutTimer > 0) wallJumpLockoutTimer -= delta;
        if (wallJumpAnimTimer    > 0) wallJumpAnimTimer    -= delta;
        if (doubleJumpAnimTimer  > 0) doubleJumpAnimTimer  -= delta;
        if (jumpBufferCounter    > 0) jumpBufferCounter    -= delta;

        if (isOnGround) coyoteTimeCounter = COYOTE_TIME;
        else            coyoteTimeCounter -= delta;

        if (isOnGround && !wasOnGround) {
            if (isKnockedBack) {
                isKnockedBack = false;
                hurtTimer = 0.5f;
                velocity.x = 0;
            } else {
                landingTimer  = LANDING_DURATION;
                hasDoubleJump = true;
            }
        }
        wasOnGround = isOnGround;
        if (landingTimer > 0) landingTimer -= delta;

        if (jumpBufferCounter > 0 && coyoteTimeCounter > 0 && !isDashing) {
            velocity.y        = JUMP_SPEED;
            isOnGround        = false;
            jumpBufferCounter = 0;
            coyoteTimeCounter = 0;
        }
        if (isFocusing) {
            velocity.x = 0;
            if (isOnGround) {
                focusTimer += delta;
                if (focusTimer >= FOCUS_DURATION) {
                    health = Math.min(health + 1, maxHealth);
                    soul -= SOUL_FOCUS_COST;
                    focusTimer = 0f;
                    System.out.println("Healed 1HP  HP: " + health);

                    if (soul < SOUL_FOCUS_COST || health == maxHealth) {
                        isFocusing = false;
                    }
                }
            } else {
                stopFocus();
            }
        } else {
            focusTimer = 0f;
        }
        if (castTimer > 0) {
            castTimer -= delta;
            velocity.x = 0;
        }


        // جاذبه
        if (isOnGround) {
            velocity.y = -GROUND_STICK_SPEED;
        } else if (isDashing || castTimer > 0) {
            velocity.y = 0;
        } else if (isWallSliding) {
            velocity.y -= GRAVITY * WALL_SLIDE_GRAVITY * delta;
            if (velocity.y < -WALL_SLIDE_MAX_FALL) velocity.y = -WALL_SLIDE_MAX_FALL;
        } else {
            float gMult    = (velocity.y < 0) ? FALL_GRAVITY_MULT : 1f;
            velocity.y    -= GRAVITY * gMult * delta;
            velocity.y     = Math.max(velocity.y, -MAX_FALL_SPEED);
        }

        if (isDashing) {
            dashTimer -= delta;
            if (dashTimer <= 0) isDashing = false;
        }
        position.y += velocity.y * delta;
        hitbox.y    = position.y;
        isOnGround  = false;

        Block closestVerticalBlock = null;
        float minOverlapY = Float.MAX_VALUE;
        boolean blockAbove = false;

        for (Block block : blocks) {
            if (!hitbox.overlaps(block.rect)) continue;
            if (block.isDeadly) { takeDamageAndRespawn(); return; }
            if (!block.isSolid) continue;

            float overlap;
            if (velocity.y <= 0) {
                overlap = (block.rect.y + block.rect.height) - position.y;
                if (overlap > 0 && overlap < minOverlapY) {
                    minOverlapY = overlap;
                    closestVerticalBlock = block;
                    blockAbove = false;
                }
            } else {
                overlap = (position.y + hitbox.height) - block.rect.y;
                if (overlap > 0 && overlap < minOverlapY) {
                    minOverlapY = overlap;
                    closestVerticalBlock = block;
                    blockAbove = true;
                }
            }
        }

        if (closestVerticalBlock != null) {
            if (!blockAbove) {
                position.y = closestVerticalBlock.rect.y + closestVerticalBlock.rect.height;
                velocity.y = 0;
                isOnGround = true;
                lastSafePosition.set(position.x, position.y);
            } else {
                position.y = closestVerticalBlock.rect.y - hitbox.height;
                velocity.y = 0;
            }
            hitbox.y = position.y;
        }

        position.x += velocity.x * delta;
        hitbox.x    = position.x;
        isOnRightWall = false;
        isOnLeftWall  = false;

        Block closestHorizontalBlock = null;
        float minOverlapX = Float.MAX_VALUE;
        boolean blockRight = false;

        for (Block block : blocks) {
            if (!hitbox.overlaps(block.rect)) continue;
            if (block.isDeadly) { takeDamageAndRespawn(); return; }
            if (!block.isSolid) continue;

            float overlap;
            if (velocity.x > 0) {
                overlap = (position.x + hitbox.width) - block.rect.x;
                if (overlap > 0 && overlap < minOverlapX) {
                    minOverlapX = overlap;
                    closestHorizontalBlock = block;
                    blockRight = true;
                }
            } else if (velocity.x < 0) {
                overlap = (block.rect.x + block.rect.width) - position.x;
                if (overlap > 0 && overlap < minOverlapX) {
                    minOverlapX = overlap;
                    closestHorizontalBlock = block;
                    blockRight = false;
                }
            }
        }


        if (closestHorizontalBlock != null) {
            if (blockRight) {
                position.x = closestHorizontalBlock.rect.x - hitbox.width;
                velocity.x = 0;
                isOnRightWall = true;
            } else {
                position.x = closestHorizontalBlock.rect.x + closestHorizontalBlock.rect.width;
                velocity.x = 0;
                isOnLeftWall = true;
            }
            hitbox.x = position.x;
        }

        boolean touchingWall = isOnRightWall || isOnLeftWall;
        boolean pressingIntoWall =
            (isOnRightWall && horizontalInput > 0) ||
                (isOnLeftWall  && horizontalInput < 0);

        isWallSliding = !isOnGround && !isDashing && !isKnockedBack && touchingWall
            && pressingIntoWall && velocity.y < 0;

        if (isWallSliding) {
            isFacingRight = isOnRightWall;
        }

        currentState = resolveState();
    }

    private void performWallJump() {
        float dir      = isOnRightWall ? -1f : 1f;
        velocity.y     = WALL_JUMP_SPEED_Y;
        velocity.x     = dir * WALL_JUMP_SPEED_X;
        isFacingRight  = (dir > 0);
        isOnGround     = false;
        isWallSliding  = false;
        hasDoubleJump  = true;
        wallJumpLockoutTimer = WALL_JUMP_LOCKOUT;
        wallJumpAnimTimer    = WALL_JUMP_ANIM;
        coyoteTimeCounter = 0;
        jumpBufferCounter = 0;
    }

    private void performDoubleJump() {
        velocity.y         = DOUBLE_JUMP_SPEED;
        hasDoubleJump      = false;
        doubleJumpAnimTimer = DOUBLE_JUMP_ANIM;
        coyoteTimeCounter  = 0;
        jumpBufferCounter  = 0;
    }

    public boolean isReadyToRespawn() {
        return isDead && deathTimer <= 0;
    }

    public void fullRespawn(Vector2 spawnPoint) {
        isDead = false;
        health = maxHealth;
        position.set(spawnPoint);
        lastSafePosition.set(spawnPoint);
        hitbox.setPosition(position);
        velocity.set(0, 0);
        hurtTimer = 0;
        attackTimer = 0;
        invincibilityTimer = 0;
        freezeTimer = 0;
    }

    // اسپل ها
    public class VengefulSpirit {
        public Rectangle hitbox;
        public float startX, startY;
        public float velocityX;
        public float lifeTimer;
        public float maxLife = 1.0f;
        public boolean isFacingRight;

        private Array<Enemy> hitEnemies = new Array<>();
        private boolean hitBoss = false;
        private boolean hitZote = false;

        public VengefulSpirit(float x, float y, boolean right) {
            this.startX = x;
            this.startY = y;
            this.hitbox = new Rectangle(x, y, 120, 80);
            this.isFacingRight = right;
            this.velocityX = right ? 900f : -900f;
            this.lifeTimer = maxLife;
        }

        public void update(float delta, Array<Enemy> enemies, FalseKnight boss, Zote zote) {
            hitbox.x += velocityX * delta;
            lifeTimer -= delta;

            if (enemies != null) {
                for (Enemy e : enemies) {
                    if (e.isAlive() && hitbox.overlaps(e.hitbox) && !hitEnemies.contains(e, true)) {
                        e.takeDamage(2);
                        hitEnemies.add(e);
                    }
                }
            }
            if (boss != null && !boss.isDead && hitbox.overlaps(boss.hitbox) && !hitBoss) {
                boss.takeDamage(2);
                hitBoss = true;
            }
            if (zote != null && hitbox.overlaps(zote.hitbox) && !hitZote) {
                zote.takeDamage();
                hitZote = true;
            }
        }
    }

    public class HowlingWraiths {
        public Rectangle hitbox;
        public float lifeTimer;
        public float maxLife = 0.65f;

        private int ticks = 0;
        private float tickTimer = 0f;

        public HowlingWraiths(float x, float y) {
            this.hitbox = new Rectangle(x - 60, y + 20, 140, 250);
            this.lifeTimer = maxLife;
        }

        public void update(float delta, Array<Enemy> enemies, FalseKnight boss, Zote zote) {
            lifeTimer -= delta;
            tickTimer -= delta;

            if (tickTimer <= 0 && ticks < 3) {
                ticks++;
                tickTimer = 0.2f;

                if (enemies != null) {
                    for (Enemy e : enemies) {
                        if (e.isAlive() && hitbox.overlaps(e.hitbox)) e.takeDamage(1);
                    }
                }
                if (boss != null && !boss.isDead && hitbox.overlaps(boss.hitbox)) boss.takeDamage(1);
                if (zote != null && hitbox.overlaps(zote.hitbox)) zote.takeDamage();
            }
        }
    }
}

