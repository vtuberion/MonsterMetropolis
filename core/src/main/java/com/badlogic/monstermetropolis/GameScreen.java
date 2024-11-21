package com.badlogic.monstermetropolis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

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

    private long lastJetTime;

    public GameScreen(final monstermetropolis game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(); // Use default font for simplicity
        this.gameOverFont = new BitmapFont(); // Initialize game over font
        gameOverFont.getData().setScale(2.0f); // Increase font size for game over text

        // Load assets
        cityBackground = new Texture("nyc_background.png");
        //https://img.freepik.com/free-photo/8-bit-graphics-pixels-scene-with-city-sunset_23-2151120910.jpg
        cityBackground2 = new Texture("paris_background.jpg");
        //https://i.pinimg.com/736x/7b/e7/64/7be7647ef1d6ba714dce5e451ccfa354.jpg
        cityBackground3 = new Texture("tokyo_background.png");
        //https://preview.redd.it/tokyo-tower-v0-tys6oq2smz091.png?auto=webp&s=3f400039932128dd5eff05b98fbae12707e318d9
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
        float jetX = 0; // initialize the jetX variable
        jetX=Gdx.graphics.getWidth(); //Set the jet sprite's x to spawn off the screen
        float speed = -300;
        Texture jetTexture =  new Texture("jet_left.png");

        jets.add(new Jet(jetX, jetY, speed, jetTexture)); // Add the new jet sprite to a list of jet sprites
        lastJetTime = TimeUtils.millis();
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
                batch.draw(dinoRightTexture, dinoX, dinoY,
                    dinoRightTexture.getWidth() * 2, dinoRightTexture.getHeight() * 2);

                updateCoins(delta);
                for (Rectangle coin : coins) {
                    batch.draw(coinTexture, coin.x, coin.y,
                        coinTexture.getWidth() * 2, coinTexture.getHeight() * 2);
                }
                game.font.draw(batch, "Score: " + score, Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight()-10);
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
        // Update explosions
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
        if(jets.size()<2) {
            spawnJet();
        }
        else if(score>10 && jets.size()<4){ //Possible condition for modifying difficulty
            // (increase jet spawns/spawn a new enemy type)
            spawnJet();
        }

        batch.end();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
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
                explosions.add(new Explosion(jet.bounds.x, jet.bounds.y, 64, 1.0f));
                jets.remove(jet);
                loseLife();
                break;
            }
        }
        for (Buildings building : buildings) {
            if (dinobounds.overlaps(building.getBounds())) {
                explosions.add(new Explosion(building.getBounds().x, building.getBounds().y, 128, 1.0f)); // Larger explosion for buildings
                buildings.remove(building);
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
