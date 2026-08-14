package com.HollowKnight.controller;

import com.HollowKnight.model.Block;
import com.HollowKnight.model.Knight;
import com.HollowKnight.model.mob.Enemy;
import com.HollowKnight.model.mob.FalseKnight;
import com.HollowKnight.model.mob.Zote;
import com.HollowKnight.model.manager.KeyBindingManager;
import com.HollowKnight.view.PopupOverlay;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;

public class KnightController {

    private final Knight knight;
    private boolean wasJumpKeyDown = false;

    public boolean requestBossTeleport = false;

    public KnightController(Knight knight) {
        this.knight = knight;
    }

    public void handleInput(Array<Enemy> enemies, FalseKnight boss, Zote zote, Array<Block> mapBlocks, PopupOverlay popupOverlay) {
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT)) {

            //  (Emergency Heal)
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.H)) {
                knight.ActivateEmergencyHeal();
            }
            //  (Refill Soul)
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
                knight.soul = knight.maxSoul;
            }
            //  (God Mode)
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.G)) {
                knight.godMode = !knight.godMode;
                System.out.println("God Mode: " + knight.godMode);
            }
            //  (Noclip)
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.N)) {
                knight.noClip = !knight.noClip;
                System.out.println("Noclip: " + knight.noClip);
            }
            // انتقال به اتاق باس
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.T)) {
                requestBossTeleport = true;
            }
            //  (Insta-Kill)
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.K)) {
                for (Enemy enemy : enemies) {
                    enemy.takeDamage(10000, knight.position.x);
                }
            }
        } else {
            KeyBindingManager keys = KeyBindingManager.getInstance();

            if (Gdx.input.isKeyPressed(keys.get(KeyBindingManager.RIGHT))) {
                knight.moveRight();
            } else if (Gdx.input.isKeyPressed(keys.get(KeyBindingManager.LEFT))) {
                knight.moveLeft();
            } else {
                knight.stopMoving();
            }

            // kump Z
            if (Gdx.input.isKeyJustPressed(keys.get(KeyBindingManager.JUMP))) {
                knight.requestJump();
            }
            boolean isJumpKeyDown = Gdx.input.isKeyPressed(keys.get(KeyBindingManager.JUMP));
            if (wasJumpKeyDown && !isJumpKeyDown) {
                knight.releaseJump();
            }
            wasJumpKeyDown = isJumpKeyDown;
            boolean isDownHeld = Gdx.input.isKeyPressed(keys.get(KeyBindingManager.DOWN));

            // dash C
            if (Gdx.input.isKeyJustPressed(keys.get(KeyBindingManager.DASH))) {
                knight.dash();
            }

            //attack X
            if (Gdx.input.isKeyJustPressed(keys.get(KeyBindingManager.ATTACK))) {
                knight.attack(enemies, boss, zote, mapBlocks, isDownHeld);
            }

            if (Gdx.input.isKeyPressed(keys.get(KeyBindingManager.FOCUS))) {
                knight.startFocus();
            } else {
                if (knight.isFocusing) {
                    knight.stopFocus();
                }
            }
            if (Gdx.input.isKeyJustPressed(keys.get(KeyBindingManager.FIREBALL))) {
                knight.castVengefulSpirit();
            }
            if (Gdx.input.isKeyJustPressed(keys.get(KeyBindingManager.SCREAM))) {
                knight.castHowlingWraiths();

            }
            if (Gdx.input.isKeyJustPressed(keys.get(KeyBindingManager.INVENTORY))) {
                popupOverlay.showInventoryMenu();
            }

            if (knight.isFocusing) {
                return;
            }
        }
    }
}
