package com.badlogic.monstermetropolis;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tokyo extends ScreenAdapter {
    private SpriteBatch batch;
    private Texture jet, tank, helicopter;

    public Tokyo(SpriteBatch batch) {
        this.batch = batch;
        jet = new Texture("jet.png");
        tank = new Texture("tank.png");
        helicopter = new Texture("helicopter.png");
    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(jet, 150, 250);
        batch.draw(tank, 400, 200);
        batch.draw(helicopter, 600, 300);
        batch.end();
    }

    @Override
    public void dispose() {
        jet.dispose();
        tank.dispose();
        helicopter.dispose();
    }
}
