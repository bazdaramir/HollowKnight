package com.HollowKnight.model.manager;
import com.HollowKnight.model.enums.KnightState;
import com.HollowKnight.model.EffectInstance;
import com.HollowKnight.model.Knight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class KnightEffectManager {

    // ── Shared animation templates (never mutated after construction) ────────
    private final Animation<TextureRegion> dashEffectAnim;
    private final Animation<TextureRegion> slashEffectAnim;
    private final Animation<TextureRegion> hurtEffectAnim;
  private final Animation<TextureRegion> focusEffectAnim;


    private boolean wasDashing     = false;
    private boolean wasAttacking   = false;
    private int     lastHealth     = -1;

    private final Array<EffectInstance> active = new Array<>();

    private static final float DASH_EFFECT_OFFSET_X = 10f;
    private static final float DASH_EFFECT_OFFSET_Y = 0f;

    private static final float SLASH_OFFSET_X = 60f;
    private static final float SLASH_OFFSET_Y = 10f;
    private final Animation<TextureRegion> blastAnim;
    private final Animation<TextureRegion> soulBallAnim;
    private final Animation<TextureRegion> soulScreamAnim;


    public KnightEffectManager() {


        Array<TextureRegion> dashFrames = new Array<>();
        for (int i = 0; i <= 6; i++) {
            dashFrames.add(load(String.format("ui/Knight/dash_effect/Dash effect_%03d.png", i)));
        }
        dashEffectAnim = new Animation<>(0.0214f, dashFrames, Animation.PlayMode.NORMAL);



        Array<TextureRegion> hurtFrames = new Array<>();
        for (int i = 0; i <= 11; i++) {
            hurtFrames.add(load(String.format("ui/Knight/hurteffect/HUD Cln_%03d.png", i)));
        }
        hurtEffectAnim = new Animation<>(0.01f, hurtFrames, Animation.PlayMode.NORMAL);



        Array<TextureRegion> slashFrames = new Array<>();
        for (int i = 0; i <= 3; i++) {
            slashFrames.add(load(String.format("ui/Knight/slash_effect/SlashEffect_%03d.png", i)));
        }
        slashEffectAnim = new Animation<>(0.05f, slashFrames, Animation.PlayMode.NORMAL);



        Array<TextureRegion> focusframes = new Array<>();
        for (int i = 0; i <= 6; i++) {
            focusframes.add(load(String.format("ui/Knight/Focus/Pure Vessel small focus ring - out of bounds impact%04d.png", i)));
        }
        focusEffectAnim = new Animation<>(0.02f, focusframes, Animation.PlayMode.NORMAL);



        Array<TextureRegion> blastFrames = new Array<>();
        for (int i = 0; i <= 7; i++) {
            blastFrames.add(load(String.format("ui/Knight/spells/Blast_%03d.png", i)));
        }
        blastAnim = new Animation<>(0.06f, blastFrames, Animation.PlayMode.NORMAL);



        Array<TextureRegion> ballFrames = new Array<>();
        for (int i = 0; i <= 3; i++) {
            ballFrames.add(load(String.format("ui/Knight/spells/SoulBall_%03d.png", i)));
        }
        soulBallAnim = new Animation<>(0.06f, ballFrames, Animation.PlayMode.LOOP);



        Array<TextureRegion> screamFrames = new Array<>();
        for (int i = 0; i <= 12; i++) {
            screamFrames.add(load(String.format("ui/Knight/Spells/SoulScream_%03d.png", i)));
        }
        soulScreamAnim = new Animation<>(0.05f, screamFrames, Animation.PlayMode.NORMAL);
    }

    public void update(Knight knight, float delta) {

        if (knight.isDashing && !wasDashing) {
            spawnDashEffect(knight);
        }
        wasDashing = knight.isDashing;

        boolean isAttacking = (knight.getState() == KnightState.ATTACKING);
        if (isAttacking && !wasAttacking) {
            spawnSlashEffect(knight);
        }
        wasAttacking = isAttacking;


        if (lastHealth == -1) {
            lastHealth = knight.health;
        }

        if (knight.health < lastHealth) {
            spawnHurtEffect(knight);
        }
        else if (knight.health > lastHealth) {
            spawnFocusEffect(knight);
        }

        lastHealth = knight.health;

        for (int i = active.size - 1; i >= 0; i--) {
            EffectInstance inst = active.get(i);
            inst.update(delta);
            if (inst.isFinished()) {
                active.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (EffectInstance inst : active) {
            TextureRegion frame = inst.getFrame();


            float w = frame.getRegionWidth();
            float h = frame.getRegionHeight();

            if (inst.flipX) {

                if (!frame.isFlipX()) frame.flip(true, false);
                batch.draw(frame, inst.x, inst.y, w, h);
                frame.flip(true, false); // restore
            } else {
                if (frame.isFlipX()) frame.flip(true, false);
                batch.draw(frame, inst.x, inst.y, w, h);
            }
        }
    }


    private void spawnDashEffect(Knight knight) {
        float frameWidth = dashEffectAnim.getKeyFrame(0).getRegionWidth();
        float frameHeight = dashEffectAnim.getKeyFrame(0).getRegionHeight();

        float spawnX;
        if (knight.isFacingRight) {
            spawnX = knight.position.x - frameWidth + 170f;
        } else {
            spawnX = knight.position.x + knight.hitbox.width +100;
        }

        float spawnY = knight.position.y + (knight.hitbox.height / 2f) - (frameHeight / 2f);

        active.add(new EffectInstance(dashEffectAnim, spawnX, spawnY, !knight.isFacingRight));
    }

    private void spawnSlashEffect(Knight knight) {
        float frameWidth = slashEffectAnim.getKeyFrame(0).getRegionWidth();
        float frameHeight = slashEffectAnim.getKeyFrame(0).getRegionHeight();

        float spawnX;
        if (knight.isFacingRight) {
            spawnX = knight.position.x + knight.hitbox.width +40;
        } else {
            spawnX = knight.position.x - frameWidth + 260f;
        }

        float spawnY = knight.position.y + 3f;

        active.add(new EffectInstance(slashEffectAnim, spawnX, spawnY, knight.isFacingRight));
    }


    private TextureRegion load(String path) {
        return new TextureRegion(new Texture(Gdx.files.internal(path)));
    }


    private void spawnHurtEffect(Knight knight) {
        float frameWidth = hurtEffectAnim.getKeyFrame(0).getRegionWidth();
        float frameHeight = hurtEffectAnim.getKeyFrame(0).getRegionHeight();
        float spawnX = knight.position.x + (knight.hitbox.width / 2f) - (frameWidth / 2f)+150;
        float spawnY = knight.position.y + (knight.hitbox.height / 2f) - (frameHeight / 2f);
        active.add(new EffectInstance(hurtEffectAnim, spawnX, spawnY, false));
    }

    private void spawnFocusEffect(Knight knight) {
        float frameWidth = focusEffectAnim.getKeyFrame(0).getRegionWidth();
        float frameHeight = focusEffectAnim.getKeyFrame(0).getRegionHeight();

        float spawnX = knight.position.x + (knight.hitbox.width / 2f) - (frameWidth / 2f)+120f;
        float spawnY = knight.position.y + (knight.hitbox.height / 2f) - (frameHeight / 2f);

        active.add(new EffectInstance(focusEffectAnim, spawnX, spawnY, false));
    }
    public void renderSpells(SpriteBatch batch, Knight knight) {


        for (Knight.VengefulSpirit vs : knight.fireballs) {
            float elapsedTime = vs.maxLife - vs.lifeTimer;
            boolean flip = !vs.isFacingRight;

            if (!blastAnim.isAnimationFinished(elapsedTime)) {
                TextureRegion baseBlast = blastAnim.getKeyFrame(elapsedTime, false);
                TextureRegion blastFrame = new TextureRegion(baseBlast);

                if (flip && !blastFrame.isFlipX()) blastFrame.flip(true, false);
                else if (!flip && blastFrame.isFlipX()) blastFrame.flip(true, false);

                float blastW = blastFrame.getRegionWidth() * 1.5f;
                float blastH = blastFrame.getRegionHeight() * 1.5f;

                batch.draw(blastFrame, vs.startX + 50 + (flip ? -30f : -30f), vs.startY -40f, blastW, blastH);
            }

            TextureRegion baseBall = soulBallAnim.getKeyFrame(elapsedTime, true);
            TextureRegion ballFrame = new TextureRegion(baseBall);

            if (flip && !ballFrame.isFlipX()) ballFrame.flip(true, false);
            else if (!flip && ballFrame.isFlipX()) ballFrame.flip(true, false);

            float ballW = ballFrame.getRegionWidth() * 1.5f;
            float ballH = ballFrame.getRegionHeight() * 1.5f;

            batch.draw(ballFrame, vs.hitbox.x +80+ (vs.hitbox.width - ballW)/2f, vs.hitbox.y +100f+ (vs.hitbox.height - ballH)/2f, ballW, ballH);
        }


        for (Knight.HowlingWraiths hw : knight.wraiths) {
            float elapsedTime = hw.maxLife - hw.lifeTimer;

            TextureRegion baseScream = soulScreamAnim.getKeyFrame(elapsedTime, false);
            TextureRegion screamFrame = new TextureRegion(baseScream);

            float screamW = screamFrame.getRegionWidth() * 1.8f;
            float screamH = screamFrame.getRegionHeight() * 1.8f;

            batch.draw(screamFrame, hw.hitbox.x+100 + (hw.hitbox.width - screamW)/2f, hw.hitbox.y-110, screamW, screamH);
        }
    }
}
