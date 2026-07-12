package com.HollowKnight.view;

import com.HollowKnight.data.GameData;
import com.HollowKnight.data.SaveGameManager;
import com.HollowKnight.model.App;
import com.HollowKnight.model.Translator;
import com.HollowKnight.model.UIHelper;
import com.HollowKnight.model.animations.AnimatedImage;
import com.HollowKnight.model.manager.AudioManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class StartGameScreen implements Screen {
    private Stage stage;
    private App app;
    private Texture blackFadeTexture;
    private BitmapFont font;

    public StartGameScreen(App app) {
        this.app = app;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        Image background = new Image(new TextureRegion(new Texture(Gdx.files.internal("ui/MainMenu/background.png"))));
        background.setFillParent(true);
        stage.addActor(background);

        Image fog = new Image(new Texture(Gdx.files.internal("ui/MainMenu/fog.png")));
        fog.setSize(1920 * 2, 1080 * 2); fog.getColor().a = 0.2f;
        fog.setPosition(0, +500);
        fog.addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, -1000, 40f), Actions.moveTo(0, 0))));
        stage.addActor(fog);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Fonts/trajan.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 45; param.color = Color.WHITE;
        param.shadowColor = new Color(0, 0, 0, 0.8f);
        param.shadowOffsetX = 2; param.shadowOffsetY = 2;
        font = generator.generateFont(param);
        generator.dispose();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(60);

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.WHITE);
        Label titleLabel = new Label(Translator.getText("SELECT PROFILE"), titleStyle);
        mainTable.add(titleLabel).padBottom(10).row();

        AnimatedImage animatedHeader = new AnimatedImage(UIHelper.getHeaderAnim(), false);
        mainTable.add(animatedHeader).size(600, 150).padBottom(30).row();

        for (int i = 0; i < 4; i++) {
            mainTable.add(createSaveSlot(i)).padBottom(15).row();
        }

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font; btnStyle.fontColor = Color.WHITE; btnStyle.overFontColor = Color.LIGHT_GRAY;

        Table backBtn = UIHelper.createGlowButton(Translator.getText("BACK"), btnStyle, 460, 70, new Runnable() {
            @Override public void run() { transitionToScreen(new MainMenuScreen(app)); }
        });
        mainTable.add(backBtn).padTop(50).row();

        stage.addActor(mainTable);

        createFadeTexture();
        Image fadeInOverlay = new Image(blackFadeTexture);
        fadeInOverlay.setFillParent(true); fadeInOverlay.getColor().a = 1f;
        fadeInOverlay.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.removeActor()));
        stage.addActor(fadeInOverlay);
    }

    private Table createSaveSlot(final int slotIndex) {
        Table wrapperTable = new Table();

        String slotText = Translator.getText("NEW GAME");
        boolean isNewGame = !SaveGameManager.getInstance().hasSave(slotIndex);

        if (!isNewGame) {
            GameData savedData = SaveGameManager.getInstance().loadGame(slotIndex);
            int minutes = (int) (savedData.gameTimer / 60);
            int seconds = (int) (savedData.gameTimer % 60);
            slotText = String.format("Saved Game - %02d:%02d", minutes, seconds);
        }

        final AnimatedImage leftPointer = new AnimatedImage(UIHelper.getLeftPointerAnim(), true);
        final AnimatedImage rightPointer = new AnimatedImage(UIHelper.getRightPointerAnim(), false);
        leftPointer.setVisible(false); rightPointer.setVisible(false);
        leftPointer.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        rightPointer.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);

        int glowWidth = 1100;
        int glowHeight = 140;
        final Image glowImage = createGlowImage(glowWidth, glowHeight);


        final AnimatedImage flourishAnim = new AnimatedImage(UIHelper.getProfileFleurAnim(), false);
        flourishAnim.setScaling(Scaling.stretch);
        flourishAnim.getColor().a = 0.6f;

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        final Label textLabel = new Label((slotIndex + 1) + ".      " + slotText, labelStyle);
        textLabel.getColor().a = 0.6f;

        Table flourishTable = new Table();
        flourishTable.add(flourishAnim).size(1000, 110).left();

        Table textTable = new Table();
        textTable.add(textLabel).left().padBottom(15).padTop(40).padRight(400);

        Stack contentStack = new Stack();
        contentStack.add(flourishTable);
        contentStack.add(textTable);

        Stack slotStack = new Stack();
        slotStack.add(glowImage);
        slotStack.add(contentStack);

        wrapperTable.add(leftPointer).size(40, 40).padRight(15);
        wrapperTable.add(slotStack).width(glowWidth).height(glowHeight);
        wrapperTable.add(rightPointer).size(40, 40).padLeft(15);

        wrapperTable.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        wrapperTable.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                leftPointer.setVisible(true); rightPointer.setVisible(true);
                leftPointer.resetAnimation(); rightPointer.resetAnimation();
                AudioManager.getInstance().playHoverSound();

                glowImage.clearActions();
                glowImage.addAction(Actions.forever(Actions.sequence(
                    Actions.alpha(0.5f, 0.4f), Actions.alpha(0.1f, 0.4f)
                )));

                textLabel.clearActions();
                textLabel.addAction(Actions.alpha(1f, 0.2f));

                flourishAnim.clearActions();
                flourishAnim.addAction(Actions.alpha(1f, 0.2f));
                flourishAnim.resetAnimation();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                leftPointer.setVisible(false); rightPointer.setVisible(false);

                glowImage.clearActions();
                glowImage.addAction(Actions.alpha(0f, 0.2f));

                textLabel.clearActions();
                textLabel.addAction(Actions.alpha(0.6f, 0.2f));

                flourishAnim.clearActions();
                flourishAnim.addAction(Actions.alpha(0.4f, 0.2f));
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().playClickSound();

                AudioManager.getInstance().pausebackGroundMusic();
                transitionToScreen(new GameScreen(app, slotIndex));
            }
        });

        return wrapperTable;
    }

    private Image createGlowImage(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        float cx = width / 2f; float cy = height / 2f;
        float rx = width / 2f; float ry = height / 2f;
        Color knightGold = new Color(0.9f, 0.85f, 0.6f, 1f);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float dx = (x - cx) / rx; float dy = (y - cy) / ry;
                float distance = dx * dx + dy * dy;
                if (distance <= 1.0f) {
                    float linearAlpha = 1.0f - (float) Math.sqrt(distance);
                    Color pixelColor = new Color(Color.WHITE).lerp(knightGold, (float) Math.sqrt(distance));
                    pixelColor.a = linearAlpha * linearAlpha * 0.5f;
                    pixmap.setColor(pixelColor); pixmap.drawPixel(x, y);
                }
            }
        }
        Texture glowTex = new Texture(pixmap);
        pixmap.dispose();
        Image glowImage = new Image(glowTex);
        glowImage.getColor().a = 0f;
        return glowImage;
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
    @Override public void dispose() {
        if(stage != null) stage.dispose();
        if(blackFadeTexture != null) blackFadeTexture.dispose();
    }
}
