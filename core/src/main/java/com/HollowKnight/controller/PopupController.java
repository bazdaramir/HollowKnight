package com.HollowKnight.controller;

import com.HollowKnight.model.Knight;
import com.HollowKnight.model.enums.PopupType;
import com.HollowKnight.model.mob.Zote;
import com.HollowKnight.view.GameScreen;
import com.HollowKnight.view.PopupOverlay;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PopupController {
    private PopupOverlay popupOverlay;
    private GameScreen gameScreen;

    public PopupController(PopupOverlay popupOverlay, GameScreen gameScreen) {
        this.popupOverlay = popupOverlay;
        this.gameScreen = gameScreen;
    }

    public void handleInput(Knight knight, Zote zote) {

        if (!popupOverlay.isVisible()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                popupOverlay.showPauseMenu();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                popupOverlay.showInventoryMenu();
            }
            return;
        }

        PopupType currentType = popupOverlay.getType();

        switch (currentType) {
            case PAUSE:
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    popupOverlay.hide();
                }
                break;

            case INVENTORY:
                if (Gdx.input.isKeyJustPressed(Input.Keys.I) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    popupOverlay.hide();
                }
                break;
            case ACHIEVEMENT:
                if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
                    popupOverlay.hide();
                }
                break;

            case DEATH:

                break;

            case DIALOGUE:
                if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    if (zote != null) {
                        zote.interact(knight);
                        popupOverlay.updateDialogueText(zote.displayedText);

                        if (!zote.isInteracting) {
                            popupOverlay.hide();
                        }
                    } else {
                        popupOverlay.hide();
                    }
                }
                break;

            default:
                break;
        }
    }
}
