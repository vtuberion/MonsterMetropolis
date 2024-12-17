package com.badlogic.monstermetropolis.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.monstermetropolis.objects.Airliners;
import com.badlogic.monstermetropolis.objects.Buildings;
import com.badlogic.monstermetropolis.objects.Explosion;
import com.badlogic.monstermetropolis.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NYC {
    public List<Airliners> airliners;
    private List<Buildings> buildings;
    private List<Explosion> explosions;
    private Texture[] buildingTextures;
    private Texture airlinerTexture;
    private Random random;
    private float buildingSpawnTimer = 0f;
    private float buildingSpawnDelay = 2f;

    private Sound explosionSound;
    private Sound buildingScoreSound;

    public NYC(Texture[] buildingTextures, Texture airlinerTexture) {
        this.buildingTextures = buildingTextures;
        this.airlinerTexture = airlinerTexture;
        this.airliners = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.random = new Random();
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion-91872.mp3")); //Sound for when explosion is triggered (drop.wav is a placeholder for testing)
        buildingScoreSound = Gdx.audio.newSound(Gdx.files.internal("coin-recieved.mp3"));

    }

    public void spawnAirliner() {
        float airlinerHeight = 64;
        float airlinerY = random.nextFloat() * (Gdx.graphics.getHeight() / 2 - airlinerHeight) + (Gdx.graphics.getHeight() / 2);
        float airlinerX = Gdx.graphics.getWidth();
        float speed = -300;

        airliners.add(new Airliners(airlinerTexture, airlinerX, airlinerY, speed));
    }

    public void spawnBuildings() {
        for (int i = 0; i < 1; i++) {
            float buildingX = Gdx.graphics.getWidth();
            float buildingY = 0;
            Texture texture = buildingTextures[MathUtils.random(buildingTextures.length - 1)];
            Buildings building = new Buildings(texture, buildingX, buildingY, 200, 300, -100);
            buildings.add(building);
        }
    }

    public void updateAndRender(float delta, SpriteBatch batch) {
        for (Airliners airliner : airliners) {
            airliner.update(delta);
            airliner.render(batch);
        }

        for (Buildings building : buildings) {
            building.updatePosition(delta);
            building.render(batch);
        }

        // Spawn buildings periodically
        buildingSpawnTimer += delta;
        if (buildingSpawnTimer >= buildingSpawnDelay) {
            buildingSpawnTimer = 0f;
            spawnBuildings();
        }
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public void checkCollisions(Rectangle dinobounds) {
        // Check collisions with airliners
        for (int i = airliners.size() - 1; i >= 0; i--) {
            Airliners airliner = airliners.get(i);
            if (dinobounds.overlaps(airliner.getBounds())) {
                explosionSound.play(); //explosion sound to play on collision
                airliners.remove(i); // Remove airliner
                GameScreen.loselife(); // Decrease a life
                spawnAirliner();
            }
        }

        // Check collisions with buildings
        for (int i = buildings.size() - 1; i >= 0; i--) {
            Buildings building = buildings.get(i);
            if (dinobounds.overlaps(building.getBounds()) &&
                (dinobounds.y > dinobounds.height)) {
                buildings.remove(i); // Remove building
                GameScreen.score++;
                buildingScoreSound.play();
            }
            else if(dinobounds.overlaps(building.getBounds())){
                explosionSound.play();
                buildings.remove(i);
                GameScreen.loselife();
            }
        }
    }
}
