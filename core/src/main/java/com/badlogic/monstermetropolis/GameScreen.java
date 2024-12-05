package com.badlogic.monstermetropolis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    final monstermetropolis game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont gameOverFont;

    private Texture dinoRightTexture;
    private Texture heartTexture;
    private Texture halfHeartTexture;
    private Texture coinTexture;
    private Texture[] backgrounds;
    private Texture[] buildingTextures;
    private Texture airlinerTexture;

    private Rectangle dinobounds;
    private List<Rectangle> coins;
    private Random random;
    private NYC nyc;

    private float dinoX, dinoY;
    private float lizardVelocityY;
    private static final float GRAVITY = -400f;
    private static final float JUMP_VELOCITY = 400f;
    private int jumpCount = 0; // To track double jump
    private static float damageCooldown = 1f; // 1 second cooldown
    private static float damageTimer = 0f;

    private static boolean isGameOver;
    private boolean isGameStarted;
    private int score;
    private static int lives;
    private int currentBackgroundIndex = 0;
    private float timer = 30f; // Timer in seconds

    public GameScreen(final monstermetropolis game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.gameOverFont = new BitmapFont();
        gameOverFont.getData().setScale(2.0f);

        // Load assets
        dinoRightTexture = new Texture("dino.png");
        heartTexture = new Texture("heart.png");
        halfHeartTexture = new Texture("half-heart.png");
        coinTexture = new Texture("coin.png");

        backgrounds = new Texture[]{
            new Texture("nyc_background.png"),
            new Texture("paris_background.jpg"),
            new Texture("tokyo_background.png")
        };

        buildingTextures = new Texture[]{
            new Texture("building1.png"),
            new Texture("building2.png"),
            new Texture("building3.png")
        };

        airlinerTexture = new Texture("airliner.png");

        nyc = new NYC(buildingTextures, airlinerTexture);  // Pass airliner texture to NYC
        resetGame();
    }

    public static void loselife() {
        if (damageTimer <= 0 && lives > 0) {
            lives--; // Decrement lives
            damageTimer = damageCooldown; // Reset cooldown
            if (lives == 0) {
                gameOver();
            }
        }
    }

    private void resetGame() {
        dinoX = 50;
        dinoY = 0;
        lizardVelocityY = 0;
        isGameOver = false;
        isGameStarted = false;
        score = 0;
        lives = 6;
        timer = 30f;

        dinobounds = new Rectangle(dinoX, dinoY, dinoRightTexture.getWidth() * 2, dinoRightTexture.getHeight() * 2); // Double size
        coins = new ArrayList<>();
        random = new Random();
        spawnCoins();

        nyc.spawnAirliner();
        nyc.spawnBuildings();
    }

    private void spawnCoins() {
        while (coins.size() < 3) {
            float coinX = random.nextFloat() * (Gdx.graphics.getWidth() - 32);
            float coinY = random.nextFloat() * (Gdx.graphics.getHeight() - 32);
            Rectangle coin = new Rectangle(coinX, coinY, 32, 32);
            coins.add(coin);
        }
    }


    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            lizardVelocityY = JUMP_VELOCITY;
        }
    }


    private void applyGravity(float delta) {
        lizardVelocityY += GRAVITY * delta;
        dinoY += lizardVelocityY * delta;

        if (dinoY < 0) {
            dinoY = 0;
            lizardVelocityY = 0;
            jumpCount = 0; // Reset jump count on landing
        } else if (dinoY + dinoRightTexture.getHeight() * 2 > Gdx.graphics.getHeight()) {
            dinoY = Gdx.graphics.getHeight() - dinoRightTexture.getHeight() * 2;
            lizardVelocityY = 0;
        }

        dinobounds.setPosition(dinoX, dinoY);
    }

    private void moveCoins() {
        float coinScrollSpeed = 2.0f;
        for (Rectangle coin : coins) {
            coin.x -= coinScrollSpeed;
            if (coin.x < -coin.width) {
                coin.x = Gdx.graphics.getWidth();
                coin.y = random.nextFloat() * (Gdx.graphics.getHeight() - coin.height);
            }
        }
    }

    private void checkCollisions() {
        for (int i = coins.size() - 1; i >= 0; i--) {
            Rectangle coin = coins.get(i);
            if (dinobounds.overlaps(coin)) {
                coins.remove(i);
                score++;
                spawnCoins(); // Maintain the number of coins
            }
        }
        nyc.checkCollisions(dinobounds, lives);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        handleInput();
        applyGravity(delta);
        moveCoins();
        checkCollisions();
        updateTimer(delta);
        batch.begin();

        // Draw background
        batch.draw(backgrounds[currentBackgroundIndex], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw Dino
        batch.draw(dinoRightTexture, dinoX, dinoY, dinoRightTexture.getWidth() * 2, dinoRightTexture.getHeight() * 2);

        // Draw coins
        for (Rectangle coin : coins) {
            batch.draw(coinTexture, coin.x, coin.y,
                coinTexture.getWidth() * 2, coinTexture.getHeight() * 2);
        }

        // Draw lives
        for (int i = 0; i < lives / 2; i++) {
            batch.draw(heartTexture, 10 + i * 40, Gdx.graphics.getHeight() - 40, 64, 64);
        }
        if (lives % 2 != 0) {
            batch.draw(halfHeartTexture, 10 + (lives / 2) * 40, Gdx.graphics.getHeight() - 40, 64, 64);
        }
        game.font.draw(batch, "Score: " + score, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 10);
        drawTimer();

        // Update and render NYC elements
        nyc.updateAndRender(delta, batch);

        // Game Over Text
        if (isGameOver) {
            drawGameOver();
        }

        batch.end();
    }

    private void updateTimer(float delta) {
        timer -= delta;
        if (timer <= 0) {
            isGameOver = true;
            timer = 0;
        }
    }

    private void drawTimer() {
        int minutes = (int) (timer / 60);
        int seconds = (int) (timer % 60);
        String timerText = String.format("Time: %02d:%02d", minutes, seconds);
        game.font.draw(batch, timerText, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 30);
    }
    private void drawGameOver() {
        ScreenUtils.clear(0, 0, 0, 1);
        gameOverFont.draw(batch, "Game Over!", Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 + 50);
        gameOverFont.draw(batch, "Tap to Respawn", Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2);
    }
    private static void gameOver() {
        isGameOver = true;
    }
    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        gameOverFont.dispose();
        dinoRightTexture.dispose();
        heartTexture.dispose();
        halfHeartTexture.dispose();
        coinTexture.dispose();
        airlinerTexture.dispose();
        for (Texture background : backgrounds) {
            background.dispose();
        }
        for (Texture texture : buildingTextures) {
            texture.dispose();
        }
    }

    @Override
    public void show() {
    }
}
