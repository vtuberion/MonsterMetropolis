package com.badlogic.monstermetropolis.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.monstermetropolis.objects.Buildings;
import com.badlogic.monstermetropolis.objects.Explosion;
import com.badlogic.monstermetropolis.objects.Jets;
import com.badlogic.monstermetropolis.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Paris {
    private List<Jets> jets;
    private List<Buildings> buildings;
    private List<Explosion> explosions;
    private Texture[] buildingTextures;
    private Texture jetTexture;
    private Random random;
    private float buildingSpawnTimer = 0f;
    private float buildingSpawnDelay = 2f;

    private Sound explosionSound;
    private Sound buildingScoreSound;

    public Paris(Texture[] buildingTextures, Texture jetTexture) {
        this.buildingTextures = buildingTextures;
        this.jetTexture = jetTexture;
        this.jets = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.random = new Random();
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion-91872.mp3")); //Sound for when explosion is triggered (drop.wav is a placeholder for testing)
        buildingScoreSound = Gdx.audio.newSound(Gdx.files.internal("coin-recieved.mp3"));

    }

    public void spawnJets() {
        float jetHeight = 64;
        float jetY = random.nextFloat() * (Gdx.graphics.getHeight() / 2 - jetHeight) + (Gdx.graphics.getHeight() / 2-40);
        float jetX = Gdx.graphics.getWidth();
        float speed = -300;

        jets.add(new Jets(jetTexture, jetX, jetY, speed));
    }

    public void spawnBuildings() {
        float buildingX = Gdx.graphics.getWidth();
        float buildingY = 0;
        float speed = -450;
        Texture texture = buildingTextures[MathUtils.random(buildingTextures.length - 1)];
        Buildings building = new Buildings(texture, buildingX, buildingY, 200, 300, -100);
        buildings.add(building);
    }

    public void updateAndRender(float delta, SpriteBatch batch) {
        // Update and render jets
        for (Jets jet : jets) {
            jet.update(delta);
            jet.render(batch);
        }

        // Update and render buildings
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

    public void checkCollisions(Rectangle dinoBounds) {
        // Check collisions with jets
        for (int i = jets.size() - 1; i >= 0; i--) {
            Jets jet = jets.get(i);
            if (dinoBounds.overlaps(jet.getBounds())) {
                explosionSound.play();
                jets.remove(i); // Remove jet
                GameScreen.loselife(); // Decrease a life
                spawnJets();
            }
        }

        // Check collisions with buildings
        for (int i = buildings.size() - 1; i >= 0; i--) {
            Buildings building = buildings.get(i);
            if (dinoBounds.overlaps(building.getBounds()) &&
                (dinoBounds.y > dinoBounds.height)) {
                buildings.remove(i); // Remove building
                GameScreen.score++;
                buildingScoreSound.play();
            }
            else if(dinoBounds.overlaps(building.getBounds())){
                explosionSound.play();
                buildings.remove(i);
                GameScreen.loselife();
            }
        }
    }
}
