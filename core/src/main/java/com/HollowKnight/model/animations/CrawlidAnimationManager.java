package com.HollowKnight.model.animations;

import com.HollowKnight.model.enums.CrawlidStation;
import com.HollowKnight.model.mob.Crawlid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;

/**
 * Loads Crawlid's animation strips and returns the correct frame per instance.
 */
public class CrawlidAnimationManager {

    private final Animation<TextureRegion> walkAnim;
    private final Animation<TextureRegion> turnAnim;
    private final Animation<TextureRegion> deathLandAnim;
    private final Animation<TextureRegion> deathAirAnim;

    private final IdentityMap<Crawlid, StateClock> clocks = new IdentityMap<>();

    private static class StateClock {
        CrawlidStation previousStation = null;
        float stateStartTime = 0f;
    }

    public CrawlidAnimationManager() {
        Array<TextureRegion> walk = new Array<>();
        for (int i = 0; i <= 3; i++) walk.add(load(String.format("ui/Crawlid/Walk_%03d.png", i)));
        walkAnim = new Animation<>(0.12f, walk, Animation.PlayMode.LOOP);



        Array<TextureRegion> turn = new Array<>();
        for (int i = 0; i <= 1; i++) turn.add(load(String.format("ui/Crawlid/turn_%03d.png", i)));
        turnAnim = new Animation<>(0.08f, turn, Animation.PlayMode.NORMAL);



        Array<TextureRegion> deathLand = new Array<>();
        for (int i = 0; i <= 1; i++) deathLand.add(load(String.format("ui/Crawlid/Death Land_%03d.png", i)));
        deathLandAnim = new Animation<>(0.1f, deathLand, Animation.PlayMode.NORMAL);



        Array<TextureRegion> deathAir = new Array<>();
        for (int i = 0; i <= 2; i++) deathAir.add(load(String.format("ui/Crawlid/Death Air_%03d.png", i)));
        deathAirAnim = new Animation<>(0.1f, deathAir, Animation.PlayMode.NORMAL);
    }

    public TextureRegion getFrame(Crawlid crawlid, float globalStateTime) {
        CrawlidStation station = crawlid.getStation();

        StateClock clock = clocks.get(crawlid);
        if (clock == null) {
            clock = new StateClock();
            clocks.put(crawlid, clock);
        }
        if (station != clock.previousStation) {
            clock.stateStartTime  = globalStateTime;
            clock.previousStation = station;
        }
        float t = globalStateTime - clock.stateStartTime;

        TextureRegion masterFrame;
        switch (station) {
            case TURN:       masterFrame = turnAnim.getKeyFrame(t, false); break;
            case DEATH_LAND: masterFrame = deathLandAnim.getKeyFrame(t, false); break;
            case DEATH_AIR:  masterFrame = deathAirAnim.getKeyFrame(t, false);  break;
            case WALK:
            default:         masterFrame = walkAnim.getKeyFrame(t, true);  break;
        }

        TextureRegion finalFrame = new TextureRegion(masterFrame);

        boolean shouldFaceRight = crawlid.isFacingRight;

        if (shouldFaceRight && !finalFrame.isFlipX()) {
            finalFrame.flip(true, false);
        } else if (!shouldFaceRight && finalFrame.isFlipX()) {
            finalFrame.flip(true, false);
        }

        return finalFrame;

    }

    public void forget(Crawlid crawlid) {
        clocks.remove(crawlid);
    }

    private TextureRegion load(String path) {
        return new TextureRegion(new Texture(Gdx.files.internal(path)));
    }
}
