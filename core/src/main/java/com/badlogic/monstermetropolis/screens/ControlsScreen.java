package com.badlogic.monstermetropolis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.monstermetropolis.monstermetropolis;

public class ControlsScreen implements Screen {
    private final monstermetropolis game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final Rectangle resumeButtonBounds;
    private final Rectangle mainMenuButtonBounds;
    private final GlyphLayout titleLayout;
    private final GlyphLayout jumpControlLayout;

    private final GlyphLayout playerGoalLayout;
    private final GlyphLayout resumeButtonLayout;
    private final GlyphLayout mainMenuButtonLayout;

    public ControlsScreen(final monstermetropolis game) {
        this.game = game;

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(2); // Increase font size for better visibility
        this.shapeRenderer = new ShapeRenderer();

        // Initialize layout objects for centering the text
        this.titleLayout = new GlyphLayout();
        this.jumpControlLayout = new GlyphLayout();
        this.resumeButtonLayout = new GlyphLayout();
        this.mainMenuButtonLayout = new GlyphLayout();
        this.playerGoalLayout = new GlyphLayout();

        // Define button bounds (position and size)
        resumeButtonBounds = new Rectangle(
            (float) (Gdx.graphics.getWidth() - 200) / 2,
            ((float) Gdx.graphics.getHeight() / 2) - 80, // Positioned below the jump control
            200,
            60
        );
        mainMenuButtonBounds = new Rectangle(
            (float) (Gdx.graphics.getWidth() - 200) / 2,
            ((float) Gdx.graphics.getHeight() / 2) - 160, // Positioned below the resume button
            200,
            60
        );
    }

    @Override
    public void show() {
        // Additional initialization code can go here
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1); // Background color

        // Enable blending for transparency (useful for buttons)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw the button background shapes first
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.5f, 1.0f, 1); // Light blue for buttons
        shapeRenderer.rect(resumeButtonBounds.x, resumeButtonBounds.y,
            resumeButtonBounds.width, resumeButtonBounds.height);
        shapeRenderer.rect(mainMenuButtonBounds.x, mainMenuButtonBounds.y,
            mainMenuButtonBounds.width, mainMenuButtonBounds.height);
        shapeRenderer.end();

        // Then draw text (title and button labels)
        batch.begin();

        // Update layout with the text to be drawn
        titleLayout.setText(font, "Controls and Goals");
        jumpControlLayout.setText(font, "Jump: Spacebar");
        playerGoalLayout.setText(font,"Goal: Jump on buildings and \n collect coins to score points.");
        resumeButtonLayout.setText(font, "Play");
        mainMenuButtonLayout.setText(font, "Main Menu");

        // Draw title centered on the screen
        font.setColor(1, 1, 1, 1); // White color for the title
        font.draw(batch, "Controls and Goals",
            (Gdx.graphics.getWidth() - titleLayout.width) / 2,
            (float) Gdx.graphics.getHeight() / 2 + 200); // Position slightly above center

        // Draw control instruction text (centered)
        font.draw(batch, "Jump: Spacebar",
            (Gdx.graphics.getWidth() - jumpControlLayout.width) / 2,
            (float) Gdx.graphics.getHeight() / 2 + 140); // Position below the title

        font.draw(batch, "Goal: Jump on buildings and \ncollect coins to score points.",
            (Gdx.graphics.getWidth() - playerGoalLayout.width) / 2,
            (float) Gdx.graphics.getHeight() / 2+100 ); // Position below the title
        // Draw button text (centered within their respective bounds)
        font.draw(batch, "Play",
            resumeButtonBounds.x + (resumeButtonBounds.width - resumeButtonLayout.width) / 2,
            resumeButtonBounds.y + (resumeButtonBounds.height + resumeButtonLayout.height) / 2);
        font.draw(batch, "Main Menu",
            mainMenuButtonBounds.x + (mainMenuButtonBounds.width - mainMenuButtonLayout.width) / 2,
            mainMenuButtonBounds.y + (mainMenuButtonBounds.height + mainMenuButtonLayout.height) / 2);

        batch.end();

        // Handle user input (button clicks)
        handleInput();
    }
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (resumeButtonBounds.contains(touchX, touchY)) {
                game.setScreen(new GameScreen(game));
                dispose();
            } else if (mainMenuButtonBounds.contains(touchX, touchY)) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        // Handle resizing if needed
    }

    @Override
    public void pause() {
        // Handle pause if needed
    }

    @Override
    public void resume() {
        // Handle resume if needed
    }

    @Override
    public void hide() {
        // Handle hiding the screen if needed
    }

    @Override
    public void dispose() {
        // Clean up resources
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
