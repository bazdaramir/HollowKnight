package com.HollowKnight.model;

import com.HollowKnight.data.GameData;
import com.HollowKnight.model.enums.KnightState;
import com.HollowKnight.model.enums.Map;
import com.HollowKnight.model.manager.AudioManager;
import com.HollowKnight.model.manager.CharmManager;
import com.HollowKnight.model.manager.KnightEffectManager;
import com.HollowKnight.model.mob.Enemy;
import com.HollowKnight.model.mob.FalseKnight;
import com.HollowKnight.model.mob.Zote;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Knight {
    public static final float DASH_SPEED = 700f;
    public static final float DASH_DURATION = 0.45f;
    public static final float DASH_COOLDOWN = 1.2f;
    private static final float MOVE_SPEED = 400f;
    private static final float GRAVITY = 1500f;
    public Rectangle hitbox;
    private static final float FALL_GRAVITY_MULT = 1.6f;
    private static final float MAX_FALL_SPEED = 1000f;
    private static final float GROUND_STICK_SPEED = 60f;
    private static final float JUMP_SPEED = 900f;
    private static final float JUMP_CUT_MULT = 0.6f;
    private static final float COYOTE_TIME = 0.3f;
    private static final float JUMP_BUFFER_TIME = 0.12f;
    private static final float DOUBLE_JUMP_SPEED = 720f;
    private static final float DOUBLE_JUMP_ANIM = 0.32f;
    private static final float WALL_SLIDE_GRAVITY = 0.18f;
    private static final float WALL_JUMP_SPEED_Y = 780f;
    private static final float WALL_JUMP_SPEED_X = 380f;

    public int maxHealth = 5;
    public int health = maxHealth;
    public float hurtTimer = 0f;
    public boolean isDead = false;

    public float invincibilityTimer = 0f;
    public float freezeTimer = 0f;
    private float knockbackDirX = 0f;
    private static final float WALL_JUMP_LOCKOUT = 0.18f;

    public boolean isKnockedBack = false;
    public float deathTimer = 0f;
    public float castTimer = 0f;

    public Array<VengefulSpirit> fireballs = new Array<>();
    public Array<HowlingWraiths> wraiths = new Array<>();
    private static final float WALL_JUMP_ANIM = 0.30f;
    private static final float LANDING_DURATION = 0.16f;
    private static final float ATTACK_DURATION = 0.20f;
    private static final float MAX_DELTA_TIME = 1f / 30f;
    private static final float POGO_BOUNCE_SPEED = 850f;
    private static final float POGO_SAFE_WINDOW = 0.15f;
    public final int SOUL_PER_HIT = 11;
    private final float FOCUS_DURATION = 4.5f;
    public Vector2 position;
    public Vector2 velocity;
    public Vector2 lastSafePosition;
    public boolean godMode = false;
    public boolean noClip = false;
    public boolean isFacingRight = true;
    public boolean isDashing = false;
    public boolean isOnGround = false;
    public boolean isWallSliding = false;
    public boolean isOnRightWall = false;
    public boolean isOnLeftWall = false;
    private static final float WALL_SLIDE_MAX_FALL = 90f;
    public boolean isAttackingDown = false;
    public float horizontalInput = 0f;
    public float shakeIntensity = 0f;
    public float shakeDuration = 0f;
    public int soul = 33;
    private KnightEffectManager effectManager;
    private boolean EmergenyHealisActivated;
    private boolean hasUsedEmergencyHeal;
    private CharmManager charmManager;

    public int maxSoul = 99;
    private float pogoInvulnTimer = 0f;
    private Array<Enemy> dashedEnemies = new Array<>();
    public final int SOUL_FOCUS_COST = 33;

    public boolean isFocusing = false;
    private float focusTimer = 0f;
    private boolean dashedBoss = false;
    private float elapsedTime = 0f;
    private float coyoteTimeCounter = 0f;
    private float jumpBufferCounter = 0f;
    private float dashTimer = 0f;
    private float lastDashTime = -DASH_COOLDOWN;
    private boolean hasDoubleJump = true;
    private float doubleJumpAnimTimer = 0f;
    private float wallJumpLockoutTimer = 0f;
    private float wallJumpAnimTimer = 0f;
    private float landingTimer = 0f;
    private boolean wasOnGround = false;
    private float attackTimer = 0f;
    private boolean attackHitRegistered = false;
    private KnightState currentState = KnightState.IDLE;

    public Knight(float startX, float startY) {
        position = new Vector2(startX, startY);
        velocity = new Vector2(0, 0);
        hitbox = new Rectangle(startX, startY, 20, 30);
        lastSafePosition = new Vector2(startX, startY);
        hasUsedEmergencyHeal = false;
        EmergenyHealisActivated = false;
        effectManager = new KnightEffectManager();
    }

    public void setCharmManager(CharmManager charmManager) {
        this.charmManager = charmManager;
    }

    public GameData captureState() {
        GameData data = new GameData();
        data.health = this.health;
        data.maxHealth = this.maxHealth;
        data.soul = this.soul;
        data.maxSoul = this.maxSoul;
        data.x = this.position.x;
        data.y = this.position.y;
        return data;
    }

    public void loadFromSave(GameData data) {
        this.maxHealth = data.maxHealth;
        this.health = data.health;
        this.maxSoul = data.maxSoul;
        this.soul = data.soul;
        this.position.set(data.x, data.y);
        this.hitbox.setPosition(this.position);
        this.lastSafePosition.set(data.x, data.y);
    }

    public boolean hasCharm(String charmName) {
        return charmManager != null && charmManager.isEquipped(charmName);
    }

    public float getFocusProgress() {
        if (!isFocusing) return 0f;
        float targetFocusDuration = hasCharm("Quick Focus") ? FOCUS_DURATION * 0.6f : FOCUS_DURATION;
        return com.badlogic.gdx.math.MathUtils.clamp(focusTimer / targetFocusDuration, 0f, 1f);
    }

    public void castVengefulSpirit() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || isDashing || castTimer > 0 || isFocusing) return;

        if (soul >= SOUL_FOCUS_COST) {
            shakeIntensity = 12f;
            shakeDuration = 0.3f;
            soul -= SOUL_FOCUS_COST;
            castTimer = 0.4f;
            velocity.x = 0;
            velocity.y = 0;

            AudioManager.getInstance().KnightSoundHandler("fireball", null);

            float spawnX = isFacingRight ? position.x + hitbox.width : position.x - 120f;
            float spawnY = position.y - 10f;
            fireballs.add(new VengefulSpirit(spawnX, spawnY, isFacingRight));
        }
    }

    public void castHowlingWraiths() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || isDashing || castTimer > 0 || isFocusing) return;

        if (soul >= SOUL_FOCUS_COST) {
            soul -= SOUL_FOCUS_COST;
            shakeIntensity = 17f;
            shakeDuration = 0.5f;
            castTimer = 0.5f;
            velocity.x = 0;
            velocity.y = 0;

            AudioManager.getInstance().KnightSoundHandler("Howling_spell", null);

            wraiths.add(new HowlingWraiths(position.x, position.y));
        }
    }

    public void releaseJump() {
        if (velocity.y > 0) velocity.y *= JUMP_CUT_MULT;
    }

    public void moveRight() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || attackTimer > 0 || isFocusing) return;
        horizontalInput = 1f;
        isFacingRight = true;
        if (isDashing || wallJumpLockoutTimer > 0) return;
        velocity.x = MOVE_SPEED;
    }

    public void moveLeft() {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || attackTimer > 0 || isFocusing) return;
        horizontalInput = -1f;
        isFacingRight = false;
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
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || isFocusing) return;
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
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || isDashing || isFocusing) return;

        float currentDashCooldown = hasCharm("Dashmaster") ? DASH_COOLDOWN * 0.5f : DASH_COOLDOWN;
        if (elapsedTime - lastDashTime < currentDashCooldown) return;

        isDashing = true;

        AudioManager.getInstance().KnightSoundHandler("dash", null);

        dashedEnemies.clear();
        dashedBoss = false;

        dashTimer = hasCharm("Sharp Shadow") ? DASH_DURATION * 1.2f : DASH_DURATION;
        velocity.x = isFacingRight ? DASH_SPEED : -DASH_SPEED;
        velocity.y = 0;
        lastDashTime = elapsedTime;
    }

    public void startFocus() {
        if (isOnGround && !isDashing && attackTimer <= 0 && hurtTimer <= 0 && health < maxHealth && soul >= SOUL_FOCUS_COST && !isDead) {
            if (!isFocusing) {
                isFocusing = true;
                velocity.x = 0;
                AudioManager.getInstance().KnightSoundHandler("focus", null);
            }
        }
    }

    public void stopFocus() {
        if (isFocusing) {
            isFocusing = false;
            focusTimer = 0f;
            AudioManager.getInstance().KnightSoundHandler("focus_stop", null);
        }
    }

    public void attack(Array<Enemy> enemies, FalseKnight boss, Zote zote, Array<Block> blocks, boolean isDownHeld) {
        if (isDead || freezeTimer > 0 || hurtTimer > 0 || isKnockedBack || attackTimer > 0 || isDashing || isFocusing)
            return;

        attackTimer = hasCharm("Quick Slash") ? ATTACK_DURATION * 0.6f : ATTACK_DURATION;
        velocity.x = 0;
        attackHitRegistered = false;

        AudioManager.getInstance().KnightSoundHandler("slash", null);

        isAttackingDown = (!isOnGround && isDownHeld);
        tryRegisterAttackHit(enemies, boss, zote, blocks);
    }

    public void ActivateEmergencyHeal() {
        EmergenyHealisActivated = true;
    }

    private void tryRegisterAttackHit(Array<Enemy> enemies, FalseKnight boss, Zote zote, Array<Block> blocks) {
        if (attackHitRegistered) return;

        float slashWidth, slashHeight, slashX, slashY;

        if (isAttackingDown) {
            slashWidth = 350f;
            slashHeight = 250f;
            slashX = position.x + (hitbox.width / 2f) - (slashWidth / 2f) + 200;
            slashY = position.y - slashHeight - 55f;
        } else {
            slashWidth = 160f;
            slashHeight = 100f;
            slashX = isFacingRight ? position.x + hitbox.width : position.x - slashWidth;
            slashY = position.y + 10f;
        }

        Rectangle slashHitbox = new Rectangle(slashX, slashY, slashWidth, slashHeight);
        boolean hitSomething = false;
        boolean pogoValidHit = false;

        int nailDamage = hasCharm("Unbreakable Strength") ? 2 : 1;

        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && slashHitbox.overlaps(enemy.hitbox)) {
                    enemy.takeDamage(nailDamage, position.x + hitbox.width / 2f);
                    hitSomething = true;
                    pogoValidHit = true;
                }
            }
        }
        if (boss != null && !boss.isDead && slashHitbox.overlaps(boss.hitbox)) {
            boss.takeDamage(nailDamage);
            hitSomething = true;
            pogoValidHit = true;
        }
        if (zote != null && slashHitbox.overlaps(zote.hitbox)) {
            zote.takeDamage();
            hitSomething = true;
        }

        if (isAttackingDown && blocks != null) {
            for (Block block : blocks) {
                if (block.isDeadly && slashHitbox.overlaps(block.rect)) {
                    hitSomething = true;
                    pogoValidHit = true;
                    break;
                }
            }
        }
        if (blocks != null) {
            for (int i = blocks.size - 1; i >= 0; i--) {
                Block block = blocks.get(i);

                if (block.isBreakable && slashHitbox.overlaps(block.rect)) {
                    block.health -= nailDamage;
                    hitSomething = true;

                    if (block.health <= 0) {
                    }
                }
            }
        }

        if (hitSomething) {
            AudioManager.getInstance().KnightSoundHandler("enemy_damage", null);

            int soulGain = hasCharm("Soul Catcher") ? SOUL_PER_HIT + 5 : SOUL_PER_HIT;

            if (soul < maxSoul && soul + soulGain >= maxSoul) {
                AudioManager.getInstance().FullSoulSound();
            }

            soul = Math.min(soul + soulGain, maxSoul);
            attackHitRegistered = true;
        }

        if (isAttackingDown && pogoValidHit) {
            velocity.y = POGO_BOUNCE_SPEED;
            pogoInvulnTimer = POGO_SAFE_WINDOW;
            hasDoubleJump = true;
            lastDashTime = elapsedTime - DASH_COOLDOWN;
        }
    }

    public void takeDamage(int amount, float hitSourceX) {
        if (godMode || isDead || invincibilityTimer > 0 || isDashing || pogoInvulnTimer > 0) return;
        health -= amount;
        invincibilityTimer = 1.5f;
        freezeTimer = 0.12f;

        shakeIntensity = 12f;
        shakeDuration = 0.35f;

        stopFocus();

        attackTimer = 0;
        isWallSliding = false;

        if (health <= 0) {
            if (EmergenyHealisActivated && !hasUsedEmergencyHeal) {
                hasUsedEmergencyHeal = true;
                health += 1;
                AudioManager.getInstance().KnightSoundHandler("focusdone", null);

            } else {
                health = 0;
                isDead = true;
                velocity.set(0, 0);
                deathTimer = 1.5f;
                AudioManager.getInstance().KnightSoundHandler("knight_death", null);
            }
        } else {
            isKnockedBack = true;
            float myCenterX = position.x + hitbox.width / 2f;
            knockbackDirX = (hitSourceX < myCenterX) ? 350f : -350f;
            AudioManager.getInstance().KnightSoundHandler("knight_damage", null);
        }
    }

    public void takeDamage(int amount) {
        takeDamage(amount, position.x);
    }

    public boolean isFlashing() {
        return invincibilityTimer > 0 && (int) (invincibilityTimer * 15) % 2 == 0;
    }

    private KnightState resolveState(Map map) {
        if (isDead) {
            AudioManager.getInstance().KnightSoundHandler("idle", map);
            return KnightState.DEATH;
        }
        if (hurtTimer > 0) {
            AudioManager.getInstance().KnightSoundHandler("idle", map);
            return KnightState.IDLE_HURT;
        }
        if (isDashing) {
            AudioManager.getInstance().KnightSoundHandler("idle", map);
            return KnightState.DASHING;
        }
        if (isFocusing) {
            AudioManager.getInstance().KnightSoundHandler("idle", map);
            return KnightState.FOCUS;
        }

        if (attackTimer > 0) {
            AudioManager.getInstance().KnightSoundHandler("idle", map);
            if (isAttackingDown) return KnightState.DOWN_SLASH;
            return KnightState.ATTACKING;
        }
        if (castTimer > 0){
            return KnightState.CAST_SPELL;
        }

        if (wallJumpAnimTimer > 0) {
            AudioManager.getInstance().KnightSoundHandler("idle", map);
            return KnightState.WALL_JUMPING;
         }
        if (landingTimer > 0) {
            AudioManager.getInstance().KnightSoundHandler("idle", map);
            return KnightState.LANDING;
        }
        if (doubleJumpAnimTimer > 0) {
            AudioManager.getInstance().KnightSoundHandler("idle", map);
            return KnightState.DOUBLE_JUMPING;
        }
        if (isWallSliding) return KnightState.WALL_SLIDING;

        if (!isOnGround) {
            AudioManager.getInstance().KnightSoundHandler("idle", map);
            return velocity.y > 0 ? KnightState.JUMPING : KnightState.FALLING;
        }

        if (Math.abs(velocity.x) > 0.1f) {
            AudioManager.getInstance().KnightSoundHandler("run", map);
            return KnightState.RUNNING;
        }

        AudioManager.getInstance().KnightSoundHandler("idle", map);
        return KnightState.IDLE;
    }

    private void takeDamageAndRespawn() {
        if (isDead) return;

        if (invincibilityTimer <= 0 && pogoInvulnTimer <= 0) {
            takeDamage(1, position.x);
        }

        if (!isDead) {
            position.set(lastSafePosition);
            hitbox.setPosition(position);
            velocity.set(0, 0);

            isDashing = false;
            isWallSliding = false;
            isKnockedBack = false;
            hurtTimer = 0.5f;
            freezeTimer = 0;

            doubleJumpAnimTimer = 0;
            attackTimer = 0;
            wallJumpAnimTimer = 0;
            landingTimer = 0;
            hasDoubleJump = true;
        }
    }

    public KnightState getState() { return currentState; }

    public void update(float delta, Array<Block> blocks, Array<Enemy> enemies, FalseKnight boss, Zote zote, Map map) {
        if (noClip) {
            float flySpeed = 800f;
            velocity.set(0, 0);
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) velocity.x = flySpeed;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) velocity.x = -flySpeed;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) velocity.y = flySpeed;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) velocity.y = -flySpeed;

            position.x += velocity.x * delta;
            position.y += velocity.y * delta;
            hitbox.setPosition(position);
            return;
        }

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
        if (pogoInvulnTimer > 0) pogoInvulnTimer -= delta;

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

        if (isDashing && hasCharm("Sharp Shadow")) {
            if (enemies != null) {
                for (Enemy enemy : enemies) {
                    if (enemy.isAlive() && hitbox.overlaps(enemy.hitbox) && !dashedEnemies.contains(enemy, true)) {
                        enemy.takeDamage(1, position.x + hitbox.width / 2f);
                        dashedEnemies.add(enemy);
                    }
                }
            }
            if (boss != null && !boss.isDead && hitbox.overlaps(boss.hitbox) && !dashedBoss) {
                boss.takeDamage(1);
                dashedBoss = true;
            }
        }

        if (invincibilityTimer <= 0 && pogoInvulnTimer <= 0 && !isDashing && !isDead) {
            boolean tookDamage = false;
            float damageSourceX = 0f;

            if (enemies != null) {
                for (Enemy enemy : enemies) {
                    if (enemy.isAlive() && hitbox.overlaps(enemy.hitbox)) {
                        tookDamage = true;
                        damageSourceX = enemy.position.x + (enemy.hitbox.width / 2f);
                        break;
                    }
                }
            }

            if (!tookDamage && boss != null && !boss.isDead && hitbox.overlaps(boss.hitbox)) {
                tookDamage = true;
                damageSourceX = boss.position.x + (boss.hitbox.width / 2f);
            }

            if (tookDamage) {
                takeDamage(1, damageSourceX);
            }
        }

        if (attackTimer > 0) {
            tryRegisterAttackHit(enemies, boss, zote, blocks);
        }

        if (deathTimer > 0) deathTimer -= delta;
        if (hurtTimer > 0) hurtTimer -= delta;
        if (attackTimer > 0) attackTimer -= delta;
        if (wallJumpLockoutTimer > 0) wallJumpLockoutTimer -= delta;
        if (wallJumpAnimTimer > 0) wallJumpAnimTimer -= delta;
        if (doubleJumpAnimTimer > 0) doubleJumpAnimTimer -= delta;
        if (jumpBufferCounter > 0) jumpBufferCounter -= delta;

        if (isOnGround) coyoteTimeCounter = COYOTE_TIME;
        else coyoteTimeCounter -= delta;

        if (isOnGround && !wasOnGround) {
            if (isKnockedBack) {
                isKnockedBack = false;
                hurtTimer = 0.5f;
                velocity.x = 0;
            } else {
                landingTimer = LANDING_DURATION;
                hasDoubleJump = true;
            }
        }
        wasOnGround = isOnGround;
        if (landingTimer > 0) landingTimer -= delta;

        if (jumpBufferCounter > 0 && coyoteTimeCounter > 0 && !isDashing) {
            velocity.y = JUMP_SPEED;
            isOnGround = false;
            jumpBufferCounter = 0;
            coyoteTimeCounter = 0;

            AudioManager.getInstance().KnightSoundHandler("jump", map);
        }

        if (isFocusing) {
            velocity.x = 0;
            if (isOnGround) {
                focusTimer += delta;

                float targetFocusDuration = hasCharm("Quick Focus") ? FOCUS_DURATION * 0.6f : FOCUS_DURATION;

                if (focusTimer >= targetFocusDuration) {
                    health = Math.min(health + 1, maxHealth);
                    soul -= SOUL_FOCUS_COST;
                    focusTimer = 0f;
                    System.out.println("Healed 1HP  HP: " + health);
                    AudioManager.getInstance().KnightSoundHandler("focusdone", map);

                    if (soul < SOUL_FOCUS_COST || health == maxHealth) {
                        stopFocus();
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

        if (isOnGround) {
            velocity.y = -GROUND_STICK_SPEED;
        } else if (isDashing || castTimer > 0) {
            velocity.y = 0;
        } else if (isWallSliding) {
            velocity.y -= GRAVITY * WALL_SLIDE_GRAVITY * delta;
            if (velocity.y < -WALL_SLIDE_MAX_FALL) velocity.y = -WALL_SLIDE_MAX_FALL;
        } else {
            float gMult = (velocity.y < 0) ? FALL_GRAVITY_MULT : 1f;
            velocity.y -= GRAVITY * gMult * delta;
            velocity.y = Math.max(velocity.y, -MAX_FALL_SPEED);
        }

        if (isDashing) {
            dashTimer -= delta;
            if (dashTimer <= 0) isDashing = false;
        }
        position.y += velocity.y * delta;
        hitbox.y = position.y;
        isOnGround = false;

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
        hitbox.x = position.x;
        isOnRightWall = false;
        isOnLeftWall = false;

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
                (isOnLeftWall && horizontalInput < 0);

        boolean wasWallSliding = isWallSliding;
        isWallSliding = !isOnGround && !isDashing && !isKnockedBack && touchingWall
            && pressingIntoWall && velocity.y < 0;

        if (isWallSliding && !wasWallSliding) {
            AudioManager.getInstance().KnightSoundHandler("Wall_slide", map);
        }

        if (isWallSliding) {
            isFacingRight = isOnRightWall;
        }

        currentState = resolveState(map);
    }

    private void performWallJump() {
        float dir = isOnRightWall ? -1f : 1f;
        velocity.y = WALL_JUMP_SPEED_Y;
        velocity.x = dir * WALL_JUMP_SPEED_X;
        isFacingRight = (dir > 0);
        isOnGround = false;
        isWallSliding = false;
        hasDoubleJump = true;
        wallJumpLockoutTimer = WALL_JUMP_LOCKOUT;
        wallJumpAnimTimer = WALL_JUMP_ANIM;
        coyoteTimeCounter = 0;
        jumpBufferCounter = 0;

        AudioManager.getInstance().KnightSoundHandler("Wall_jump", null);
    }

    private void performDoubleJump() {
        velocity.y = DOUBLE_JUMP_SPEED;
        hasDoubleJump = false;
        doubleJumpAnimTimer = DOUBLE_JUMP_ANIM;
        coyoteTimeCounter = 0;
        jumpBufferCounter = 0;

        AudioManager.getInstance().KnightSoundHandler("Double_jump", null);
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
        pogoInvulnTimer = 0;
        freezeTimer = 0;
    }

    public class VengefulSpirit {
        public Rectangle hitbox;
        public float startX, startY;
        public float velocityX;
        public float lifeTimer;
        public float maxLife = 4.0f;
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

            int spellDamage = hasCharm("Void Heart") ? 3 : 2;

            if (enemies != null) {
                for (Enemy e : enemies) {
                    if (e.isAlive() && hitbox.overlaps(e.hitbox) && !hitEnemies.contains(e, true)) {
                        e.takeDamage(spellDamage);
                        hitEnemies.add(e);
                    }
                }
            }
            if (boss != null && !boss.isDead && hitbox.overlaps(boss.hitbox) && !hitBoss) {
                boss.takeDamage(spellDamage);
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
            this.hitbox = new Rectangle(x - 60, y + 20, 240, 290);
            this.lifeTimer = maxLife;
        }

        public void update(float delta, Array<Enemy> enemies, FalseKnight boss, Zote zote) {
            lifeTimer -= delta;
            tickTimer -= delta;

            if (tickTimer <= 0 && ticks < 3) {
                ticks++;
                tickTimer = 0.2f;

                int spellDamage = hasCharm("Void Heart") ? 2 : 1;

                if (enemies != null) {
                    for (Enemy e : enemies) {
                        if (e.isAlive() && hitbox.overlaps(e.hitbox)) e.takeDamage(spellDamage);
                    }
                }
                if (boss != null && !boss.isDead && hitbox.overlaps(boss.hitbox)) boss.takeDamage(spellDamage);
                if (zote != null && hitbox.overlaps(zote.hitbox)) zote.takeDamage();
            }
        }
    }
}
