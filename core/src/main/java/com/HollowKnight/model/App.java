package com.HollowKnight.model;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class App {
    private Game game; // نگه‌داشتن رفرنس هسته اصلی بازی
    public App() {
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
        // این خط کلیدی است! این دستور مستقیماً به فریم‌ورک می‌گوید صفحه را عوض کن
        game.setScreen(screen);
    }
}
