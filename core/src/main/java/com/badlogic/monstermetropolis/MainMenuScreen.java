package com.badlogic.monstermetropolis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {
    private final monstermetropolis game;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private Rectangle startButtonBounds;
    private GlyphLayout titleLayout;
    private GlyphLayout buttonLayout;

    public MainMenuScreen(final monstermetropolis game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(2); // Make the font bigger
        this.shapeRenderer = new ShapeRenderer();

        // Initialize GlyphLayouts
        this.titleLayout = new GlyphLayout();
        this.buttonLayout = new GlyphLayout();

        // Define the bounds for the start button
        startButtonBounds = new Rectangle(
            (Gdx.graphics.getWidth() - 200) / 2,
            (Gdx.graphics.getHeight() - 60) / 2,
            200,
            60
        );
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // First, draw all shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Draw button background
        shapeRenderer.setColor(0.2f, 0.5f, 1.0f, 1);
        shapeRenderer.rect(startButtonBounds.x, startButtonBounds.y,
            startButtonBounds.width, startButtonBounds.height);
        shapeRenderer.end();

        // Then, draw all text
        batch.begin();

        // Update layouts
        titleLayout.setText(font, "Monster Metropolis");
        buttonLayout.setText(font, "START");

        // Draw title
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Monster Metropolis",
            (Gdx.graphics.getWidth() - titleLayout.width) / 2,
            Gdx.graphics.getHeight() - 50);

        // Draw button text
        font.draw(batch, "START",
            startButtonBounds.x + (startButtonBounds.width - buttonLayout.width) / 2,
            startButtonBounds.y + (startButtonBounds.height + buttonLayout.height) / 2);

        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (startButtonBounds.contains(touchX, touchY)) {
                game.setScreen(new GameScreen(game));
                dispose(); // Clean up this screen when switching
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
        font.dispose();
        shapeRenderer.dispose();
    }
}
