package com.HollowKnight;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.swing.*;

import static com.badlogic.gdx.Gdx.input;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture image;
    private FitViewport viewport;
    private OrthographicCamera camera;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
        image = new Texture("libgdx.png");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);



        // این چیزیرو عوض نمیکنه ، صرفا کمرا رو داره تغییر میده یعنی ( یعنی نقطه مبدا جهان ثابته همه چی )
        // صرفا ویو کمرا داره روی مپ ما تغییر میکنه
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.translate(-1f, 0f, 0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.translate(1f, 0f, 0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.translate(0f, -1f, 0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.translate(0f, 1f, 0f);
        }
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
