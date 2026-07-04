package com.HollowKnight.model.mob;

import com.HollowKnight.model.Block;
import com.HollowKnight.model.Knight;
import com.HollowKnight.model.enums.FalseKnightStation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.Random;

public class FalseKnight {
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle hitbox;
    public boolean isFacingRight = false;

    private final int maxHealth = 60;
    public int health = maxHealth;
    private boolean isPhase2 = false;
    public boolean isDead = false;

    private FalseKnightStation station = FalseKnightStation.IDLE;
    private float stateTimer = 0f;
    public float deathTimer = 3.0f;
    private float deathSeqTimer = 0f;
    private float stunTimer = 0f;
    private float hitCounterTimer = 0f;
    private int recentHits = 0;
    private int lastMove = -1;

    private float contactDamageCooldown = 0f;
    private static final float CONTACT_DAMAGE_INTERVAL = 0.5f;

    private final float gravity = 1500f;
    private float moveSpeed = 200f;
    private final float fullHitboxHeight = 160f;
    private final float stunHitboxHeight = 80f;
    private boolean onGround = false;
    private final Random random = new Random();


    private static final float GROUND_ACCEL = 1400f;

    public FalseKnight(float startX, float startY) {
        position = new Vector2(startX, startY);
        velocity = new Vector2(0, 0);
        hitbox = new Rectangle(startX, startY, 120f, fullHitboxHeight);
    }

    public void update(float delta, Array<Block> blocks, Knight knight) {
        if (contactDamageCooldown > 0) contactDamageCooldown -= delta;

        if (isDead) {
            applyPhysics(delta, blocks);
            processDeathSequence(delta);
            return;
        }

        if (hitCounterTimer > 0) hitCounterTimer -= delta;
        if (hitCounterTimer <= 0) recentHits = 0;

        applyPhysics(delta, blocks);
        processAI(delta, knight, blocks);
        checkContactDamage(knight);
    }

    private void processDeathSequence(float delta) {
        switch (station) {
            case DEATH_HIT:
                deathSeqTimer -= delta;
                if (deathSeqTimer <= 0) {
                    station = FalseKnightStation.DEATH_FALL;
                    deathSeqTimer = 0.36f;
                }
                break;
            case DEATH_FALL:
                deathSeqTimer -= delta;
                if (deathSeqTimer <= 0) {
                    station = FalseKnightStation.DEATH;
                }
                break;
            case DEATH:
                deathTimer -= delta;
                break;
            default:
                break;
        }
    }

    private void processAI(float delta, Knight knight, Array<Block> blocks) {
        if (station == FalseKnightStation.STUN) {
            moveTowardsHorizontal(0f, delta);
            stunTimer -= delta;
            if (stunTimer <= 0) {
                station = FalseKnightStation.STUN_RECOVER;
                stateTimer = 0.75f;
            }
            return;
        }

        if (station == FalseKnightStation.STUN_RECOVER) {
            stateTimer -= delta;
            if (stateTimer <= 0) {
                isPhase2 = true;
                moveSpeed = 280f;
                restoreFullHitbox(blocks);
                station = FalseKnightStation.IDLE;
                stateTimer = 0.5f;
            }
            return;
        }

        if (knight != null && station == FalseKnightStation.IDLE) {
            isFacingRight = (knight.position.x > position.x);
        }

        switch (station) {
            case IDLE:
                moveTowardsHorizontal(0f, delta);
                stateTimer -= delta;
                if (stateTimer <= 0 && knight != null) {
                    decideNextMove(knight);
                }
                break;

            case ATTACK_ANTIC:
                moveTowardsHorizontal(0f, delta);
                stateTimer -= delta;
                if (stateTimer <= 0) {
                    station = FalseKnightStation.ATTACK;
                    stateTimer = isPhase2 ? 0.11f : 0.15f;
                }
                break;

            case RUN_ANTIC:
                moveTowardsHorizontal(0f, delta);
                stateTimer -= delta;
                if (stateTimer <= 0) {
                    station = FalseKnightStation.RUN;
                    stateTimer = 1.5f;
                }
                break;

            case JUMP_ANTIC:
                moveTowardsHorizontal(0f, delta);
                stateTimer -= delta;
                if (stateTimer <= 0) {
                    station = FalseKnightStation.JUMP;
                    velocity.y = 900f;
                    velocity.x = isFacingRight ? 250f : -250f; // launch impulse: instant, not eased
                    onGround = false;
                }
                break;

            case RUN:
                moveTowardsHorizontal(isFacingRight ? moveSpeed : -moveSpeed, delta);
                stateTimer -= delta;
                if (stateTimer <= 0) resetToIdle();
                break;

            case ATTACK:
                moveTowardsHorizontal(isFacingRight ? 80f : -80f, delta);
                stateTimer -= delta;
                if (stateTimer <= 0) {
                    station = FalseKnightStation.ATTACK_RECOVER;
                    stateTimer = isPhase2 ? 0.36f : 0.5f;
                }
                break;

            case ATTACK_RECOVER:
                moveTowardsHorizontal(0f, delta);
                stateTimer -= delta;
                if (stateTimer <= 0) resetToIdle();
                break;

            case JUMP:
            case DEFENSIVE_LEAP:
                if (onGround && velocity.y <= 0) {
                    velocity.x = 0;
                    if (station == FalseKnightStation.JUMP && isPhase2) {
                        station = FalseKnightStation.JUMP_ATTACK;
                        stateTimer = 0.64f / 1.4f;
                    } else {
                        station = FalseKnightStation.LAND;
                        stateTimer = isPhase2 ? (0.4f / 1.4f) : 0.4f;
                    }
                }
                break;

            case LAND:
            case JUMP_ATTACK:
                moveTowardsHorizontal(0f, delta);
                stateTimer -= delta;
                if (stateTimer <= 0) resetToIdle();
                break;
            default:
                break;
        }
    }


