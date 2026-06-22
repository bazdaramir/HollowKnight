package com.HollowKnight.view;

import com.HollowKnight.model.AnimatedImage;
import com.HollowKnight.model.AudioManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.io.InputStream;

public class UIHelper {

    private static Texture cachedGlowTex = null;
    private static Animation<TextureRegion> leftPointerAnim = null;
    private static Animation<TextureRegion> rightPointerAnim = null;

    public static void initResources(int glowWidth, int glowHeight) {
        if (leftPointerAnim == null || rightPointerAnim == null) {
            String pointerPrefix = "ui/pointer_anim/main_menu_pointer_anim";
            Array<TextureRegion> leftFrames = new Array<>();
            Array<TextureRegion> rightFrames = new Array<>();

            for (int i = 0; i <= 10; i++) {
                String fileName = String.format("%s%04d.png", pointerPrefix, i);
                Texture tex = new Texture(Gdx.files.internal(fileName));
                TextureRegion leftRegion = new TextureRegion(tex);
                TextureRegion rightRegion = new TextureRegion(tex);
                rightRegion.flip(true, false);
                leftFrames.add(leftRegion);
                rightFrames.add(rightRegion);
            }
            leftPointerAnim = new Animation<>(0.05f, leftFrames);
            rightPointerAnim = new Animation<>(0.05f, rightFrames);
        }

        if (cachedGlowTex == null) {
            try {
                cachedGlowTex = new Texture(Gdx.files.internal("ui/MainMenu/button_glow.png"));
            } catch (Exception e) {
                Pixmap pixmap = new Pixmap(glowWidth, glowHeight, Pixmap.Format.RGBA8888);
                float cx = glowWidth / 2f; float cy = glowHeight / 2f;
                float rx = glowWidth / 2f; float ry = glowHeight / 2f;
                Color knightGold = new Color(0.9f, 0.85f, 0.6f, 1f);

                for (int x = 0; x < glowWidth; x++) {
                    for (int y = 0; y < glowHeight; y++) {
                        float dx = (x - cx) / rx;
                        float dy = (y - cy) / ry;
                        float distance = dx * dx + dy * dy;

                        if (distance <= 1.0f) {
                            float linearAlpha = 1.0f - (float) Math.sqrt(distance);
                            Color pixelColor = new Color(Color.WHITE).lerp(knightGold, (float) Math.sqrt(distance));
                            pixelColor.a = linearAlpha * linearAlpha * 0.7f;
                            pixmap.setColor(pixelColor);
                            pixmap.drawPixel(x, y);
                        }
                    }
                }
                cachedGlowTex = new Texture(pixmap);
                pixmap.dispose();
            }
        }
    }

    public static Animation<TextureRegion> getLeftPointerAnim() {
        initResources(460, 70);
        return leftPointerAnim;
    }

    public static Animation<TextureRegion> getRightPointerAnim() {
        initResources(460, 70);
        return rightPointerAnim;
    }

    //  فلش ها و هاله نوری که زیرش میاد
    public static Table createGlowButton(String text, TextButton.TextButtonStyle style, int glowWidth, int glowHeight, final Runnable action) {
        initResources(glowWidth, glowHeight);

        Table rowTable = new Table();
        final AnimatedImage leftPointer = new AnimatedImage(leftPointerAnim, true);
        final AnimatedImage rightPointer = new AnimatedImage(rightPointerAnim, false);

        leftPointer.setVisible(false); rightPointer.setVisible(false);
        leftPointer.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        rightPointer.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);

        final TextButton button = new TextButton(text, style);

        final Image glowImage = new Image(cachedGlowTex);
        glowImage.setScaling(com.badlogic.gdx.utils.Scaling.stretch);
        glowImage.getColor().a = 0f;
        glowImage.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);

        Stack buttonStack = new Stack();
        buttonStack.add(glowImage);
        buttonStack.add(button);

        rowTable.add(leftPointer).size(40, 40).padRight(15);
        rowTable.add(buttonStack).width(glowWidth).height(glowHeight);
        rowTable.add(rightPointer).size(40, 40).padLeft(15);

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                leftPointer.setVisible(true); rightPointer.setVisible(true);
                leftPointer.resetAnimation(); rightPointer.resetAnimation();
                AudioManager.getInstance().playHoverSound();

                glowImage.clearActions();
                glowImage.addAction(Actions.forever(Actions.sequence(
                    Actions.alpha(0.65f, 0.4f),
                    Actions.alpha(0.15f, 0.4f)
                )));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                leftPointer.setVisible(false); rightPointer.setVisible(false);
                glowImage.clearActions();
                glowImage.addAction(Actions.alpha(0f, 0.2f));
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().playClickSound();
                if (action != null) action.run();
            }
        });

        return rowTable;
    }
    private static Animation<TextureRegion> headerAnim = null;

    // انیمیشن هدر
    public static Animation<TextureRegion> getHeaderAnim() {
        if (headerAnim == null) {
            Array<TextureRegion> frames = new Array<>();
            for (int i = 1; i <= 8; i++) {
                String fileName = String.format("ui/MainMenu/pause_top_fleur%04d.png", i);
                if (!Gdx.files.internal(fileName).exists()) {
                    fileName = String.format("ui/MainMenu/pause_top_fleur_%04d.png", i);
                }
                Texture tex = new Texture(Gdx.files.internal(fileName));
                frames.add(new TextureRegion(tex));
            }
            headerAnim = new Animation<>(0.08f, frames);
        }
        return headerAnim;
    }
    private static Animation<TextureRegion> profileFleurAnim = null;

    // لود کردن انیمیشن پروفایل
    public static Animation<TextureRegion> getProfileFleurAnim() {
        if (profileFleurAnim == null) {
            Array<TextureRegion> frames = new Array<>();

            for (int i = 0; i <= 12; i++) {
                String fileName = String.format("ui/MainMenu/profile_fleur%04d.png", i);
                try {

                    Texture tex = new Texture(Gdx.files.internal(fileName));
                    frames.add(new TextureRegion(tex));
                } catch (Exception e) {
                }
            }
            if (frames.size == 0) {
                Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888); pm.setColor(Color.CLEAR); pm.fill();
                frames.add(new TextureRegion(new Texture(pm))); pm.dispose();
            }
            profileFleurAnim = new Animation<>(0.05f, frames);
        }
        return profileFleurAnim;
    }
}
