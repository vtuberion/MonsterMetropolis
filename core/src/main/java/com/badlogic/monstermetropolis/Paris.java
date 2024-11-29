package com.badlogic.monstermetropolis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Paris implements Background {

    private Texture cityBackground;
    private Texture jetTexture;
    private Texture tankTexture;
    private float bgOffset = 0;
    private float bgScrollSpeed = 4.0f;

    public Paris() {
        cityBackground = new Texture("paris_background.jpg");
        jetTexture = new Texture("jet.png");
        tankTexture = new Texture("tank.png");
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
        // Render jets and tanks specific to Paris
        batch.draw(jetTexture, 150, 400);  // Example position for jet
        batch.draw(tankTexture, 350, 100); // Example position for tank
    }

    @Override
    public void dispose() {
        cityBackground.dispose();
        jetTexture.dispose();
        tankTexture.dispose();
    }
}
