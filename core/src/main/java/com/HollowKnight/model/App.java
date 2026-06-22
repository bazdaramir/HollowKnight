package com.HollowKnight.model;

import com.HollowKnight.Main;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class App {
    private Game game;
    public App(Main main) {
        this.game =  new Game() {
            @Override
            public void create() {

            }
        };
    }

    public Screen getScreen() {
        return game.getScreen();
    }

    public void setScreen(Screen screen) {
        game.setScreen(screen);
    }
}
