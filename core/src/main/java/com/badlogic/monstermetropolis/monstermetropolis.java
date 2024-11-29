package com.badlogic.monstermetropolis;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class monstermetropolis extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this)); // Start with MainMenuScreen
    }

    @Override
    public void render() {
        super.render(); // Call Game's render method to handle the current screen
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        getScreen().dispose(); // Dispose the current screen when the game ends
    }
}
