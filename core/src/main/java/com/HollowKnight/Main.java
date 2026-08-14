package com.HollowKnight;

import com.HollowKnight.view.GameScreen;
import com.HollowKnight.model.*;

import com.HollowKnight.view.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class
Main extends Game {
    private App app;
    private SpriteBatch batch;
    private Texture blackTexture;
    private Preferences prefs;

    @Override
    public void create() {
        app = new App(this);
        batch = new SpriteBatch();
        prefs = Gdx.app.getPreferences("HollowKnight_Settings");


        Pixmap blackPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        blackPix.setColor(Color.WHITE);
        blackPix.fill();
        blackTexture = new Texture(blackPix);
        blackPix.dispose();


        Pixmap originalPixmap = new Pixmap(Gdx.files.internal("ui/MainMenu/curser.png"));
        Pixmap cursorPixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        cursorPixmap.drawPixmap(originalPixmap, 0, 0);

        int xHotspot = 0;
        int yHotspot = 0;

        com.badlogic.gdx.graphics.Cursor customCursor = Gdx.graphics.newCursor(cursorPixmap, xHotspot, yHotspot);
        Gdx.graphics.setCursor(customCursor);

        originalPixmap.dispose();
        cursorPixmap.dispose();

        setScreen(new MainMenuScreen(app));
    }

    @Override
    public void render() {
        super.render();

        int brightness = prefs.getInteger("brightness_lvl", 100);
        float alpha = 1.0f - (brightness / 100f);

        if (alpha > 0 && blackTexture != null && batch != null) {
            batch.begin();
            batch.setColor(0, 0, 0, alpha);
            batch.draw(blackTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        if(batch != null) batch.dispose();
        if(blackTexture != null) blackTexture.dispose();
    }
}
