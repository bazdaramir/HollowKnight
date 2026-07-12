package com.HollowKnight.model.manager;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CameraShake {
    private final Vector3 defaultPos;
    private float intensity;
    private float duration;
    private float timer;

    public CameraShake() {
        defaultPos = new Vector3();
    }

    public void shake(float intensity, float duration) {
        if (this.timer > 0 && this.intensity > intensity) return;

        this.intensity = intensity;
        this.duration = duration;
        this.timer = duration;
    }

    public void update(float delta, OrthographicCamera camera, float targetX, float targetY) {
        defaultPos.set(targetX, targetY, 0);

        if (timer > 0) {
            timer -= delta;
            float currentIntensity = intensity * (timer / duration);
            camera.position.x = defaultPos.x + MathUtils.random(-currentIntensity, currentIntensity);
            camera.position.y = defaultPos.y + MathUtils.random(-currentIntensity, currentIntensity);
        } else {
            camera.position.set(defaultPos);
        }
        camera.update();
    }
}
