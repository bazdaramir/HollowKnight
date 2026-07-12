package com.HollowKnight.model;

import com.HollowKnight.Main;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import java.util.LinkedHashMap;

public class App {
    private Game game;
    private LinkedHashMap<String, Achievement> achievements;
    public App(Main main) {
        achievements = new LinkedHashMap<>();
        achievements.put("True Hunter", new Achievement("True Hunter", Translator.getText("True Hunter"), false));
        achievements.put("Defeat Boss", new Achievement("Defeat Boss", Translator.getText("Defeat Boss"), false));
        achievements.put("Zote", new Achievement("Zote", Translator.getText("Zote"), false));

        this.game = main;
    }

    public LinkedHashMap<String, Achievement> getAchievements() {
        return achievements;
    }

    public Screen getScreen() {
        return game.getScreen();
    }

    public void setScreen(Screen screen) {
        game.setScreen(screen);
    }
}
