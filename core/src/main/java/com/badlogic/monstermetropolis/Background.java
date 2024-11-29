package com.badlogic.monstermetropolis;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Background {
    void render(SpriteBatch batch, float delta);
    void renderObjects(SpriteBatch batch, float delta); // Added for object rendering
    void dispose();
}