    private void moveTowardsHorizontal(float targetVx, float delta) {
        float maxDelta = GROUND_ACCEL * delta;
        if (velocity.x < targetVx) {
            velocity.x = Math.min(velocity.x + maxDelta, targetVx);
        } else if (velocity.x > targetVx) {
            velocity.x = Math.max(velocity.x - maxDelta, targetVx);
        }
    }

    private void decideNextMove(Knight knight) {
        float distance = Math.abs(knight.position.x - position.x);
        int chosenMove;

        if (recentHits >= 3) {
            if (lastMove == 5) {
                chosenMove = random.nextBoolean() ? 1 : 2;
            } else {
                chosenMove = 5;
            }
            recentHits = 0;
        } else {
            do {
                if (distance > 400f) {
                    chosenMove = random.nextBoolean() ? 2 : 3;
                } else if (distance < 150f) {
                    chosenMove = random.nextInt(100) < 70 ? 1 : 5;
                } else {
                    chosenMove = random.nextInt(3) + 1;
                }
            } while (chosenMove == lastMove);
        }

        lastMove = chosenMove;
        executeMove(chosenMove);
    }

    private void executeMove(int move) {
        switch (move) {
            case 1:
                station = FalseKnightStation.ATTACK_ANTIC;
                stateTimer = isPhase2 ? 0.34f : 0.48f;
                break;
            case 2:
                station = FalseKnightStation.RUN_ANTIC;
                stateTimer = isPhase2 ? 0.14f : 0.2f;
                break;
            case 3:
                station = FalseKnightStation.JUMP_ANTIC;
                stateTimer = 0.1f;
                break;
            case 5:
                station = FalseKnightStation.DEFENSIVE_LEAP;
                velocity.y = 600f;
                velocity.x = isFacingRight ? -350f : 350f; // launch impulse: instant
                onGround = false;
                break;
        }
    }

    private void resetToIdle() {
        station = FalseKnightStation.IDLE;
        stateTimer = isPhase2 ? 0.3f : 0.6f;
    }

    private void checkContactDamage(Knight knight) {
        if (knight != null && hitbox.overlaps(knight.hitbox) && !isDead
            && station != FalseKnightStation.STUN && contactDamageCooldown <= 0) {
            float bossCenterX = position.x + hitbox.width / 2f;
            knight.takeDamage(2, bossCenterX);
            contactDamageCooldown = CONTACT_DAMAGE_INTERVAL;
        }
    }

    public void takeDamage(int amount) {
        if (isDead || station == FalseKnightStation.STUN_RECOVER) return;

        health -= amount;
        recentHits++;
        hitCounterTimer = 2.0f;

        if (!isPhase2 && health <= maxHealth / 2 && station != FalseKnightStation.STUN) {
            station = FalseKnightStation.STUN;
            stunTimer = 4.0f;
            velocity.set(0, 0);
            hitbox.height = stunHitboxHeight;
            hitbox.y = position.y;
        } else if (health <= 0) {
            health = 0;
            isDead = true;
            station = FalseKnightStation.DEATH_HIT;
            deathSeqTimer = 0.36f;
            velocity.x = 0;
        }
    }

    private void restoreFullHitbox(Array<Block> blocks) {
        hitbox.height = fullHitboxHeight;
        hitbox.y = position.y;

        if (blocks == null) return;
        for (Block block : blocks) {
            if (block.isSolid && hitbox.overlaps(block.rect)) {
                float overlapTop = (hitbox.y + hitbox.height) - block.rect.y;
                if (overlapTop > 0) {
                    position.y -= overlapTop;
                    hitbox.y = position.y;
                }
            }
        }
    }

    private void applyPhysics(float delta, Array<Block> blocks) {
        if (!onGround) velocity.y -= gravity * delta;

        position.x += velocity.x * delta;
        hitbox.x = position.x;

        for (Block block : blocks) {
            if (block.isSolid && hitbox.overlaps(block.rect)) {
                if (velocity.x > 0) position.x = block.rect.x - hitbox.width;
                else if (velocity.x < 0) position.x = block.rect.x + block.rect.width;
                velocity.x = 0;
                hitbox.x = position.x;
            }
        }

        position.y += velocity.y * delta;
        hitbox.y = position.y;
        onGround = false;

        for (Block block : blocks) {
            if (block.isSolid && hitbox.overlaps(block.rect)) {
                if (velocity.y <= 0) {
                    position.y = block.rect.y + block.rect.height;
                    onGround = true;
                    velocity.y = 0;
                } else {
                    position.y = block.rect.y - hitbox.height;
                    velocity.y = 0;
                }
                hitbox.y = position.y;
            }
        }
    }

    public FalseKnightStation getStation() { return station; }
    public boolean isPhase2() { return isPhase2; }
}
