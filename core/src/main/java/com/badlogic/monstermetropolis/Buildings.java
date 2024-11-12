package com.badlogic.monstermetropolis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Buildings {
    private Texture texture;
    private float x, y;
    private float speed;
    private float width, height;

    public Buildings(Texture texture, float x, float y, float width, float height, float speed) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    public void updatePosition(float delta) {
        x += speed * delta;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }
}
