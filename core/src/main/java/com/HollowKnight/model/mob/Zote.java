package com.HollowKnight.model.mob;

import com.HollowKnight.model.Block;
import com.HollowKnight.model.Knight;
import com.HollowKnight.model.Translator;
import com.HollowKnight.model.enums.ZoteStation;
import com.HollowKnight.model.manager.AudioManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Zote {
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle hitbox;
    public boolean isFacingRight = false;

    private ZoteStation station = ZoteStation.IDLE;
    private float stateTimer = 0f;
    private final float gravity = 1500f;
    private final float MAX_FALL_SPEED = 1200f;
    private boolean onGround = false;

    public boolean isInteracting = false;
    private int dialoguePhase = 0;
    private int currentLineIndex = 0;

    private final String[] introKeys = {
        "ZOTE_INTRO_1",
        "ZOTE_INTRO_2"
    };

    private final String[] exhaustedKeys = {
        "ZOTE_EXHAUSTED_1",
        "ZOTE_EXHAUSTED_2"
    };

    public String displayedText = "";
    private int charIndex = 0;
    private float typeTimer = 0f;
    private final float TYPE_SPEED = 0.05f;

    public boolean isAngry = false;
    private float angryTimer = 0f;
    private final float RUN_SPEED = 120f;

    public Zote(float startX, float startY) {
        position = new Vector2(startX, startY);
        velocity = new Vector2(0, 0);
        hitbox = new Rectangle(startX, startY, 40f, 50f);
    }

    public void update(float delta, Array<Block> blocks, Knight knight) {
        applyPhysics(delta, blocks);

        if (isAngry) {
            angryTimer -= delta;
            if (knight != null) {
                isFacingRight = knight.position.x > position.x;
                velocity.x = isFacingRight ? RUN_SPEED : -RUN_SPEED;
            }

            if (angryTimer <= 0) {
                isAngry = false;
                velocity.x = 0;
                station = ZoteStation.FALL;
                stateTimer = 0.6f;
                AudioManager.getInstance().ZoteSoundHandler("fall");
            }
        }

        else if (station == ZoteStation.FALL || station == ZoteStation.GET_UP) {
            stateTimer -= delta;
            velocity.x = 0;
            if (stateTimer <= 0) {
                if (station == ZoteStation.FALL) {
                    station = ZoteStation.GET_UP;
                    stateTimer = 0.6f;
                    AudioManager.getInstance().ZoteSoundHandler("getup");
                } else {
                    station = ZoteStation.IDLE;
                }
            }
        }

        else if (isInteracting) {
            velocity.x = 0;
            String currentLine = getCurrentFullLine();
            if (charIndex < currentLine.length()) {
                typeTimer += delta;
                if (typeTimer >= TYPE_SPEED) {
                    typeTimer = 0f;
                    charIndex++;
                    displayedText = currentLine.substring(0, charIndex);
                }
            }

            if (station == ZoteStation.IDLE && charIndex < currentLine.length()) {
                station = ZoteStation.TALK;
            } else if (station == ZoteStation.TALK && charIndex >= currentLine.length()) {
                station = ZoteStation.IDLE;
            }
        }

        else {
            velocity.x = 0;
            station = ZoteStation.IDLE;
        }
    }

    public void interact(Knight knight) {
        if (isAngry || station == ZoteStation.FALL || station == ZoteStation.GET_UP) return;

        isFacingRight = knight.position.x > position.x;

        if (!isInteracting) {
            isInteracting = true;
            resetTypewriter();
            playZoteGrumbleSFX();
        } else {
            String currentLine = getCurrentFullLine();

            if (charIndex < currentLine.length()) {
                charIndex = currentLine.length();
                displayedText = currentLine;
            } else {
                currentLineIndex++;

                if (dialoguePhase == 0 && currentLineIndex >= introKeys.length) {
                    isInteracting = false;
                    dialoguePhase = 1;
                    currentLineIndex = 0;
                    station = ZoteStation.IDLE;
                } else if (dialoguePhase == 1 && currentLineIndex >= exhaustedKeys.length) {
                    isInteracting = false;
                    currentLineIndex = 0;
                    station = ZoteStation.IDLE;
                } else {
                    resetTypewriter();
                    playZoteGrumbleSFX();
                }
            }
        }
    }

    private String getCurrentFullLine() {
        if (dialoguePhase == 0) {
            return Translator.getText(introKeys[currentLineIndex]);
        } else {
            return Translator.getText(exhaustedKeys[currentLineIndex]);
        }
    }

    public String getDreamNailDialogue() {
        return Translator.getText("ZOTE_DREAM");
    }

    private void resetTypewriter() {
        displayedText = "";
        charIndex = 0;
        typeTimer = 0f;
        station = ZoteStation.TALK;
    }

    private void playZoteGrumbleSFX() {
        int randomTalk = com.badlogic.gdx.math.MathUtils.random(0, 4);
        AudioManager.getInstance().ZoteSoundHandler(String.valueOf(randomTalk));
    }

    public void takeDamage() {
        if (isAngry || station == ZoteStation.FALL || station == ZoteStation.GET_UP) return;

        isInteracting = false;
        isAngry = true;
        angryTimer = 7.3f;
        station = ZoteStation.ATTACK;

        AudioManager.getInstance().ZoteSoundHandler("attack");

        velocity.y = 350f;
        onGround = false;
    }

    public boolean isPlayerInRange(Knight knight) {
        float distance = Math.abs(knight.position.x - position.x);
        return distance < 100f && Math.abs(knight.position.y - position.y) < 50f;
    }

    private void applyPhysics(float delta, Array<Block> blocks) {
        if (!onGround) {
            velocity.y -= gravity * delta;
            velocity.y = Math.max(velocity.y, -MAX_FALL_SPEED);
        }

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

    public ZoteStation getStation() { return station; }
}
