package com.HollowKnight;

import com.HollowKnight.model.App;
import com.HollowKnight.view.MainMenuScreen;
import com.badlogic.gdx.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private App app;

    @Override
    public void create() {
        app = new App();
        setScreen(new MainMenuScreen(app));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);


    }

    @Override
    public void render() {

        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();

    }
}
