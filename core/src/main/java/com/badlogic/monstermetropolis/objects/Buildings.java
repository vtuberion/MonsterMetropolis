package com.badlogic.monstermetropolis.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Buildings {
    private Texture texture;
    private float x, y;
    private float speed;
    private float width, height;
    private Rectangle bounds;

    public Buildings(Texture texture, float x, float y, float width, float height, float speed) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.bounds = new Rectangle(x, y, width, height/2);
    }

    public void updatePosition(float delta) {
        x += speed * delta;
        bounds.setPosition(x, y); // Update the bounding rectangle position
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
