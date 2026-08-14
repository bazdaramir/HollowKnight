package com.HollowKnight.view;

import com.HollowKnight.data.GameDataManager;
import com.HollowKnight.model.App;
import com.HollowKnight.model.Translator;
import com.HollowKnight.model.UIHelper;
import com.HollowKnight.model.animations.AnimatedImage;
import com.HollowKnight.model.manager.AudioManager;
import com.HollowKnight.model.manager.KeyBindingManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SettingScreen implements Screen {
    private Stage stage;
    private App app;
    private Preferences prefs;


    private final GameScreen returnToGameScreen;

    private Label.LabelStyle labelStyle;
    private TextButton.TextButtonStyle btnStyle;
    private Texture blackFadeTexture;

    private Image themeBackground;
    private Label themeLabel;
    private String themePrefix;
    private int themeIndex;

    public SettingScreen(App app) {
        this(app, null);
    }

    public SettingScreen(App app, GameScreen returnToGameScreen) {
        this.app = app;
        this.returnToGameScreen = returnToGameScreen;
        this.prefs = Gdx.app.getPreferences("HollowKnight_Settings");
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        Image background = new Image(new TextureRegion(UIHelper.getMenuBackground()));
        background.setFillParent(true);
        stage.addActor(background);
        themeBackground = background;

        stage.addActor(UIHelper.createFog());

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Fonts/trajan.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40; parameter.color = Color.WHITE;
        parameter.shadowColor = new Color(0, 0, 0, 0.5f);
        parameter.shadowOffsetX = 2; parameter.shadowOffsetY = 2;
        BitmapFont customFont = generator.generateFont(parameter);
        generator.dispose();

        labelStyle = new Label.LabelStyle(customFont, Color.WHITE);
        btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = customFont; btnStyle.fontColor = Color.WHITE; btnStyle.overFontColor = Color.LIGHT_GRAY;

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(60);

        Label titleLabel = new Label(Translator.getText("SETTINGS"), labelStyle);
        titleLabel.setFontScale(1.4f);
        mainTable.add(titleLabel).colspan(2).padBottom(10).row();

        AnimatedImage animatedHeader = new AnimatedImage(UIHelper.getHeaderAnim(), false);
        mainTable.add(animatedHeader).size(600, 150).padBottom(15).padRight(40f).row();

        Table contentTable = new Table();
        contentTable.top();

        Table volumeAdjuster = createAdjuster(Translator.getText("VOLUME"), "master_volume", 100);
        TextButton musicToggle = createToggle(Translator.getText("MUSIC"), "music_on", true);
        contentTable.add(musicToggle).left().padRight(120).padBottom(30);
        contentTable.add(volumeAdjuster).right().padBottom(30).row();

        Table brightnessAdjuster = createAdjuster(Translator.getText("BRIGHTNESS"), "brightness_lvl", 100);
        TextButton sfxToggle = createToggle(Translator.getText("SFX"), "sfx_on", true);
        contentTable.add(sfxToggle).left().padRight(120).padBottom(40);
        contentTable.add(brightnessAdjuster).right().padBottom(40).row();

        Table resetBtn = UIHelper.createGlowButton(Translator.getText("RESET"), btnStyle, 460, 70, new Runnable() {
            @Override public void run() {
                prefs.putInteger("master_volume", 100);
                prefs.putInteger("brightness_lvl", 100);
                prefs.putBoolean("music_on", true);
                prefs.putBoolean("sfx_on", true);
                prefs.flush();
                AudioManager.getInstance().setMasterVolume(1.0f);
                transitionToScreen(new SettingScreen(app, returnToGameScreen));
            }
        });
        contentTable.add(resetBtn).colspan(2).padBottom(40).row();

        addOrnament(contentTable);

        Table keysTable = new Table();
        keysTable.add(createKeybindBtn("LEFT", KeyBindingManager.LEFT)).padRight(120).padBottom(30);
        keysTable.add(createKeybindBtn("RIGHT", KeyBindingManager.RIGHT)).padBottom(30).row();
        keysTable.add(createKeybindBtn("UP", KeyBindingManager.UP)).padRight(120).padBottom(30);
        keysTable.add(createKeybindBtn("DOWN", KeyBindingManager.DOWN)).padBottom(30).row();
        keysTable.add(createKeybindBtn("JUMP", KeyBindingManager.JUMP)).padRight(120).padBottom(30);
        keysTable.add(createKeybindBtn("ATTACK", KeyBindingManager.ATTACK)).padBottom(30).row();
        keysTable.add(createKeybindBtn("DASH", KeyBindingManager.DASH)).padRight(120).padBottom(30);
        keysTable.add(createKeybindBtn("FOCUS", KeyBindingManager.FOCUS)).padBottom(30).row();
        keysTable.add(createKeybindBtn("SPELL 1", KeyBindingManager.FIREBALL)).padRight(120).padBottom(30);
        keysTable.add(createKeybindBtn("SPELL 2", KeyBindingManager.SCREAM)).padBottom(30).row();
        keysTable.add(createKeybindBtn("INTERACT", KeyBindingManager.INTERACT)).padRight(120).padBottom(30);
        keysTable.add(createKeybindBtn("INVENTORY", KeyBindingManager.INVENTORY)).padBottom(30).row();
        keysTable.add(createKeybindBtn("PAUSE", KeyBindingManager.PAUSE)).padRight(120).padBottom(30).row();
        contentTable.add(keysTable).colspan(2).padBottom(30).row();

        Table resetKeysBtn = UIHelper.createGlowButton(Translator.getText("RESET CONTROLS"), btnStyle, 460, 70, new Runnable() {
            @Override public void run() {
                KeyBindingManager.getInstance().resetToDefaults();
                transitionToScreen(new SettingScreen(app, returnToGameScreen));
            }
        });
        contentTable.add(resetKeysBtn).colspan(2).padBottom(40).row();

        addOrnament(contentTable);

        Table langBtn = createLangToggle(Translator.getText("LANGUAGE"), "language_id");
        contentTable.add(langBtn).colspan(2).padBottom(50).row();

        Table themeSelector = createThemeSelector(Translator.getText("THEME"));
        contentTable.add(themeSelector).colspan(2).padBottom(50).row();

        addOrnament(contentTable);

        Table backBtn = UIHelper.createGlowButton(Translator.getText("BACK"), btnStyle, 460, 70, new Runnable() {
            @Override
            public void run() {
                if (returnToGameScreen != null) {

                    transitionToScreen(returnToGameScreen);
                } else {
                    transitionToScreen(new MainMenuScreen(app));
                }
            }
        });
        contentTable.add(backBtn).colspan(2).padBottom(150);

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        Texture knobTex = new Texture(Gdx.files.internal("ui/MainMenu/scrollbar_fleur_new.png"));
        scrollStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(knobTex));

        ScrollPane scrollPane = new ScrollPane(contentTable, scrollStyle);
        scrollPane.setFadeScrollBars(false); scrollPane.setScrollingDisabled(true, false);

        mainTable.add(scrollPane).width(1400).expandY().fillY().padBottom(50);
        stage.addActor(mainTable);

        createFadeTexture();
        Image fadeInOverlay = new Image(blackFadeTexture);
        fadeInOverlay.setFillParent(true); fadeInOverlay.getColor().a = 1f;
        fadeInOverlay.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.removeActor()));
        stage.addActor(fadeInOverlay);

        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.LEFT) { changeTheme(-1); return true; }
                if (keycode == Input.Keys.RIGHT) { changeTheme(1); return true; }
                return false;
            }
        });
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

    private void addOrnament(Table table) {
        Texture tex = new Texture(Gdx.files.internal("ui/MainMenu/divider.png"));
        Image ornament = new Image(tex);
        ornament.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        table.add(ornament).colspan(2).width(600).height(400).center().row();
    }

    private Table createLangToggle(final String prefix, final String prefKey) {
        Table wrapperTable = new Table();
        int currentLangIdx = prefs.getInteger(prefKey, 0);
        final TextButton toggleBtn = new TextButton(prefix + ": " + Translator.LANGUAGES[currentLangIdx], btnStyle);

        final AnimatedImage leftPointer = new AnimatedImage(UIHelper.getLeftPointerAnim(), true);
        final AnimatedImage rightPointer = new AnimatedImage(UIHelper.getRightPointerAnim(), false);

        leftPointer.setVisible(false); rightPointer.setVisible(false);
        leftPointer.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        rightPointer.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);

        wrapperTable.add(leftPointer).size(40, 40).padRight(15);
        wrapperTable.add(toggleBtn);
        wrapperTable.add(rightPointer).size(40, 40).padLeft(15);


        toggleBtn.addListener(new ClickListener() {
            @Override public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                leftPointer.setVisible(true); rightPointer.setVisible(true);
                leftPointer.resetAnimation(); rightPointer.resetAnimation();
                AudioManager.getInstance().playHoverSound();
            }
            @Override public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                leftPointer.setVisible(false); rightPointer.setVisible(false);
            }
            @Override public void clicked(InputEvent event, float x, float y) {
                int nextIdx = (prefs.getInteger(prefKey, 0) + 1) % Translator.LANGUAGES.length;
                prefs.putInteger(prefKey, nextIdx); prefs.flush();
                AudioManager.getInstance().playClickSound();
                transitionToScreen(new SettingScreen(app, returnToGameScreen));

            }
        });
        return wrapperTable;
    }

    private TextButton createToggle(final String prefix, final String prefKey, boolean defaultValue) {
        final boolean[] isOn = {prefs.getBoolean(prefKey, defaultValue)};
        String statusTxt = isOn[0] ? Translator.getText("ON") : Translator.getText("OFF");
        final TextButton toggleBtn = new TextButton(prefix + ": " + statusTxt, btnStyle);
        toggleBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                isOn[0] = !isOn[0];
                String newStatus = isOn[0] ? Translator.getText("ON") : Translator.getText("OFF");
                toggleBtn.setText(prefix + ": " + newStatus);
                prefs.putBoolean(prefKey, isOn[0]); prefs.flush();
                if (prefKey.equals("music_on")) AudioManager.getInstance().toggleMusic(isOn[0]);
                AudioManager.getInstance().playClickSound();
            }
        });
        return toggleBtn;
    }

    private Table createAdjuster(final String prefix, final String prefKey, int defaultValue) {
        Table table = new Table();
        final int[] currentValue = {prefs.getInteger(prefKey, defaultValue)};
        final Label valueLabel = new Label(prefix + ": " + currentValue[0] + "%", labelStyle);

        Texture arrowTexture = new Texture(Gdx.files.internal("ui/MainMenu/slider_arrow.png"));
        TextureRegion rightRegion = new TextureRegion(arrowTexture);
        TextureRegionDrawable rightDrawable = new TextureRegionDrawable(rightRegion);
        TextureRegion leftRegion = new TextureRegion(arrowTexture);
        leftRegion.flip(true, false);
        TextureRegionDrawable leftDrawable = new TextureRegionDrawable(leftRegion);

        ImageButton leftArrow = new ImageButton(leftDrawable);
        ImageButton rightArrow = new ImageButton(rightDrawable);

        leftArrow.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentValue[0] > 0) {
                    currentValue[0] -= 10;
                    valueLabel.setText(prefix + ": " + currentValue[0] + "%");
                    prefs.putInteger(prefKey, currentValue[0]); prefs.flush();
                    AudioManager.getInstance().playClickSound();
                    if(prefKey.equals("master_volume")) AudioManager.getInstance().setMasterVolume(currentValue[0] / 100f);
                }
            }
        });
        rightArrow.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentValue[0] < 100) {
                    currentValue[0] += 10;
                    valueLabel.setText(prefix + ": " + currentValue[0] + "%");
                    prefs.putInteger(prefKey, currentValue[0]); prefs.flush();
                    AudioManager.getInstance().playClickSound();
                    if(prefKey.equals("master_volume")) AudioManager.getInstance().setMasterVolume(currentValue[0] / 100f);
                }
            }
        });

        table.add(leftArrow).size(30, 40).padRight(20);
        table.add(valueLabel).width(350).center();
        table.add(rightArrow).size(30, 40).padLeft(20);
        return table;
    }

    // ردیف انتخاب تم بک گراند منو، دقیقا مثل بقیه ردیف های تنظیمات با فلش چپ و راست
    private Table createThemeSelector(final String prefix) {
        Table table = new Table();
        themePrefix = prefix;
        themeIndex = GameDataManager.getInstance().getSelectedTheme();
        themeLabel = new Label(themeText(), labelStyle);

        Texture arrowTexture = new Texture(Gdx.files.internal("ui/MainMenu/slider_arrow.png"));
        TextureRegion rightRegion = new TextureRegion(arrowTexture);
        TextureRegionDrawable rightDrawable = new TextureRegionDrawable(rightRegion);
        TextureRegion leftRegion = new TextureRegion(arrowTexture);
        leftRegion.flip(true, false);
        TextureRegionDrawable leftDrawable = new TextureRegionDrawable(leftRegion);

        ImageButton leftArrow = new ImageButton(leftDrawable);
        ImageButton rightArrow = new ImageButton(rightDrawable);

        leftArrow.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { changeTheme(-1); }
        });
        rightArrow.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { changeTheme(1); }
        });

        table.add(leftArrow).size(30, 40).padRight(20);
        table.add(themeLabel).width(350).center();
        table.add(rightArrow).size(30, 40).padLeft(20);
        return table;
    }

    private String themeText() {
        return themePrefix + ": Theme " + (themeIndex + 1);
    }

    private void changeTheme(int step) {
        int nextIndex = themeIndex + step;
        if (nextIndex < 0 || nextIndex >= UIHelper.THEME_COUNT) return;

        themeIndex = nextIndex;
        GameDataManager.getInstance().setSelectedTheme(themeIndex);
        themeLabel.setText(themeText());
        themeBackground.setDrawable(new TextureRegionDrawable(new TextureRegion(UIHelper.getMenuBackground())));
        AudioManager.getInstance().playClickSound();
    }

    private Table createKeybindBtn(final String actionName, final String action) {
        Table wrapperTable = new Table();
        final KeyBindingManager keys = KeyBindingManager.getInstance();
        final TextButton keyBtn = new TextButton(actionName + " [ " + keys.getKeyName(action) + " ]", btnStyle);

        final AnimatedImage leftPointer = new AnimatedImage(UIHelper.getLeftPointerAnim(), true);
        final AnimatedImage rightPointer = new AnimatedImage(UIHelper.getRightPointerAnim(), false);

        leftPointer.setVisible(false); rightPointer.setVisible(false);
        leftPointer.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        rightPointer.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);

        wrapperTable.add(leftPointer).size(40, 40).padRight(15);
        wrapperTable.add(keyBtn);
        wrapperTable.add(rightPointer).size(40, 40).padLeft(15);

        keyBtn.addListener(new ClickListener() {
            @Override public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                leftPointer.setVisible(true); rightPointer.setVisible(true);
                leftPointer.resetAnimation(); rightPointer.resetAnimation();
                AudioManager.getInstance().playHoverSound();
            }
            @Override public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                leftPointer.setVisible(false); rightPointer.setVisible(false);
            }
            @Override public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().playClickSound();
                keyBtn.setText("Dadash yechi bezan");
                Gdx.input.setInputProcessor(new InputAdapter() {
                    @Override public boolean keyDown(int keycode) {
                        // اسکیپ یعنی بیخیال، کلید نامعتبر یا تکراری هم قبول نمیشه و کلید قبلی سرجاش میمونه
                        if (keycode != Input.Keys.ESCAPE && !keys.rebind(action, keycode)) {
                            AudioManager.getInstance().playHoverSound();
                        }
                        keyBtn.setText(actionName + " [ " + keys.getKeyName(action) + " ]");
                        Gdx.input.setInputProcessor(stage);
                        return true;
                    }
                });
            }
        });
        return wrapperTable;
    }

    @Override public void render(float delta) { ScreenUtils.clear(0, 0, 0, 1); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}
    @Override public void dispose() { if(stage != null) stage.dispose(); if(blackFadeTexture != null) blackFadeTexture.dispose(); }
}
