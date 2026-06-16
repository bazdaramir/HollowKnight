package com.HollowKnight.view;

import com.HollowKnight.model.AnimatedImage;
import com.HollowKnight.model.App;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {
    private Stage stage;
    public App app ;
    public MainMenuScreen(App app) {this.app=app;}

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        Image background = new Image(new TextureRegion(new Texture(Gdx.files.internal("ui/MainMenu/controller_prompt_bg.png"))));
        background.setFillParent(true);
        stage.addActor(background);

        Image fog = new Image(new Texture(Gdx.files.internal("ui/MainMenu/fog_texture.png")));

        fog.setSize(1920 * 2, 1080 * 2);
        fog.getColor().a = 0.28f;
        fog.setPosition(0, -500);
        fog.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(-1920, 0, 40f),
                Actions.moveTo(0, 0)
            )
        ));
        stage.addActor(fog);


        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Fonts/trajan.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 40;
        parameter.color = Color.WHITE;
        parameter.shadowColor = new Color(0, 0, 0, 0.5f);
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;

        BitmapFont customFont = generator.generateFont(parameter);
        generator.dispose();

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = customFont;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.LIGHT_GRAY;
        textButtonStyle.up = null;
        textButtonStyle.down = null;
        textButtonStyle.checked = null;

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Image title = new Image(new TextureRegion(new Texture(Gdx.files.internal("ui/MainMenu/vheart_title.png"))));
        mainTable.add(title).center().top().padBottom(100f).padTop(-100f).row();
        String pointerPrefix = "ui/pointer_anim/main_menu_pointer_anim";

        Animation<TextureRegion> rightPointerAnim = createAnimation(pointerPrefix, 0, 10, 0.05f, true);
        Animation<TextureRegion> leftPointerAnim = createAnimation(pointerPrefix, 0, 10, 0.05f, false);

        mainTable.add(createMenuRow("START GAME", textButtonStyle, leftPointerAnim, rightPointerAnim, new Runnable() {
            @Override
            public void run() {
                System.out.println("استارت گیم");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new StartGameScreen(app));

            }
        })).pad(5).row();
        mainTable.add(createMenuRow("Guide", textButtonStyle, leftPointerAnim, rightPointerAnim, new Runnable() {
            @Override
            public void run() {
                System.out.println("گاییده");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GuideScreen(app));
            }
        })).pad(5).row();

        mainTable.add(createMenuRow("ACHIEVEMENTS", textButtonStyle, leftPointerAnim, rightPointerAnim, new Runnable() {
            @Override
            public void run() {
                System.out.println("دستاورد های داشاخی");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new AchievementsScreen(app));

            }
        })).pad(5).row();
        mainTable.add(createMenuRow("SETTING", textButtonStyle, leftPointerAnim, rightPointerAnim, new Runnable() {
            @Override
            public void run() {
                System.out.println("سوتینگ");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new SettingScreen(app));

            }
        })).pad(5).row();
        mainTable.add(createMenuRow("Quit Game", textButtonStyle, leftPointerAnim, rightPointerAnim, new Runnable() {
            @Override
            public void run() {

                System.out.println("بستیم");
                Gdx.app.exit();
            }
        })).pad(5).row();
        //mainTable.debug();
        stage.addActor(mainTable);
    }


    private Table createMenuRow(String text, TextButton.TextButtonStyle style, Animation<TextureRegion> leftAnim, Animation<TextureRegion> rightAnim, final Runnable action) {
        Table rowTable = new Table();

        final AnimatedImage leftPointer = new AnimatedImage(leftAnim, true);
        final AnimatedImage rightPointer = new AnimatedImage(rightAnim, false);

        leftPointer.setVisible(false);
        rightPointer.setVisible(false);

        TextButton button = new TextButton(text, style);

        rowTable.add(leftPointer).size(40, 40).padRight(15);
        rowTable.add(button);
        rowTable.add(rightPointer).size(40, 40).padLeft(15);

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                leftPointer.setVisible(true);
                rightPointer.setVisible(true);
                leftPointer.resetAnimation();
                rightPointer.resetAnimation();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                leftPointer.setVisible(false);
                rightPointer.setVisible(false);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (action != null) {
                    action.run();
                }
            }
        });

        return rowTable;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if(stage != null) stage.dispose();
    }
    private Animation<TextureRegion> createAnimation(String filePrefix, int startFrame, int endFrame, float frameDuration, boolean flipX) {
        Array<TextureRegion> frames = new Array<>();

        for (int i = startFrame; i <= endFrame; i++) {
            String fileName = String.format("%s%04d.png", filePrefix, i);
            Texture tex = new Texture(Gdx.files.internal(fileName));
            TextureRegion region = new TextureRegion(tex);

            if (flipX) {
                region.flip(true, false);
            }

            frames.add(region);
        }

        return new Animation<>(frameDuration, frames);
    }
}
