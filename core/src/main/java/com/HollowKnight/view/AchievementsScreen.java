package com.HollowKnight.view;

import com.HollowKnight.data.GameDataManager;
import com.HollowKnight.model.App;
import com.HollowKnight.model.Translator;
import com.HollowKnight.model.UIHelper;
import com.HollowKnight.model.animations.AnimatedImage;
import com.HollowKnight.model.manager.AchievementManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class AchievementsScreen implements Screen {
    private Stage stage;
    private App app;

    private Label.LabelStyle unlockedTitleStyle;
    private Label.LabelStyle lockedTitleStyle;
    private Label.LabelStyle unlockedDescStyle;
    private Label.LabelStyle lockedDescStyle;
    private TextButton.TextButtonStyle btnStyle;
    private Texture blackFadeTexture;

    public AchievementsScreen(App app) {
        this.app = app;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        Image background = new Image(new TextureRegion(UIHelper.getMenuBackground()));
        background.setFillParent(true);
        stage.addActor(background);

        stage.addActor(UIHelper.createFog());


        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Fonts/trajan.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 35;
        param.color = new Color(0.9f, 0.85f, 0.6f, 1f);
        param.shadowColor = new Color(0, 0, 0, 0.8f);
        param.shadowOffsetX = 2; param.shadowOffsetY = 2;
        BitmapFont unlockedTitleFont = generator.generateFont(param);

        param.color = Color.LIGHT_GRAY;
        param.shadowOffsetX = 0; param.shadowOffsetY = 0;
        BitmapFont lockedTitleFont = generator.generateFont(param);

        param.size = 20;
        param.color = Color.LIGHT_GRAY;
        param.shadowOffsetX = 1; param.shadowOffsetY = 1;
        BitmapFont unlockedDescFont = generator.generateFont(param);

        param.shadowOffsetX = 0; param.shadowOffsetY = 0;
        BitmapFont lockedDescFont = generator.generateFont(param);

        param.size = 45;
        param.color = Color.WHITE;
        param.shadowOffsetX = 2; param.shadowOffsetY = 2;
        BitmapFont mainTitleFont = generator.generateFont(param);

        generator.dispose();

        unlockedTitleStyle = new Label.LabelStyle(unlockedTitleFont, new Color(0.9f, 0.85f, 0.6f, 1f));
        lockedTitleStyle = new Label.LabelStyle(lockedTitleFont, Color.LIGHT_GRAY);
        unlockedDescStyle = new Label.LabelStyle(unlockedDescFont, Color.LIGHT_GRAY);
        lockedDescStyle = new Label.LabelStyle(lockedDescFont, Color.GRAY);

        btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = mainTitleFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.LIGHT_GRAY;


        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(60);

        Label titleLabel = new Label(Translator.getText("ACHIEVEMENTS"), new Label.LabelStyle(mainTitleFont, Color.WHITE));
        mainTable.add(titleLabel).padBottom(10).row();

        AnimatedImage animatedHeader = new AnimatedImage(UIHelper.getHeaderAnim(), false);
        mainTable.add(animatedHeader).size(600, 150).padBottom(20).padRight(40).row();


        Table contentTable = new Table();
        contentTable.top();

        addAchievementRow(contentTable, "ui/Achievements/True Hunter.png", "TRUE HUNTER", Translator.getText("True Hunter"), AchievementManager.TRUE_HUNTER);
        addAchievementRow(contentTable, "ui/Achievements/achievement__0020_charms_half.png", "CHARMED", Translator.getText("Charmed"), AchievementManager.CHARMED);
        addAchievementRow(contentTable, "ui/Achievements/achievement__0018_vessel_01.png", "SOUL MASTER", Translator.getText("Soul Master"), AchievementManager.SOUL_MASTER);
        addAchievementRow(contentTable, "ui/Achievements/Defeat Boss.png", "DEFEAT BOSS", Translator.getText("Defeat Boss"), AchievementManager.DEFEAT_BOSS);
        addAchievementRow(contentTable, "ui/Achievements/achievement__0001_all_maps.png", "ZOTE", Translator.getText("Zote"), AchievementManager.ZOTE);
        addAchievementRow(contentTable, "ui/Achievements/Completion.png", "COMPLETION", Translator.getText("Completion"), AchievementManager.COMPLETION);
        addAchievementRow(contentTable, "ui/Achievements/Speedrun.png", "SPEEDRUN", Translator.getText("Speedrun"), AchievementManager.SPEEDRUN);

        Table backBtn = UIHelper.createGlowButton(Translator.getText("BACK"), btnStyle, 460, 70, new Runnable() {
            @Override
            public void run() {
                transitionToScreen(new MainMenuScreen(app));
            }
        });
        contentTable.add(backBtn).center().padTop(60).padBottom(150).row();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        Texture knobTex = new Texture(Gdx.files.internal("ui/MainMenu/scrollbar_fleur_new.png"));
        scrollStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(knobTex));

        ScrollPane scrollPane = new ScrollPane(contentTable, scrollStyle);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        mainTable.add(scrollPane).width(1100).expandY().fillY().padBottom(50);
        stage.addActor(mainTable);

        createFadeTexture();
        Image fadeInOverlay = new Image(blackFadeTexture);
        fadeInOverlay.setFillParent(true);
        fadeInOverlay.getColor().a = 1f;
        fadeInOverlay.addAction(Actions.sequence(Actions.fadeOut(0.6f), Actions.removeActor()));
        stage.addActor(fadeInOverlay);
    }

    private void addAchievementRow(Table table, String iconPath, String defaultTitle, String defaultDesc, String achievementId) {
        Table rowTable = new Table();

        boolean isUnlocked = GameDataManager.getInstance().isAchievementUnlocked(achievementId);

        Texture iconTex;
        try {
            iconTex = new Texture(Gdx.files.internal(iconPath));
        } catch (Exception e) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE); pm.fill();
            iconTex = new Texture(pm);
            pm.dispose();
        }
        Image icon = new Image(iconTex);
        icon.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        Label titleLabel = new Label(defaultTitle, isUnlocked ? unlockedTitleStyle : lockedTitleStyle);
        Label descLabel = new Label(defaultDesc, isUnlocked ? unlockedDescStyle : lockedDescStyle);

        titleLabel.setAlignment(Align.left);
        descLabel.setAlignment(Align.left);

        if (!isUnlocked) {
            rowTable.setColor(0.6f, 0.6f, 0.6f, 0.4f);
        }

        Table textTable = new Table();
        textTable.add(titleLabel).expandX().left().padBottom(5).row();
        textTable.add(descLabel).expandX().left().row();

        rowTable.add(icon).size(90, 90).left().padRight(25);
        rowTable.add(textTable).width(750).expandX().padLeft(40).left();

        table.add(rowTable).expandX().left().padLeft(100).padBottom(35).row();
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
        fadeOutOverlay.addAction(Actions.sequence(Actions.fadeIn(0.7f), Actions.run(new Runnable() {
            @Override
            public void run() { ((Game) Gdx.app.getApplicationListener()).setScreen(nextScreen); }
        })));
        stage.addActor(fadeOutOverlay);
    }
    private Animation<TextureRegion> createAnimation(String filePrefix, int startFrame, int endFrame, float frameDuration, boolean flipX) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = startFrame; i <= endFrame; i++) {
            String fileName = String.format("%s%04d.png", filePrefix, i);
            Texture tex = new Texture(Gdx.files.internal(fileName));
            TextureRegion region = new TextureRegion(tex);
            if (flipX) region.flip(true, false);
            frames.add(region);
        }
        return new Animation<>(frameDuration, frames);
    }

    @Override public void render(float delta) { ScreenUtils.clear(0, 0, 0, 1); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}
    @Override public void dispose() { if(stage != null) stage.dispose(); if(blackFadeTexture != null) blackFadeTexture.dispose(); }
}
