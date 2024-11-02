package com.badlogic.monstermetrodraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {
    private final monstermetrodraft game;
    private SpriteBatch batch;

    // Textures
    private Texture startButtonTexture;
    private Rectangle startButtonBounds;

    public MainMenuScreen(final monstermetrodraft game) {
        this.game = game;
        this.batch = new SpriteBatch();

        // Load the start button texture
        startButtonTexture = new Texture("start_button.png");
        startButtonBounds = new Rectangle(100, 100, startButtonTexture.getWidth(), startButtonTexture.getHeight());
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(startButtonTexture, startButtonBounds.x, startButtonBounds.y); // Draw start button
        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            // Check if the start button was clicked
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Invert y-axis

            if (startButtonBounds.contains(touchX, touchY)) {
                // Switch to the game screen
                game.setScreen(new GameScreen(game));
            }
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        startButtonTexture.dispose();
    }
}
