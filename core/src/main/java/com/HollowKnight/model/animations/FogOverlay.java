package com.HollowKnight.model.animations;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

// مه منو: ذرات ریز نورانی که آروم از بالا میان پایین و کمی هم کنار میخورن
// همه چی همینجا ساخته میشه و فایل تصویری جدا نمیخواد
public class FogOverlay extends Actor {

    private static final int COUNT = 110;
    // اندازه ها توی فضای 1920x1080 استیج هستن
    private static final float WORLD_W = 1920f;
    private static final float WORLD_H = 1080f;

    // نقطه نرم مشترک بین همه صفحه ها، مثل بقیه تکسچرهای کش شده UIHelper
    private static Texture dotTex;

    private final float[] x = new float[COUNT];
    private final float[] y = new float[COUNT];
    private final float[] size = new float[COUNT];
    private final float[] fallSpeed = new float[COUNT];
    private final float[] driftAmp = new float[COUNT];
    private final float[] driftFreq = new float[COUNT];
    private final float[] phase = new float[COUNT];
    private final float[] baseAlpha = new float[COUNT];
    private final float[] twinkleFreq = new float[COUNT];

    private float time;

    public FogOverlay() {
        setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        for (int i = 0; i < COUNT; i++) {
            spawn(i);
            // بار اول همه جای صفحه پخش بشن نه اینکه از بالا شروع کنن
            y[i] = MathUtils.random(-40f, WORLD_H + 40f);
        }
    }

    // ذره های درشت تر تندتر و روشن ترن تا حس عمق بده
    private void spawn(int i) {
        float depth = MathUtils.random();
        x[i] = MathUtils.random(-40f, WORLD_W + 40f);
        y[i] = WORLD_H + MathUtils.random(10f, 120f);
        size[i] = MathUtils.lerp(7f, 22f, depth);
        fallSpeed[i] = MathUtils.lerp(26f, 78f, depth) * MathUtils.random(0.85f, 1.15f);
        driftAmp[i] = MathUtils.random(10f, 44f);
        driftFreq[i] = MathUtils.random(0.12f, 0.42f);
        phase[i] = MathUtils.random(MathUtils.PI2);
        baseAlpha[i] = MathUtils.lerp(0.20f, 0.55f, depth);
        twinkleFreq[i] = MathUtils.random(0.25f, 0.8f);
    }

    private static Texture dot() {
        if (dotTex == null) {
            int s = 64, r = s / 2;
            Pixmap pm = new Pixmap(s, s, Pixmap.Format.RGBA8888);
            pm.setBlending(Pixmap.Blending.None);
            for (int py = 0; py < s; py++) {
                for (int px = 0; px < s; px++) {
                    float dx = (px - r + 0.5f) / r, dy = (py - r + 0.5f) / r;
                    float d = (float) Math.sqrt(dx * dx + dy * dy);
                    // افت نرم از مرکز تا لبه، هسته پررنگ و هاله محو
                    float a = d >= 1f ? 0f : (1f - d) * (1f - d) * (1f - d * 0.55f);
                    pm.setColor(1f, 1f, 1f, a);
                    pm.drawPixel(px, py);
                }
            }
            dotTex = new Texture(pm);
            dotTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            pm.dispose();
        }
        return dotTex;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        time += delta;
        for (int i = 0; i < COUNT; i++) {
            y[i] -= fallSpeed[i] * delta;
            if (y[i] < -size[i] * 2f) spawn(i);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float alpha = getColor().a * parentAlpha;
        if (alpha <= 0f) return;

        Texture tex = dot();
        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();
        // جمع شونده، روی هر دو تم تیره مثل نور ملایم دیده میشه نه لکه سفید
        batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE);

        for (int i = 0; i < COUNT; i++) {
            float drawX = x[i] + MathUtils.sin(time * driftFreq[i] + phase[i]) * driftAmp[i];
            float twinkle = 0.75f + 0.25f * MathUtils.sin(time * twinkleFreq[i] + phase[i]);
            // نزدیک لبه بالا و پایین محو میشن تا ظاهر و ناپدید شدنشون یهویی نباشه
            float edgeFade = Math.min(1f, Math.min(y[i] + size[i] * 2f, WORLD_H + 60f - y[i]) / 90f);
            if (edgeFade <= 0f) continue;

            batch.setColor(1f, 1f, 1f, baseAlpha[i] * twinkle * edgeFade * alpha);
            batch.draw(tex, drawX - size[i] / 2f, y[i] - size[i] / 2f, size[i], size[i]);
        }

        batch.setColor(1f, 1f, 1f, 1f);
        batch.setBlendFunction(srcFunc, dstFunc);
    }
}
