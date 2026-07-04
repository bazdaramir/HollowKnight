package com.HollowKnight.controller;

import com.HollowKnight.model.Knight;
import com.HollowKnight.model.mob.Enemy;
import com.HollowKnight.model.mob.FalseKnight;
import com.HollowKnight.model.mob.Zote;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;

public class KnightController {

    private final Knight knight;

    private boolean wasJumpKeyDown = false;

    public KnightController(Knight knight) {
        this.knight = knight;
    }

    public void handleInput(Array<Enemy> enemies, FalseKnight boss, Zote zote) {

        // چپ راست
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            knight.moveRight();
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            knight.moveLeft();
        } else {
            knight.stopMoving();
        }

        // kump Z
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            knight.requestJump();
        }
        boolean isJumpKeyDown = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if (wasJumpKeyDown && !isJumpKeyDown) {
            knight.releaseJump();
        }
        wasJumpKeyDown = isJumpKeyDown;

        // dash C
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            knight.dash();
        }

        //attack X
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            knight.attack(enemies,boss,zote);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            knight.startFocus();
        } else {
            if (knight.isFocusing) {
                knight.stopFocus();
            }
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.B)) {
                knight.castVengefulSpirit();
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
            knight.castHowlingWraiths();

        }

        if (knight.isFocusing) {
            return;
        }
    }
}
