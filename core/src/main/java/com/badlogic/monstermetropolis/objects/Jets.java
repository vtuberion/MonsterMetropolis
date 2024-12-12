package com.badlogic.monstermetropolis.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jets {
    private Texture texture;
    private Rectangle bounds;
    private float speed;

    public Jets(Texture texture, float startX, float startY, float speed) {
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

    public Rectangle getBounds() {
        return bounds;
    }
}
