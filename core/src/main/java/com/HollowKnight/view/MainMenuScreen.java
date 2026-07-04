package com.HollowKnight.view;

import com.HollowKnight.model.App;
import com.HollowKnight.model.UIHelper;
import com.HollowKnight.model.manager.AudioManager;
import com.HollowKnight.model.Translator;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {
    private Stage stage;
    public App app;
    private Texture blackFadeTexture;

    public MainMenuScreen(App app) {
        this.app = app;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);
        AudioManager.getInstance().playBGM();

        Image background = new Image(new TextureRegion(new Texture(Gdx.files.internal("ui/MainMenu/background.png"))));
        background.setFillParent(true);
        stage.addActor(background);

        Image fog = new Image(new Texture(Gdx.files.internal("ui/MainMenu/fog.png")));
        fog.setSize(1920 * 2, 1080 * 2); fog.getColor().a = 0.1f;
        fog.addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, -1000, 40f), Actions.moveTo(0, 0))));
        stage.addActor(fog);
        Texture tea = new Texture(Gdx.files.internal("ui/MainMenu/tea.png"));
        Image teaImage = new Image(tea);
        teaImage.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        Table bottiomleftcorner = new Table();
        bottiomleftcorner.setFillParent(true);
        bottiomleftcorner.bottom().left().padLeft(250).padBottom(50);
        bottiomleftcorner.add(teaImage).padBottom(100);
        stage.addActor(bottiomleftcorner);

        Texture tcTex = new Texture(Gdx.files.internal("ui/MainMenu/team.png"));
        Image tcLogo = new Image(tcTex);
        tcLogo.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        Table bottomCornerTable = new Table();
        bottomCornerTable.setFillParent(true);
        bottomCornerTable.bottom().right().padRight(100f).padBottom(50f);
        bottomCornerTable.add(tcLogo).width(130).height(130).padBottom(40).padRight(60);
        stage.addActor(bottomCornerTable);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Fonts/trajan.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40; parameter.color = Color.WHITE;
        parameter.shadowColor = new Color(0, 0, 0, 0.5f);
        parameter.shadowOffsetX = 2; parameter.shadowOffsetY = 2;
        BitmapFont customFont = generator.generateFont(parameter);
        generator.dispose();

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = customFont; textButtonStyle.fontColor = Color.WHITE; textButtonStyle.overFontColor = Color.LIGHT_GRAY;

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Image title = new Image(new TextureRegion(new Texture(Gdx.files.internal("ui/MainMenu/vheart_title.png"))));
        mainTable.add(title).center().top().padBottom(10f).padTop(-100f).minHeight(570f).row();

        mainTable.add(UIHelper.createGlowButton(Translator.getText("START GAME"), textButtonStyle, 460, 70, new Runnable() {
            @Override public void run() { transitionToScreen(new StartGameScreen(app)); }
        })).pad(5).row();

        mainTable.add(UIHelper.createGlowButton(Translator.getText("GUIDE"), textButtonStyle, 460, 70, new Runnable() {
            @Override public void run() { transitionToScreen(new GuideScreen(app)); }
        })).pad(5).row();

        mainTable.add(UIHelper.createGlowButton(Translator.getText("ACHIEVEMENTS"), textButtonStyle, 460, 70, new Runnable() {
            @Override public void run() { transitionToScreen(new AchievementsScreen(app)); }
        })).pad(5).row();

        mainTable.add(UIHelper.createGlowButton(Translator.getText("SETTINGS"), textButtonStyle, 460, 70, new Runnable() {
            @Override public void run() { transitionToScreen(new SettingScreen(app)); }
        })).pad(5).row();

        mainTable.add(UIHelper.createGlowButton(Translator.getText("QUIT GAME"), textButtonStyle, 460, 70, new Runnable() {
            @Override public void run() {
                Gdx.input.setInputProcessor(null);
                Image fadeOutOverlay = new Image(blackFadeTexture);
                fadeOutOverlay.setFillParent(true); fadeOutOverlay.getColor().a = 0f;
                fadeOutOverlay.addAction(Actions.sequence(Actions.fadeIn(0.5f), Actions.run(new Runnable() {
                    @Override public void run() { Gdx.app.exit(); }
                })));
                stage.addActor(fadeOutOverlay);
            }
        })).pad(5).row();

        stage.addActor(mainTable);

        createFadeTexture();
        Image fadeInOverlay = new Image(blackFadeTexture);
        fadeInOverlay.setFillParent(true); fadeInOverlay.getColor().a = 1f;
        fadeInOverlay.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.removeActor()));
        stage.addActor(fadeInOverlay);
    }

    private void createFadeTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK); pixmap.fill();
        blackFadeTexture = new Texture(pixmap); pixmap.dispose();
    }

    private void transitionToScreen(final Screen nextScreen) {
        Gdx.input.setInputProcessor(null);
        Image fadeOutOverlay = new Image(blackFadeTexture);
        fadeOutOverlay.setFillParent(true); fadeOutOverlay.getColor().a = 0f;
        fadeOutOverlay.addAction(Actions.sequence(Actions.fadeIn(0.5f), Actions.run(new Runnable() {
            @Override public void run() { ((Game) Gdx.app.getApplicationListener()).setScreen(nextScreen); }
        })));
        stage.addActor(fadeOutOverlay);
    }

    @Override public void render(float delta) { ScreenUtils.clear(0, 0, 0, 1); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}
    @Override public void dispose() { if(stage != null) stage.dispose(); if(blackFadeTexture != null) blackFadeTexture.dispose(); }
}
