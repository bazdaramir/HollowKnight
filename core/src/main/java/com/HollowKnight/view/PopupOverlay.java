package com.HollowKnight.view;

import com.HollowKnight.data.GameData;
import com.HollowKnight.data.GameDataManager;
import com.HollowKnight.data.SaveGameManager;
import com.HollowKnight.model.App;
import com.HollowKnight.model.Charm;
import com.HollowKnight.model.Translator;
import com.HollowKnight.model.UIHelper;
import com.HollowKnight.model.enums.PopupType;
import com.HollowKnight.model.manager.AchievementManager;
import com.HollowKnight.model.manager.AudioManager;
import com.HollowKnight.model.manager.CharmManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PopupOverlay {
    private Stage stage;
    private App app;
    private GameScreen gameScreen;
    private CharmManager charmManager;

    private Image darkBackground;
    private Table mainTable;

    private boolean screenTransitionTriggered = false;

    private Label.LabelStyle unlockedTitleStyle;
    private Label.LabelStyle lockedTitleStyle;
    private Label.LabelStyle unlockedDescStyle;
    private Label.LabelStyle lockedDescStyle;

    private boolean isVisible = false;
    private BitmapFont customFont;
    private Label dialogueLabel;

    private PopupType currentType = PopupType.NONE;

    public PopupOverlay(GameScreen gameScreen, App app) {
        this.gameScreen = gameScreen;
        this.app = app;
        this.stage = new Stage(new FitViewport(1920, 1080));
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Fonts/trajan.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 35;
        param.color = new Color(0.9f, 0.85f, 0.6f, 1f);
        param.shadowColor = new Color(0, 0, 0, 0.8f);
        param.shadowOffsetX = 2;
        param.shadowOffsetY = 2;
        BitmapFont unlockedTitleFont = generator.generateFont(param);

        param.color = Color.LIGHT_GRAY;
        param.shadowOffsetX = 0;
        param.shadowOffsetY = 0;
        BitmapFont lockedTitleFont = generator.generateFont(param);

        param.size = 20;
        param.color = Color.LIGHT_GRAY;
        param.shadowOffsetX = 1;
        param.shadowOffsetY = 1;
        BitmapFont unlockedDescFont = generator.generateFont(param);

        param.shadowOffsetX = 0;
        param.shadowOffsetY = 0;
        BitmapFont lockedDescFont = generator.generateFont(param);

        param.size = 45;
        param.color = Color.WHITE;
        param.shadowOffsetX = 2;
        param.shadowOffsetY = 2;
        BitmapFont mainTitleFont = generator.generateFont(param);

        unlockedTitleStyle = new Label.LabelStyle(unlockedTitleFont, new Color(0.9f, 0.85f, 0.6f, 1f));
        lockedTitleStyle = new Label.LabelStyle(lockedTitleFont, Color.LIGHT_GRAY);
        unlockedDescStyle = new Label.LabelStyle(unlockedDescFont, Color.LIGHT_GRAY);
        lockedDescStyle = new Label.LabelStyle(lockedDescFont, Color.GRAY);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 45;
        parameter.color = Color.WHITE;
        parameter.shadowColor = new Color(0, 0, 0, 0.8f);
        parameter.shadowOffsetX = 2; parameter.shadowOffsetY = 2;
        customFont = generator.generateFont(parameter);
        generator.dispose();
        charmManager = new CharmManager();

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

    private void activateStage() {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        Gdx.input.setInputProcessor(stage);
    }

    public void showAchiementUnlocked(String achievement) {
        isVisible = true;
        currentType = PopupType.ACHIEVEMENT;
        activateStage();
        mainTable.clear();
        Label.LabelStyle matn = new Label.LabelStyle(customFont, Color.GOLD);
        Label matnlable = new Label(Translator.getText("ACHIEVEMENT UNLOCKED"), matn);
        matnlable.setFontScale(0.6f);
        mainTable.add(matnlable).padTop(10).padBottom(60).row();

        switch (achievement) {
            case "True Hunter":
                addAchievementRow(mainTable, "ui/Achievements/True Hunter.png", "TRUE HUNTER", Translator.getText("True Hunter"), AchievementManager.TRUE_HUNTER);
                break;
            case "Soul Master":
                addAchievementRow(mainTable, "ui/Achievements/achievement__0018_vessel_01.png", "SOUL MASTER", Translator.getText("Soul Master"), AchievementManager.SOUL_MASTER);
                break;
            case "Charmed":
                addAchievementRow(mainTable, "ui/Achievements/achievement__0020_charms_half.png", "CHARMED", Translator.getText("Charmed"), AchievementManager.CHARMED);
                break;
            case "Defeat Boss":
                addAchievementRow(mainTable, "ui/Achievements/Defeat Boss.png", "DEFEAT BOSS", Translator.getText("Defeat Boss"), AchievementManager.DEFEAT_BOSS);
                break;
            case "Zote":
                addAchievementRow(mainTable, "ui/Achievements/achievement__0001_all_maps.png", "ZOTE", Translator.getText("Zote"), AchievementManager.ZOTE);
                break;
            default:
                break;
        }
        Label.LabelStyle hintStyle = new Label.LabelStyle(customFont, Color.LIGHT_GRAY);
        Label hintLabel = new Label(Translator.getText("PRESS ANY KEY TO CONTINUE PLAYING"), hintStyle);
        hintLabel.setFontScale(0.6f);

        mainTable.add(hintLabel).padTop(10).row();
    }

    private void addAchievementRow(Table table, String iconPath, String defaultTitle, String defaultDesc, String achievementId) {
        Table rowTable = new Table();

        boolean isUnlocked = GameDataManager.getInstance().isAchievementUnlocked(achievementId);

        Texture iconTex;
        try {
            iconTex = new Texture(Gdx.files.internal(iconPath));
        } catch (Exception e) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE);
            pm.fill();
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

        table.add(rowTable).expandX().padLeft(140).padBottom(35).row();
    }

    public void showPauseMenu() {
        isVisible = true;
        currentType = PopupType.PAUSE;
        activateStage();
        mainTable.clear();
        screenTransitionTriggered = false;

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = customFont; style.fontColor = Color.WHITE; style.overFontColor = Color.LIGHT_GRAY;

        Label.LabelStyle labelStyle = new Label.LabelStyle(customFont, Color.WHITE);
        mainTable.add(new Label(Translator.getText("PAUSED"), labelStyle)).padBottom(60).row();

        Label.LabelStyle cheatStyle = new Label.LabelStyle(customFont, Color.CYAN);
        Label cheatLabel = new Label(Translator.getText("ALL_CHEAT"), cheatStyle);
        cheatLabel.setFontScale(0.45f);
        cheatLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        mainTable.add(cheatLabel).padBottom(40).row();

        mainTable.add(UIHelper.createGlowButton(Translator.getText("RESUME"), style, 460, 70, () -> hide())).pad(10).row();

        mainTable.add(UIHelper.createGlowButton(Translator.getText("SETTINGS"), style, 460, 70, () -> {
            ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new SettingScreen(app, gameScreen));
        })).pad(10).row();

        mainTable.add(UIHelper.createGlowButton(Translator.getText("SAVE & QUIT"), style, 460, 70, () -> {
            if (screenTransitionTriggered) return;
            screenTransitionTriggered = true;

            if (gameScreen.getSaveSlot() >= 0) {
                GameData data = gameScreen.captureGameData();
                SaveGameManager.getInstance().saveGame(data, gameScreen.getSaveSlot());
            }
            gameScreen.dispose();
            ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(app));
        })).pad(10).row();
    }

    public void showDeathMenu(float finalTime, int enemiesKilled) {
        isVisible = true;
        currentType = PopupType.DEATH;
        activateStage();
        mainTable.clear();
        screenTransitionTriggered = false;
        int minutes = (int) (finalTime / 60);
        int seconds = (int) (finalTime % 60);
        String timeString = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds);

        Label.LabelStyle titleStyle = new Label.LabelStyle(customFont, Color.RED);
        Label.LabelStyle statsStyle = new Label.LabelStyle(customFont, Color.WHITE);

        Label gameOverTitle = new Label("GAME OVER", titleStyle);

        String statsText = "Enemies Defeated: " + enemiesKilled + "\nTime Survived: " + timeString;
        Label statsLabel = new Label(statsText, statsStyle);
        statsLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = customFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.LIGHT_GRAY;

        mainTable.add(gameOverTitle).padBottom(30).row();
        mainTable.add(statsLabel).padBottom(50).row();

        mainTable.add(UIHelper.createGlowButton("RESTART GAME", btnStyle, 460, 70, () -> {
            if (screenTransitionTriggered) return;
            screenTransitionTriggered = true;

            AudioManager.getInstance().playClickSound();
            gameScreen.dispose();
            AudioManager.getInstance().puaseDeathPopUp();

            ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(app));
        })).padBottom(20).row();

        mainTable.add(UIHelper.createGlowButton("MAIN MENU", btnStyle, 460, 70, () -> {
            if (screenTransitionTriggered) return;
            screenTransitionTriggered = true;

            AudioManager.getInstance().playClickSound();
            gameScreen.dispose();
            AudioManager.getInstance().puaseDeathPopUp();
            ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(app));
        }));

        AudioManager.getInstance().PlauDeathPopUp();
    }

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
    }

    public void render(float delta) {
        if (isVisible) {
            stage.act(delta);
            stage.draw();
        }
    }

    public void resizeViewport(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public boolean isVisible() { return isVisible; }
    public PopupType getType() { return currentType; }

    public void dispose() {
        stage.dispose();
        customFont.dispose();
    }

    public void showInventoryMenu() {
        if (currentType == PopupType.DIALOGUE) return;

        isVisible = true;
        currentType = PopupType.INVENTORY;
        activateStage();
        mainTable.clear();

        Label.LabelStyle titleStyle = new Label.LabelStyle(customFont, Color.GOLD);
        Label.LabelStyle descStyle = new Label.LabelStyle(customFont, Color.LIGHT_GRAY);

        mainTable.add(new Label("INVENTORY & CHARMS", titleStyle)).colspan(4).padBottom(20).row();

        final Label notchLabel = new Label("Notches: " + charmManager.getUsedNotches() + " / " + CharmManager.MAX_NOTCHES, descStyle);
        mainTable.add(notchLabel).colspan(4).padBottom(40).row();

        final Label descriptionLabel = new Label("Select a charm to equip/unequip.", descStyle);
        descriptionLabel.setFontScale(0.6f);
        descriptionLabel.setWrap(true);

        int colCount = 0;
        for (Charm charm : charmManager.getCharms()) {
            Image charmImage;

            try {
                charmImage = new Image(new Texture(Gdx.files.internal(charm.getImagePath())));
            } catch (Exception e) {
                Pixmap pm = new Pixmap(80, 80, Pixmap.Format.RGBA8888);
                pm.setColor(Color.DARK_GRAY);
                pm.fill();
                charmImage = new Image(new Texture(pm));
            }

            if (charm.isEquipped()) {
                charmImage.setColor(Color.WHITE);
            } else {
                charmImage.setColor(0.3f, 0.3f, 0.3f, 1f);
            }

            final Image finalCharmImage = charmImage;
            charmImage.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    descriptionLabel.setText(charm.getName() + ": " + charm.getDescription());
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioManager.getInstance().playClickSound();

                    boolean success = charmManager.toggleCharm(charm);
                    if (success) {
                        if (charm.isEquipped()) finalCharmImage.setColor(Color.WHITE);
                        else finalCharmImage.setColor(0.3f, 0.3f, 0.3f, 1f);

                        notchLabel.setText("Notches: " + charmManager.getUsedNotches() + " / " + CharmManager.MAX_NOTCHES);
                    } else {
                        descriptionLabel.setText("Not enough Notches!");
                    }
                }
            });

            mainTable.add(charmImage).size(100, 100).pad(15);
            colCount++;
            if (colCount % 4 == 0) mainTable.row();
        }

        mainTable.add(descriptionLabel).colspan(4).width(600).center().padTop(40).padBottom(30).row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = customFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.LIGHT_GRAY;
        mainTable.add(UIHelper.createGlowButton("CLOSE", btnStyle, 300, 60, () -> hide())).colspan(4).pad(20);
    }

    public CharmManager getCharmManager() {
        return charmManager;
    }
}
