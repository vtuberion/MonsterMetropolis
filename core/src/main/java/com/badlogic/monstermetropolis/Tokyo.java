package com.badlogic.monstermetropolis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tokyo implements Background {

    private Texture cityBackground;
    private Texture jetTexture;
    private Texture tankTexture;
    private Texture heliTexture;
    private float bgOffset = 0;
    private float bgScrollSpeed = 4.0f;

    public Tokyo() {
        cityBackground = new Texture("tokyo_background.png");
        jetTexture = new Texture("jet.png");
        tankTexture = new Texture("tank.png");
        heliTexture = new Texture("helicopter.png");
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
        // Render jets, tanks, and helicopters specific to Tokyo
        batch.draw(jetTexture, 200, 450);  // Example position for jet
        batch.draw(tankTexture, 400, 150); // Example position for tank
        batch.draw(heliTexture, 600, 500); // Example position for helicopter
    }

    @Override
    public void dispose() {
        cityBackground.dispose();
        jetTexture.dispose();
        tankTexture.dispose();
        heliTexture.dispose();
    }
}
