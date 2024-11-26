package com.badlogic.monstermetropolis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class Explosion {
    private final Texture explosionTexture;
    private final Rectangle bounds;
    private float timer; // Time before the explosion disappears
    private final float duration; // Explosion duration

    public Explosion(float x, float y, float size, float duration) {
        this.explosionTexture = new Texture("explosion.png");
        this.bounds = new Rectangle(x - size / 2, y - size / 2, size, size);
        this.timer = 0f;
        this.duration = duration;
    }

    public boolean isFinished() {
        return timer >= duration;
    }

    public void update(float delta) {
        timer += delta;
    }

    public void render(Batch batch) {
        batch.draw(explosionTexture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
