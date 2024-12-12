package com.badlogic.monstermetropolis.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.monstermetropolis.objects.Buildings;
import com.badlogic.monstermetropolis.objects.Explosion;
import com.badlogic.monstermetropolis.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tokyo {
    public List<Airliner> airliners;
    private List<Buildings> buildings;
    private List<Explosion> explosions;
    private Texture[] buildingTextures;
    private Texture airlinerTexture;
    private Random random;
    private float buildingSpawnTimer = 0f;
    private float buildingSpawnDelay = 2f;

    public Tokyo(Texture[] buildingTextures, Texture airlinerTexture) {
        this.buildingTextures = buildingTextures;
        this.airlinerTexture = airlinerTexture;
        this.airliners = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.random = new Random();
    }
    public void spawnAirliner() {
        float airlinerHeight = 64;
        float airlinerY = random.nextFloat() * (Gdx.graphics.getHeight() / 2 - airlinerHeight) + (Gdx.graphics.getHeight() / 2);
        float airlinerX = Gdx.graphics.getWidth();
        float speed = -300;

        airliners.add(new Airliner(airlinerTexture, airlinerX, airlinerY, speed));
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
        for (Airliner airliner : airliners) {
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
    public void checkCollisions(Rectangle dinobounds, int lives) {
        // Check collisions with jets
        for (int i = airliners.size() - 1; i >= 0; i--) {
            Airliner airliner = airliners.get(i);
            if (dinobounds.overlaps(airliner.bounds)) {
                airliners.remove(i); // Remove airliner
                GameScreen.loselife(); // Decrease a life
            }
        }

        // Check collisions with buildings
        for (int i = buildings.size() - 1; i >= 0; i--) {
            Buildings building = buildings.get(i);
            if (dinobounds.overlaps(building.getBounds())) {
                buildings.remove(i); // Remove building
                // No life penalty; adjust if needed
            }
        }
    }

    private static class Airliner {
        private Texture texture;
        private Rectangle bounds;
        private float speed;

        public Airliner(Texture texture, float startX, float startY, float speed) {
            this.texture = texture;
            this.bounds = new Rectangle(startX, startY, texture.getWidth(), texture.getHeight());
            this.speed = speed;
        }

        public void update(float delta) {
            bounds.x += speed * delta;

            // Reset position if the airliner moves off-screen
            if (bounds.x + bounds.width < 0) {
                bounds.x = Gdx.graphics.getWidth();
            }
        }

        public void render(SpriteBatch batch) {
            batch.draw(texture, bounds.x, bounds.y);
        }
    }
}
