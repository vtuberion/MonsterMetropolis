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
    // Timer variables
    private float timeRemaining = 30f; // 3 minutes in seconds
    private boolean isTimeUp = false;
    private String currentBackground = "NYC"; // Starting background


    private Texture[] backgrounds;

    private boolean backgroundSwitched = false;

    // Textures
    private Texture cityBackground1;
    private Texture cityBackground2;
    private Texture cityBackground3;
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
    private List<Explosion> explosions;
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
    private boolean firstSwitchDone;
    private boolean secondSwitchDone;
    public GameScreen(final monstermetropolis game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(); // Use default font for simplicity
        this.gameOverFont = new BitmapFont(); // Initialize game over font
        gameOverFont.getData().setScale(2.0f); // Increase font size for game over text

        // Load assets
        cityBackground1 = new Texture("nyc_background.png");
        //https://img.freepik.com/free-photo/8-bit-graphics-pixels-scene-with-city-sunset_23-2151120910.jpg
        cityBackground2 = new Texture("paris_background.jpg");
        //https://i.pinimg.com/736x/7b/e7/64/7be7647ef1d6ba714dce5e451ccfa354.jpg
        cityBackground3 = new Texture("tokyo_background.png");
        //https://preview.redd.it/tokyo-tower-v0-tys6oq2smz091.png?auto=webp&s=3f400039932128dd5eff05b98fbae12707e318d9
        backgrounds = new Texture[]{
            cityBackground1,
            cityBackground2,
            cityBackground3
        };
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
        explosions = new ArrayList<>();
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
        float jetX = Gdx.graphics.getWidth();
        float speed = -300;
        Texture jetTexture = new Texture("jet_left.png");

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
            if (!isGameOver) {
                // Update timer
                updateTimer(delta);

                // Scroll background logic
                bgOffset += bgScrollSpeed * delta;
                if (bgOffset > cityBackground1.getWidth()) {
                    bgOffset -= cityBackground1.getWidth();
                }

                batch.draw(cityBackground1, -bgOffset, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                batch.draw(cityBackground1, -bgOffset + cityBackground1.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

                batch.draw(dinoRightTexture, dinoX, dinoY,
                    dinoRightTexture.getWidth() * 2, dinoRightTexture.getHeight() * 2);

                updateCoins(delta);
                for (Rectangle coin : coins) {
                    batch.draw(coinTexture, coin.x, coin.y,
                        coinTexture.getWidth() * 2, coinTexture.getHeight() * 2);
                }

                game.font.draw(batch, "Score: " + score, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 10);
                drawTimer(); // Draw the countdown timer

                for (Jet jet : jets) {
                    batch.draw(jet.texture, jet.bounds.x, jet.bounds.y);
                }

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

            for (int i = 0; i < explosions.size(); i++) {
                Explosion explosion = explosions.get(i);
                explosion.update(delta);
                if (explosion.isFinished()) {
                    explosions.remove(i);
                    i--;
                } else {
                    explosion.render(batch);
                }
            }
        }
        if(jets.size()<2){
            spawnJet();
        }else if(score>30 && jets.size()<4){
            spawnJet(); //Possible condition to increase difficulty by
            // spawning more enemies or different enemy types
        }

        batch.end();
    }

    // Update the timer
    private void updateTimer(float delta) {
        // Decrease the time remaining for the current background
        timeRemaining -= delta;

        if (timeRemaining <= 0) {
            // Transition to the next background based on the current state
            switch (currentBackground) {
                case "NYC":
                    cityBackground1 = cityBackground2; // Switch to Paris background
                    currentBackground = "Paris";
                    break;

                case "Paris":
                    cityBackground1 = cityBackground3; // Switch to Tokyo background
                    currentBackground = "Tokyo";
                    break;

                case "Tokyo":
                    cityBackground1 = cityBackground1; // Switch back to NYC background
                    currentBackground = "NYC";
                    break;
            }

            // Reset the timer for the new background
            timeRemaining = 30f; // Each background lasts for 30 seconds
        }
    }


    private void resetTimer() {
        timeRemaining = 15f; // Reset the timer (set to desired total starting time)
        isTimeUp = false; // Reset the timer state
        firstSwitchDone = false; // Reset the first background switch flag
        secondSwitchDone = false; // Reset the second background switch flag
    }

    // Draw the timer on screen
    private void drawTimer() {
        int minutes = (int) (timeRemaining / 60);
        int seconds = (int) (timeRemaining % 60);
        String timerText = String.format("Time: %02d:%02d", minutes, seconds);
        game.font.draw(batch, timerText, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 30);
    }

    private void drawGameOver() {
        gameOverFont.draw(batch, "Game Over!", Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 + 50);
        gameOverFont.draw(batch, "Tap to Respawn", Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2);
    }
    private void drawLives() {
        for (int i = 0; i < lives / 2; i++) {
            batch.draw(heartTexture, 10 + i * 40, Gdx.graphics.getHeight() - 40, 64, 64);
        }
        if (lives % 2 != 0) {
            batch.draw(halfHeartTexture, 10 + (lives / 2) * 40, Gdx.graphics.getHeight() - 40, 64, 64);
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
        for (int i = jets.size() - 1; i >= 0; i--) {
            Jet jet = jets.get(i);
            jet.bounds.x += jet.speed * delta;

            if ((jet.speed > 0 && jet.bounds.x > Gdx.graphics.getWidth()) ||
                (jet.speed < 0 && jet.bounds.x + jet.bounds.width < 0)) {
                jets.remove(i);
                spawnJet(); // Spawn a new jet when one leaves the screen
            }
        }
    }


    private void checkCollisions() {
        for (int i = coins.size() - 1; i >= 0; i--) {
            Rectangle coin = coins.get(i);
            if (dinobounds.overlaps(coin)) {
                coins.remove(i);
                score++;
                spawnCoins();
                break;
            }
        }

        for (int i = jets.size() - 1; i >= 0; i--) {
            Jet jet = jets.get(i);
            if (dinobounds.overlaps(jet.bounds)) {
                explosions.add(new Explosion(jet.bounds.x, jet.bounds.y, 64, 1.0f));
                jets.remove(i); // Remove the jet safely
                spawnJet(); // Spawn a new jet after collision
                loseLife();
                break;
            }
        }

        for (int i = buildings.size() - 1; i >= 0; i--) {
            Buildings building = buildings.get(i);
            if (dinobounds.overlaps(building.getBounds())) {
                explosions.add(new Explosion(building.getBounds().x + 75, building.getBounds().y + 50, 128, 1.0f));
                buildings.remove(i); // Remove the building safely
                score++;
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
        cityBackground1.dispose();
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
