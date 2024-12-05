package com.badlogic.monstermetropolis;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Paris extends ScreenAdapter {
    private SpriteBatch batch;
    private Texture jet, tank;

    public Paris(SpriteBatch batch) {
        this.batch = batch;
        jet = new Texture("jet.png");
        tank = new Texture("tank.png");
    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(jet, 200, 300);
        batch.draw(tank, 500, 250);
        batch.end();
    }

    @Override
    public void dispose() {
        jet.dispose();
        tank.dispose();
    }
}
