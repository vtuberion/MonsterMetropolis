package com.badlogic.monstermetropolis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NYC implements Background {

    private Texture cityBackground;
    private Texture airlinerTexture;
    private Texture buildingTexture;
    private float bgOffset = 0;
    private float bgScrollSpeed = 4.0f;

    public NYC() {
        cityBackground = new Texture("nyc_background.png");
        airlinerTexture = new Texture("airliner.png");
        buildingTexture = new Texture("building1.png");
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        bgOffset += bgScrollSpeed * delta;
        if (bgOffset > cityBackground.getWidth()) {
            bgOffset -= cityBackground.getWidth();
        }

        batch.draw(cityBackground, -bgOffset, 0, 800, 600);
        batch.draw(cityBackground, -bgOffset + cityBackground.getWidth(), 0, 800, 600);
    }

    @Override
    public void renderObjects(SpriteBatch batch, float delta) {
        // Render airliners and buildings specific to NYC
        batch.draw(airlinerTexture, 100, 500);  // Example position for airliner
        batch.draw(buildingTexture, 300, 200); // Example position for building
    }

    @Override
    public void dispose() {
        cityBackground.dispose();
        airlinerTexture.dispose();
        buildingTexture.dispose();
    }
}
