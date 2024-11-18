package com.badlogic.monstermetropolis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {
    private final monstermetropolis game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont gameOverFont;
    private float damageCooldown = 1f; // 1 second cooldown
    private float damageTimer = 0f;

    // Textures
    private Texture cityBackground;
    private Texture coinTexture;
    private Texture dinoRightTexture;
    private Texture heartTexture;
    private Texture halfHeartTexture;
    private Texture[] buildingTextures;

    // Sprite and position data
    private Rectangle dinobounds;
    private float dinoX, dinoY;
    private float lizardVelocityY = 0;
    private float gravity = -500f;
    private float jumpVelocity = 250f;
    private boolean isGameOver;
    private boolean isGameStarted;

    // Lists for coins, jets, and buildings
    private List<Rectangle> coins;
    private List<Jet> jets;
    private List<Buildings> buildings;
    private int score;
    private int lives;
    private Random random;

    // Scrolling background variables
    private float bgScrollSpeed = 4.0f;
    private float coinScrollSpeed = 2.0f;
    private float bgOffset = 0;

    // Building spawn variables
    private float buildingSpawnTimer = 0f;
    private float buildingSpawnDelay = 2f; // Spawn a new building every 2 seconds

    public GameScreen(final monstermetropolis game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(); // Use default font for simplicity
        this.gameOverFont = new BitmapFont(); // Initialize game over font
        gameOverFont.getData().setScale(2.0f); // Increase font size for game over text

        // Load assets
        cityBackground = new Texture("city_background.png");
        coinTexture = new Texture("coin.png");
        dinoRightTexture = new Texture("dino_right.png");
        heartTexture = new Texture("heart.png");
        halfHeartTexture = new Texture("half-heart.png");

        // Initialize building textures array
        buildingTextures = new Texture[]{
            new Texture("building1.png"),
            new Texture("building2.png"),
            new Texture("building3.png")
        };

        resetGame();
    }

    private void resetGame() {
        dinoX = 50;
        dinoY = 0;
        dinobounds = new Rectangle(dinoX, dinoY, dinoRightTexture.getWidth(), dinoRightTexture.getHeight());
        lizardVelocityY = 0;
        isGameOver = false;
        isGameStarted = false;
        score = 0;
        lives = 6;

        // Initialize coins and jets
        coins = new ArrayList<>();
        jets = new ArrayList<>();
        random = new Random();
        spawnCoins();
        spawnJet();

        // Initialize and place buildings
        buildings = new ArrayList<>();
        spawnBuildings(1); // Spawn 1 building initially
        buildingSpawnTimer = 0f; // Reset the building spawn timer
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
        float jetHeight = 64;
        float jetY = random.nextFloat() * (Gdx.graphics.getHeight() / 2 - jetHeight) + (Gdx.graphics.getHeight() / 2);
        boolean fromLeft = random.nextBoolean();

        float jetX = fromLeft ? -dinoRightTexture.getWidth() : Gdx.graphics.getWidth();
        float speed = fromLeft ? 300 : -300;
        Texture jetTexture = fromLeft ? new Texture("jet_right.png") : new Texture("jet_left.png");

        jets.add(new Jet(jetX, jetY, speed, jetTexture));
    }

    private void spawnBuildings(int count) {
        for (int i = 0; i < count; i++) {
            float buildingX = Gdx.graphics.getWidth();
            float buildingY = 0;
            Texture texture = buildingTextures[MathUtils.random(buildingTextures.length - 1)];
            Buildings building = new Buildings(texture, buildingX, buildingY, 200, 300, -100);
            buildings.add(building);
        }
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();

        if (!isGameStarted) {
            if (Gdx.input.isTouched()) {
                isGameStarted = true;
            }
        } else {
            bgOffset += bgScrollSpeed * delta;
            if (bgOffset > cityBackground.getWidth()) {
                bgOffset -= cityBackground.getWidth();
            }

            batch.draw(cityBackground, -bgOffset, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.draw(cityBackground, -bgOffset + cityBackground.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            if (!isGameOver) {
                batch.draw(dinoRightTexture, dinoX, dinoY);

                updateCoins(delta);
                for (Rectangle coin : coins) {
                    batch.draw(coinTexture, coin.x, coin.y);
                }

                for (Jet jet : jets) {
                    batch.draw(jet.texture, jet.bounds.x, jet.bounds.y);
                }

                // Update and render buildings
                for (Buildings building : buildings) {
                    building.updatePosition(delta);
                    building.render(batch);
                }

                // Spawn new buildings periodically
                buildingSpawnTimer += delta;
                if (buildingSpawnTimer >= buildingSpawnDelay) {
                    buildingSpawnTimer = 0f;
                    spawnBuildings(1); // Spawn 1 new building
                }
                updateCooldown(delta);
                drawLives();
                applyGravity(delta);
                handleInput();
                checkCollisions();
                moveJets(delta);
            } else {
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
    private void drawLives() {
        for (int i = 0; i < lives / 2; i++) {
            batch.draw(heartTexture, 10 + i * 40, Gdx.graphics.getHeight() - 40, 32, 32);
        }
        if (lives % 2 != 0) {
            batch.draw(halfHeartTexture, 10 + (lives / 2) * 40, Gdx.graphics.getHeight() - 40, 32, 32);
        }
    }
    private void applyGravity(float delta) {
        lizardVelocityY += gravity * delta;
        dinoY += lizardVelocityY * delta;

        if (dinoY < 0) {
            dinoY = 0;
            lizardVelocityY = 0;
        } else if (dinoY + dinoRightTexture.getHeight() > Gdx.graphics.getHeight()) {
            dinoY = Gdx.graphics.getHeight() - dinoRightTexture.getHeight();
            lizardVelocityY = 0;
        }

        dinobounds.setPosition(dinoX, dinoY);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            lizardVelocityY = jumpVelocity;
        }
    }

    private void updateCoins(float delta) {
        for (Rectangle coin : coins) {
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
            if (dinobounds.overlaps(coin)) {
                coins.remove(i);
                score++;
                spawnCoins();
                break;
            }
        }

        for (Jet jet : jets) {
            if (dinobounds.overlaps(jet.bounds)) {
                loseLife();
                break;
            }
        }
    }
    private void updateCooldown(float delta) {
        if (damageTimer > 0) {
            damageTimer -= delta;
        }
    }

    private void loseLife() {
        if (damageTimer <= 0 && lives > 0) {
            lives--; // Decrement lives
            damageTimer = damageCooldown; // Reset cooldown
            if (lives == 0) {
                gameOver();
            }
        }
    }
    private void gameOver() {
        isGameOver = true;
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
        gameOverFont.dispose();
        cityBackground.dispose();
        coinTexture.dispose();
        dinoRightTexture.dispose();
        heartTexture.dispose();
        halfHeartTexture.dispose();
        for (Texture texture : buildingTextures) {
            texture.dispose();
        }

        for (Jet jet : jets) {
            jet.texture.dispose();
        }
    }

    // Jet class to manage jet properties
    private class Jet {
        Rectangle bounds;
        Texture texture;
        float speed;

        Jet(float x, float y, float speed, Texture texture) {
            this.bounds = new Rectangle(x, y, 64, 64);
            this.speed = speed;
            this.texture = texture;
        }
    }
}
