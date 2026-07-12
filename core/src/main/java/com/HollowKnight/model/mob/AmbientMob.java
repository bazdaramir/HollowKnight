package com.HollowKnight.model.mob;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;


public class AmbientMob {
    public Vector2 position = new Vector2();
    public float offsetX;
    public float offsetY;
    public float speedX;
    public String type;
    public float time = 0;
    public boolean facingRight;

    public AmbientMob(float offsetX, float offsetY, float speedX, String type) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.speedX = speedX;
        this.type = type;
        this.facingRight = speedX >= 0;
    }


    public void attachToCamera(float delta, float camX, float camY,
                               float halfViewportWidth, float halfViewportHeight, float margin) {
        time += delta;
        offsetX += speedX * delta;

        float leftBound  = -halfViewportWidth + margin;
        float rightBound =  halfViewportWidth - margin;

        if (offsetX > rightBound) {
            offsetX = rightBound;
            speedX = -Math.abs(speedX);
            facingRight = false;
            offsetY = MathUtils.random(-halfViewportHeight + 60f, halfViewportHeight - 60f);
        } else if (offsetX < leftBound) {
            offsetX = leftBound;
            speedX = Math.abs(speedX);
            facingRight = true;
            offsetY = MathUtils.random(-halfViewportHeight + 60f, halfViewportHeight - 60f);
        }

        float wave = MathUtils.sin(time * 3f) * 20f;
        position.set(camX + offsetX, camY + offsetY + wave);
    }
}
