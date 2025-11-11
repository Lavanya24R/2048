package com.lavanya.game2048;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}
