package com.badlogic.monstermetrodraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {
    private final monstermetropolis game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont gameOverFont; // New font for Game Over screen

    // Textures
    private Texture cityBackground;
    private Texture coinTexture;
    private Texture lizardRightTexture;

    // Sprites and Positions
    private Rectangle lizardBounds;
    private float lizardX, lizardY;
    private float lizardVelocityY = 0;
    private float gravity = -500f;
    private float jumpVelocity = 250f;
    private boolean isGameOver;
    private boolean isGameStarted;

    // Lists for coins and jets
    private List<Rectangle> coins;
    private List<Jet> jets;
    private int score;
    private Random random;

    // Scrolling background and coin variables
    private float bgScrollSpeed = 4.0f;
    private float coinScrollSpeed = 2.0f; // Slower than the background
    private float bgOffset = 0;

    public GameScreen(final monstermetropolis game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(); // Use default font for simplicity
        this.gameOverFont = new BitmapFont(); // Initialize game over font

        // Set font size for game over screen (you can adjust the size)
        gameOverFont.getData().setScale(2.0f); // Increase font size

        // Load assets
        cityBackground = new Texture("city_background.png");
        coinTexture = new Texture("coin.png");
        lizardRightTexture = new Texture("dino_right.png"); // Changed to use dino_right.png

        resetGame();
    }

    private void resetGame() {
        lizardX = 50;
        lizardY = 0; // Set to 0 to spawn on the ground
        lizardBounds = new Rectangle(lizardX, lizardY, lizardRightTexture.getWidth(), lizardRightTexture.getHeight());
        lizardVelocityY = 0;
        isGameOver = false;
        isGameStarted = false;
        score = 0;

        // Initialize coins and jets
        coins = new ArrayList<>();
        jets = new ArrayList<>();
        random = new Random();
        spawnCoins();
        spawnJet();
    }


    private void spawnCoins() {
        while (coins.size() < 3) {
            float coinX = random.nextFloat() * (Gdx.graphics.getWidth() - 32);
            float coinY = random.nextFloat() * (Gdx.graphics.getHeight() - 32);
            Rectangle coin = new Rectangle(coinX, coinY, 32, 32);
            coins.add(coin);
        }
    }

    private void spawnJet() {
        // Define the height of the jet texture (adjust if needed)
        float jetHeight = 64;

        // Spawn jets only above half the screen height, while ensuring they don't overflow
        float jetY = random.nextFloat() * (Gdx.graphics.getHeight() / 2 - jetHeight) + (Gdx.graphics.getHeight() / 2);
        boolean fromLeft = random.nextBoolean();

        float jetX = fromLeft ? -lizardRightTexture.getWidth() : Gdx.graphics.getWidth();
        float speed = fromLeft ? 300 : -300; // Faster speed for jets
        Texture jetTexture = fromLeft ? new Texture("jet_right.png") : new Texture("jet_left.png"); // Use appropriate jet texture

        jets.add(new Jet(jetX, jetY, speed, jetTexture)); // Pass jet texture to Jet constructor
    }



    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1); // Clear screen color

        batch.begin();

        if (!isGameStarted) {
            if (Gdx.input.isTouched()) {
                isGameStarted = true;
            }
        } else {
            // Update background scroll
            bgOffset += bgScrollSpeed * delta;
            if (bgOffset > cityBackground.getWidth()) {
                bgOffset -= cityBackground.getWidth();
            }

            // Draw the background twice for seamless scrolling
            batch.draw(cityBackground, -bgOffset, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.draw(cityBackground, -bgOffset + cityBackground.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            if (!isGameOver) {
                // Draw the lizard
                batch.draw(lizardRightTexture, lizardX, lizardY);

                // Draw and update coins
                updateCoins(delta);
                for (Rectangle coin : coins) {
                    batch.draw(coinTexture, coin.x, coin.y);
                }

                // Draw jets
                for (Jet jet : jets) {
                    batch.draw(jet.texture, jet.bounds.x, jet.bounds.y);
                }

                // Update lizard position with jumping physics
                applyGravity(delta);
                handleInput();
                checkCollisions();

                // Move jets
                moveJets(delta);
            } else {
                // Set the game over background color to black
                ScreenUtils.clear(0, 0, 0, 1);
                drawGameOver();
                if (Gdx.input.isTouched()) {
                    resetGame();
                }
            }
        }

        batch.end();
    }

    private void drawGameOver() {
        gameOverFont.draw(batch, "Game Over!", Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 + 50);
        gameOverFont.draw(batch, "Tap to Respawn", Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2);
    }

    private void applyGravity(float delta) {
        // Update vertical velocity with gravity and apply to lizard's Y position
        lizardVelocityY += gravity * delta;
        lizardY += lizardVelocityY * delta;

        // Keep lizard within screen bounds
        if (lizardY < 0) {
            lizardY = 0;
            lizardVelocityY = 0;
        } else if (lizardY + lizardRightTexture.getHeight() > Gdx.graphics.getHeight()) {
            lizardY = Gdx.graphics.getHeight() - lizardRightTexture.getHeight();
            lizardVelocityY = 0;
        }

        lizardBounds.setPosition(lizardX, lizardY);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            lizardVelocityY = jumpVelocity;
        }
    }

    private void updateCoins(float delta) {
        for (int i = 0; i < coins.size(); i++) {
            Rectangle coin = coins.get(i);
            coin.x -= coinScrollSpeed;

            if (coin.x < -coin.width) {
                coin.x = Gdx.graphics.getWidth();
                coin.y = random.nextFloat() * (Gdx.graphics.getHeight() - coin.height);
            }
        }
    }

    private void moveJets(float delta) {
        for (int i = 0; i < jets.size(); i++) {
            Jet jet = jets.get(i);
            jet.bounds.x += jet.speed * delta;

            if ((jet.speed > 0 && jet.bounds.x > Gdx.graphics.getWidth()) ||
                (jet.speed < 0 && jet.bounds.x + jet.bounds.width < 0)) {
                jets.remove(i);
                i--;
                spawnJet();
            }
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < coins.size(); i++) {
            Rectangle coin = coins.get(i);
            if (lizardBounds.overlaps(coin)) {
                coins.remove(i);
                score++;
                spawnCoins();
                break;
            }
        }

        for (Jet jet : jets) {
            if (lizardBounds.overlaps(jet.bounds)) {
                gameOver();
                break;
            }
        }
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
        font.dispose();
        gameOverFont.dispose();
        cityBackground.dispose();
        coinTexture.dispose();
        lizardRightTexture.dispose();

        // Dispose jet textures to prevent memory leaks
        for (Jet jet : jets) {
            jet.texture.dispose();
        }
    }

    // Jet class to handle jet properties
    private class Jet {
        Rectangle bounds;
        Texture texture;
        float speed;

        Jet(float x, float y, float speed, Texture texture) {
            this.bounds = new Rectangle(x, y, 64, 64); // Assuming jet size is 64x64
            this.speed = speed;
            this.texture = texture; // Set the texture for the jet
        }
    }
}
