package com.HollowKnight.view;

import com.HollowKnight.model.App;
import com.HollowKnight.model.Translator;
import com.HollowKnight.model.UIHelper;
import com.HollowKnight.model.animations.AnimatedImage;
import com.badlogic.gdx.*;
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

public class GuideScreen implements Screen {
    private Stage stage;
    private App app;
    private Preferences prefs;

    private Label.LabelStyle titleStyle;
    private Label.LabelStyle descStyle;
    private TextButton.TextButtonStyle btnStyle;
    private Texture blackFadeTexture;

    public GuideScreen(App app) {
        this.app = app;
        this.prefs = Gdx.app.getPreferences("HollowKnight_Settings");
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        Image background = new Image(new TextureRegion(new Texture(Gdx.files.internal("ui/MainMenu/background.png"))));
        background.setFillParent(true);
        stage.addActor(background);

        Image fog = new Image(new Texture(Gdx.files.internal("ui/MainMenu/fog.png")));
        fog.setSize(1920 * 2, 1080 * 2);
        fog.getColor().a = 0.2f;
        fog.setPosition(0, +500);
        fog.addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, -1000, 40f), Actions.moveTo(0, 0))));
        stage.addActor(fog);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Fonts/trajan.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = 40;
        titleParam.color = Color.WHITE;
        titleParam.shadowColor = new Color(0, 0, 0, 0.8f);
        titleParam.shadowOffsetX = 2; titleParam.shadowOffsetY = 2;
        BitmapFont titleFont = generator.generateFont(titleParam);

        FreeTypeFontGenerator.FreeTypeFontParameter descParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        descParam.size = 25;
        descParam.color = Color.LIGHT_GRAY;
        descParam.shadowColor = new Color(0, 0, 0, 0.8f);
        descParam.shadowOffsetX = 1; descParam.shadowOffsetY = 1;
        BitmapFont descFont = generator.generateFont(descParam);
        generator.dispose();

        titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        descStyle = new Label.LabelStyle(descFont, Color.LIGHT_GRAY);
        btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = titleFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.LIGHT_GRAY;

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(60);

        Label titleLabel = new Label(Translator.getText("GUIDE"), titleStyle);
        titleLabel.setFontScale(1.4f);
        mainTable.add(titleLabel).colspan(2).padBottom(10).row();

        AnimatedImage animatedHeader = new AnimatedImage(UIHelper.getHeaderAnim(), false);
        mainTable.add(animatedHeader).size(600, 150).padBottom(20).padRight(55).row();


        Table contentTable = new Table();
        contentTable.top();


        contentTable.add(new Label(Translator.getText("CONTROLS"), titleStyle)).padBottom(30).row();
        Table controlsTable = new Table();
        addControlRow(controlsTable, Translator.getText("MOVE LEFT"), prefs.getInteger("key_left", Input.Keys.LEFT));
        addControlRow(controlsTable, Translator.getText("MOVE RIGHT"), prefs.getInteger("key_right", Input.Keys.RIGHT));
        addControlRow(controlsTable, Translator.getText("LOOK UP"), prefs.getInteger("key_up", Input.Keys.UP));
        addControlRow(controlsTable, Translator.getText("LOOK DOWN"), prefs.getInteger("key_down", Input.Keys.DOWN));
        addControlRow(controlsTable, Translator.getText("JUMP"), prefs.getInteger("key_jump", Input.Keys.Z));
        addControlRow(controlsTable, Translator.getText("ATTACK (NAIL)"), prefs.getInteger("key_attack", Input.Keys.X));
        addControlRow(controlsTable, Translator.getText("DASH"), prefs.getInteger("key_dash", Input.Keys.C));
        addControlRow(controlsTable, Translator.getText("FOCUS / SPELL"), prefs.getInteger("key_focus", Input.Keys.A));
        addControlRow(controlsTable, Translator.getText("INTERACT"), prefs.getInteger("key_interact", Input.Keys.E));
        addControlRow(controlsTable, Translator.getText("INVENTORY / MAP"), prefs.getInteger("key_inventory", Input.Keys.I));
        contentTable.add(controlsTable).padBottom(40).row();

        addOrnament(contentTable);


        contentTable.add(new Label(Translator.getText("ABILITIES & SYSTEMS"), titleStyle)).padBottom(30).row();

        addInfoRow(contentTable, Translator.getText("HEALTH (MASKS):"),
            Translator.getText("Your health is represented by Masks. Taking damage shatters a Mask.\nRest on benches or use Focus to recover lost Masks."));

        addInfoRow(contentTable, Translator.getText("SOUL VESSEL:"),
            Translator.getText("Striking enemies with your Nail gathers mystical energy called Soul.\nYou can use Soul to cast powerful Spells or heal yourself."));

        addInfoRow(contentTable, Translator.getText("FOCUS (HEAL):"),
            Translator.getText("Hold the Focus key to channel Soul and heal your shattered Masks.\nMake sure you are in a safe spot, as taking damage interrupts it."));

        addInfoRow(contentTable, Translator.getText("VENGEFUL SPIRIT:"),
            Translator.getText("Tap the Spell key to conjure a spirit that flies forward\nand deals heavy damage to all enemies in its path."));

        addInfoRow(contentTable, Translator.getText("MOTHWING CLOAK:"),
            Translator.getText("Press the Dash key to quickly evade enemy attacks\nor to dash over wide, dangerous gaps."));

        addOrnament(contentTable);


        contentTable.add(new Label(Translator.getText("CHEAT CODES"), titleStyle)).padBottom(30).row();
        Table cheatTable = new Table();

        addInfoRow(cheatTable, Translator.getText("CHEAT: FULL HEAL"),
            Translator.getText("Instantly restores all missing Health Masks without consuming any Soul."));

        addInfoRow(cheatTable, Translator.getText("CHEAT: INFINITE SOUL"),
            Translator.getText("Fills your Soul Vessel to the maximum limit immediately."));

        addInfoRow(cheatTable, Translator.getText("CHEAT: GOD MODE"),
            Translator.getText("Grants complete invulnerability to enemy attacks, spikes, and hazards."));

        addInfoRow(cheatTable, Translator.getText("CHEAT: NOCLIP"),
            Translator.getText("Allows the Knight to fly freely and pass through walls, floors, and solid objects."));

        addInfoRow(cheatTable, Translator.getText("CHEAT: TELEPORT (TP)"),
            Translator.getText("Warps you directly to the Boss Room or the designated spawn point."));

        addInfoRow(cheatTable, Translator.getText("CHEAT: KILL ALL"),
            Translator.getText("Instantly defeats all active enemies currently roaming in the room."));

        contentTable.add(cheatTable).padBottom(60).row();

        addOrnament(contentTable);

        Table backBtn = UIHelper.createGlowButton(Translator.getText("BACK"), btnStyle, 460, 70, new Runnable() {
            @Override
            public void run() {
                transitionToScreen(new MainMenuScreen(app));
            }
        });
        contentTable.add(backBtn).colspan(2).center().padBottom(150).row();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        Texture knobTex = new Texture(Gdx.files.internal("ui/MainMenu/scrollbar_fleur_new.png"));
        scrollStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(knobTex));
        ScrollPane scrollPane = new ScrollPane(contentTable, scrollStyle);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        mainTable.add(scrollPane).width(1400).expandY().fillY().padBottom(50);
        stage.addActor(mainTable);

        createFadeTexture();
        Image fadeInOverlay = new Image(blackFadeTexture);
        fadeInOverlay.setFillParent(true);
        fadeInOverlay.getColor().a = 1f;
        fadeInOverlay.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.removeActor()));
        stage.addActor(fadeInOverlay);
    }

    private void addControlRow(Table table, String action, int keyCode) {
        String keyName = Input.Keys.toString(keyCode).toUpperCase();
        Label actionLabel = new Label(action, titleStyle);
        Label keyLabel = new Label("[ " + keyName + " ]", descStyle);
        table.add(actionLabel).width(450).right().padRight(40).padBottom(15);
        table.add(keyLabel).width(450).left().padLeft(40).padBottom(15).row();
    }

    private void addInfoRow(Table table, String title, String description) {
        Label titleLabel = new Label(title, titleStyle);
        Label descLabel = new Label(description, descStyle);
        descLabel.setAlignment(Align.center);
        table.add(titleLabel).center().padBottom(10).row();
        table.add(descLabel).center().padBottom(40).row();
    }

    private void addOrnament(Table table) {
        Texture tex = new Texture(Gdx.files.internal("ui/MainMenu/divider.png"));
        Image ornament = new Image(tex);
        ornament.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        table.add(ornament).colspan(2).width(1200).height(150).padTop(10).padBottom(30).center().row();
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

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta); stage.draw();
    }

    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override
    public void pause() {} @Override
    public void resume() {} @Override
    public void hide() {}
    @Override
    public void dispose() { if(stage != null) stage.dispose(); if(blackFadeTexture != null) blackFadeTexture.dispose(); }
}
