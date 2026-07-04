package com.HollowKnight.view;

import com.HollowKnight.model.App;
import com.HollowKnight.model.Translator;
import com.HollowKnight.model.UIHelper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PopupOverlay {
    private Stage stage;
    private App app;
    private GameScreen gameScreen;

    private Image darkBackground;
    private Table mainTable;

    private boolean isVisible = false;
    private BitmapFont customFont;
    private Label dialogueLabel;

    public enum PopupType { PAUSE, DEATH, DIALOGUE, NONE }
    private PopupType currentType = PopupType.NONE;

    public PopupOverlay(GameScreen gameScreen, App app) {
        this.gameScreen = gameScreen;
        this.app = app;
        this.stage = new Stage(new FitViewport(1920, 1080));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Fonts/trajan.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 45;
        parameter.color = Color.WHITE;
        parameter.shadowColor = new Color(0, 0, 0, 0.8f);
        parameter.shadowOffsetX = 2; parameter.shadowOffsetY = 2;
        customFont = generator.generateFont(parameter);
        generator.dispose();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.8f));
        pixmap.fill();
        darkBackground = new Image(new Texture(pixmap));
        pixmap.dispose();
        darkBackground.setFillParent(true);

        mainTable = new Table();
        mainTable.setFillParent(true);

        stage.addActor(darkBackground);
        stage.addActor(mainTable);
    }


    public void showPauseMenu() {
        isVisible = true;
        currentType = PopupType.PAUSE;
        Gdx.input.setInputProcessor(stage);
        mainTable.clear();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = customFont; style.fontColor = Color.WHITE; style.overFontColor = Color.LIGHT_GRAY;

        Label.LabelStyle labelStyle = new Label.LabelStyle(customFont, Color.WHITE);
        mainTable.add(new Label(Translator.getText("PAUSED"), labelStyle)).padBottom(60).row();

        mainTable.add(UIHelper.createGlowButton(Translator.getText("RESUME"), style, 460, 70, () -> hide())).pad(10).row();
        mainTable.add(UIHelper.createGlowButton(Translator.getText("QUIT GAME"), style, 460, 70, () -> {
            gameScreen.dispose();
            ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(app));
        })).pad(10).row();
    }

    //پاپ اپ مردن
    public void showDeathMenu() {
        if (currentType == PopupType.DEATH) return;
        isVisible = true;
        currentType = PopupType.DEATH;
        Gdx.input.setInputProcessor(null);
        mainTable.clear();

        Label.LabelStyle titleStyle = new Label.LabelStyle(customFont, Color.RED);
        mainTable.add(new Label(Translator.getText("YOU DIED"), titleStyle)).padBottom(40).row();

        Label.LabelStyle hintStyle = new Label.LabelStyle(customFont, Color.LIGHT_GRAY);
        Label hintLabel = new Label(Translator.getText("PRESS ANY KEY TO RESPAWN"), hintStyle);
        hintLabel.setFontScale(0.6f);

        mainTable.add(hintLabel).padTop(10);
    }

    // زر زدن زوت
    public void showDialogue(String text) {
        isVisible = true;
        currentType = PopupType.DIALOGUE;
        mainTable.clear();

        Label.LabelStyle labelStyle = new Label.LabelStyle(customFont, Color.WHITE);
        dialogueLabel = new Label(text, labelStyle);
        dialogueLabel.setWrap(true);

        Label hintLabel = new Label(Translator.getText("PRESS_E"), labelStyle);
        hintLabel.setFontScale(0.6f);

        mainTable.add(dialogueLabel).width(1200).center().padBottom(50).row();
        mainTable.add(hintLabel).bottom().right().padTop(50);
    }

    public void updateDialogueText(String text) {
        if (dialogueLabel != null) dialogueLabel.setText(text);
    }

    public void hide() {
        isVisible = false;
        currentType = PopupType.NONE;
        Gdx.input.setInputProcessor(null);
        // کنترلر فعال میشه
    }

    public void render(float delta) {
        if (isVisible) {
            stage.act(delta);
            stage.draw();
        }
    }

    public boolean isVisible() { return isVisible; }
    public PopupType getType() { return currentType; }

    public void dispose() {
        stage.dispose();
        customFont.dispose();
    }
}
