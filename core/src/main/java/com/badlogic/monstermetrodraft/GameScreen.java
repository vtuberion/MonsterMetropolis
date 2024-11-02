package com.badlogic.monstermetrodraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {
    private final monstermetrodraft game;
    private SpriteBatch batch;

    // Textures
    private Texture cityBackground;
    private Texture coinTexture;
    private Texture lizardLeftTexture;
    private Texture lizardRightTexture;
    private Texture spikeTexture;
    private Texture gameOverTexture;

    // Sprites
    private Rectangle lizardBounds;

    // Positions and State
    private float lizardX;
    private float lizardY;
    private boolean isGameOver;

    // List for coins
    private List<Rectangle> coins;
    private int score;
    private Random random;

    // Scrolling background variables
    private float bgScrollSpeed = 4.0f; // Increased speed of the background scrolling
    private float bgOffset = 0; // Current offset for the scrolling background

    public GameScreen(final monstermetrodraft game) {
        this.game = game;
        this.batch = new SpriteBatch();

        // Load assets
        cityBackground = new Texture("city_background.png");
        coinTexture = new Texture("coin.png");
        lizardLeftTexture = new Texture("lizard_left.png");
        lizardRightTexture = new Texture("lizard_right.png");
        spikeTexture = new Texture("spike.png");
        gameOverTexture = new Texture("game_over.jpg");

        lizardX = 50; // Starting position
        lizardY = 200; // Starting position
        lizardBounds = new Rectangle(lizardX, lizardY, lizardRightTexture.getWidth(), lizardRightTexture.getHeight());
        isGameOver = false;

        // Initialize coins
        coins = new ArrayList<>();
        random = new Random();
        spawnCoins();
        score = 0;
    }

    private void spawnCoins() {
        // Spawn up to three coins at random positions
        while (coins.size() < 3) {
            float coinX = random.nextFloat() * (Gdx.graphics.getWidth() - 32); // Random x position within screen width
            float coinY = random.nextFloat() * (Gdx.graphics.getHeight() - 32); // Random y position within screen height
            Rectangle coin = new Rectangle(coinX, coinY, 32, 32); // Create coin rectangle
            coins.add(coin);
        }
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the background offset for scrolling
        bgOffset += bgScrollSpeed * delta;
        if (bgOffset > cityBackground.getWidth()) {
            bgOffset -= cityBackground.getWidth();
        }

        batch.begin();
        // Draw the background twice to create a seamless scrolling effect
        batch.draw(cityBackground, -bgOffset, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(cityBackground, -bgOffset + cityBackground.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (!isGameOver) {
            // Draw the lizard
            batch.draw(lizardRightTexture, lizardX, lizardY); // Using lizardRightTexture directly
            // Draw coins
            for (Rectangle coin : coins) {
                batch.draw(coinTexture, coin.x, coin.y);
            }

            // Check for collisions
            checkCollisions();

        } else {
            // Draw game over screen
            batch.draw(gameOverTexture, 0, 0);
        }
        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (!isGameOver) {
            if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                touchPos.y = Gdx.graphics.getHeight() - touchPos.y; // Invert y-axis

                // Move lizard based on touch position
                lizardX = touchPos.x - lizardRightTexture.getWidth() / 2;
                lizardY = touchPos.y - lizardRightTexture.getHeight() / 2;
                lizardBounds.setPosition(lizardX, lizardY);
            }

            // Update bounds
            lizardBounds.setPosition(lizardX, lizardY);
        }
    }

    private void checkCollisions() {
        // Check for coin collisions
        for (int i = 0; i < coins.size(); i++) {
            Rectangle coin = coins.get(i);
            if (lizardBounds.overlaps(coin)) {
                // Remove the coin and increment the score
                coins.remove(i);
                score++;
                spawnCoins(); // Spawn new coins after collecting one
                break; // Exit loop after collecting a coin
            }
        }

        // Check for spikes (not implemented, but you'd check similar to coins)
        // If there are spikes, check if lizard collides with them and call gameOver();
    }

    private void gameOver() {
        isGameOver = true;
    }

    public void resize(int width, int height) {}

    public void pause() {}

    public void resume() {}

    public void hide() {}

    public void dispose() {
        batch.dispose();
        cityBackground.dispose();
        coinTexture.dispose();
        lizardLeftTexture.dispose();
        lizardRightTexture.dispose();
        spikeTexture.dispose();
        gameOverTexture.dispose();
    }
}
