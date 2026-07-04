package com.HollowKnight.view;

import com.HollowKnight.controller.KnightController;
import com.HollowKnight.model.*;
import com.HollowKnight.model.animations.*;
import com.HollowKnight.model.manager.KnightEffectManager;
import com.HollowKnight.model.mob.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class GameScreen implements Screen {
    private App app;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private KnightAnimationManager animManager;
    private CrawlidAnimationManager crawlidAnimManager;
    private HuskHornheadAnimationManager huskAnimManager;
    private CrystalGuardianAnimationManager crystalAnimManager;
    private MossflyAnimationManager mossflyAnimManager;

    // اینا برای اون جون ها و ماسک و ایناست که فعلا پیدا نکردم تکسچر هاشو
    private OrthographicCamera uiCamera;
    private Texture maskFullTex, maskEmptyTex;
    private Texture vesselBgTex, vesselLiquidTex;

    private PopupOverlay popupOverlay;
    private BitmapFont hintFont;
    private KnightEffectManager effectManager;
    private float stateTime = 0;
    private Knight knight;
    private KnightController controller;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMapHelper mapHelper;
    private Array<Block> mapBlocks;
    private FalseKnight boss;
    private FalseKnightAnimationManager bossAnimManager;

    private Array<Enemy> activeEnemies;
    private Zote zote;
    private ZoteAnimationManager zoteAnimManager;

    private int[] backgroundLayers;
    private int[] foregroundLayers;

    public GameScreen(App app) {
        this.app = app;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1760, 900, camera);
        batch = new SpriteBatch();

        animManager        = new KnightAnimationManager();
        crawlidAnimManager = new CrawlidAnimationManager();
        huskAnimManager    = new HuskHornheadAnimationManager();
        crystalAnimManager = new CrystalGuardianAnimationManager();
        mossflyAnimManager    = new MossflyAnimationManager();
        effectManager      = new KnightEffectManager();
        zoteAnimManager    = new ZoteAnimationManager();
        popupOverlay = new PopupOverlay(this, app);
        mapHelper = new TiledMapHelper();
        map = mapHelper.loadMap("maps/map1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

//        maskFullTex = new Texture(Gdx.files.internal());
//        maskEmptyTex = new Texture(Gdx.files.internal());
//        vesselBgTex = new Texture(Gdx.files.internal());
//        vesselLiquidTex = new Texture(Gdx.files.internal());

        Array<Integer> bgIndices = new Array<>();
        Array<Integer> fgIndices = new Array<>();

        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);
            if (layer instanceof TiledMapTileLayer) {
                String layerName = layer.getName().toLowerCase();
                if (layerName.equals("forground")) {
                    fgIndices.add(i);
                } else if (layerName.equals("base") || layerName.equals("background")
                    || layerName.equals("bakground0") || layerName.equals("image")) {
                    bgIndices.add(i);
                }
            }
        }

        backgroundLayers = new int[bgIndices.size];
        for (int i = 0; i < bgIndices.size; i++) backgroundLayers[i] = bgIndices.get(i);

        foregroundLayers = new int[fgIndices.size];
        for (int i = 0; i < fgIndices.size; i++) foregroundLayers[i] = fgIndices.get(i);

        mapBlocks = mapHelper.getMapBlocks();
        Vector2 spawn = mapHelper.getRespawnPoint();

        knight     = new Knight(spawn.x, spawn.y);
        controller = new KnightController(knight);

        activeEnemies = new Array<>();
        for (TiledMapHelper.EnemySpawn s : mapHelper.getEnemySpawns()) {
            activeEnemies.add(createEnemy(s));
        }
        bossAnimManager = new FalseKnightAnimationManager();
        activeEnemies = new Array<>();

        for (TiledMapHelper.EnemySpawn s : mapHelper.getEnemySpawns()) {
            if (s.type.equalsIgnoreCase("zote")) {
                zote = new Zote(s.x, s.y);
            }
            else if (s.type.equalsIgnoreCase("falseknight")) {
                boss = new FalseKnight(s.x, s.y + 10f);
            } else {
                activeEnemies.add(createEnemy(s));
            }
        }
        com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator gen =
            new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator(Gdx.files.internal("ui/Fonts/trajan.ttf"));
        com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter param =
            new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20; param.color = Color.WHITE;
        hintFont = gen.generateFont(param);
        gen.dispose();

    }

    private Enemy createEnemy(TiledMapHelper.EnemySpawn spawn) {
        switch (spawn.type.toLowerCase()) {
            case "husk":
            case "huskhornhead":
            case "husk_hornhead":
                return new HuskHornhead(spawn.x, spawn.y);
            case "mossfly":
            case "Mossfly":
                return new Mossfly(spawn.x, spawn.y);
            case "crystal":
            case "crystalguardian":
            case "crystal_guardian":
                return new CrystalGuardian(spawn.x, spawn.y);
            case "crawlid":
            default:
                return new Crawlid(spawn.x, spawn.y);
        }
    }

    @Override
    public void render(float delta) {
        boolean isPopupOpen = popupOverlay.isVisible();


        if (!isPopupOpen) {
            // شوالیه
            controller.handleInput(activeEnemies, boss, zote);
            knight.update(delta, mapBlocks, activeEnemies, boss, zote);
            if (knight.isDead && knight.deathTimer <= 0) {
                popupOverlay.showDeathMenu();
            }
            // انمی ها
            for (Enemy enemy : activeEnemies) {
                enemy.update(delta, mapBlocks, knight);
            }

            //  باس‌
            if (boss != null) {
                boss.update(delta, mapBlocks, knight);
            }

            // زوت
            if (zote != null) {
                zote.update(delta, mapBlocks, knight);

                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.E) && zote.isPlayerInRange(knight)) {
                    zote.interact(knight);
                    if (zote.isInteracting) {
                        popupOverlay.showDialogue(zote.displayedText);
                    }
                }
            }

            // اینحت مرده هارو حذف میکنیم
            // ولی برای امتیازی که جنازه هارو نگه داره باید بعدا این و اصلاح کنم
            // که مثلا یه ارایه مرده ها بسازم
            // که انیمیشن اخر مرده بودن باشه و روی مپ بمونه ، ولی از ارایه انمی های زنده هم جدا باشه
            for (int i = activeEnemies.size - 1; i >= 0; i--) {
                Enemy enemy = activeEnemies.get(i);
                if (enemy.isReadyForRemoval()) {
                    if (enemy instanceof Crawlid) crawlidAnimManager.forget((Crawlid) enemy);
                    else if (enemy instanceof HuskHornhead) huskAnimManager.forget((HuskHornhead) enemy);
                    else if (enemy instanceof CrystalGuardian) crystalAnimManager.forget((CrystalGuardian) enemy);
                    else if (enemy instanceof Mossfly) mossflyAnimManager.forget((Mossfly) enemy);
                    activeEnemies.removeIndex(i);
                }
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                popupOverlay.showPauseMenu();
            }

            stateTime += delta;
        }

        else {
            if (popupOverlay.getType() == PopupOverlay.PopupType.DIALOGUE) {
                if (zote != null) {
                    zote.update(delta, mapBlocks, knight);
                    popupOverlay.updateDialogueText(zote.displayedText);

                    if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.E)) {
                        zote.interact(knight);
                        if (!zote.isInteracting) popupOverlay.hide();
                    }
                }
            }
            else if (popupOverlay.getType() == PopupOverlay.PopupType.DEATH) {
                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
                    popupOverlay.hide();
                    forceRespawnKnight();
                }
            }
            else if (popupOverlay.getType() == PopupOverlay.PopupType.PAUSE) {
                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                    popupOverlay.hide();
                }
            }
        }

        camera.position.set(knight.position.x, knight.position.y + 100, 0);
        camera.update();

        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1);
        mapRenderer.setView(camera);
        mapRenderer.render(backgroundLayers);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        effectManager.update(knight, delta);
        effectManager.render(batch);
        effectManager.renderSpells(batch, knight);

        // کشیدن زوت
        if (zote != null) {
            TextureRegion zoteFrame = zoteAnimManager.getFrame(zote, stateTime);
            batch.draw(zoteFrame, zote.position.x, zote.position.y);
            if (zote.isPlayerInRange(knight) && !isPopupOpen && !zote.isAngry && zote.getStation() != com.HollowKnight.model.enums.ZoteStation.FALL) {
                hintFont.draw(batch, Translator.getText("PRESS_E"), zote.position.x, zote.position.y + 80);
            }
        }
        // کشیدن باس
        if (boss != null && !(boss.isDead && boss.deathTimer <= 0)) {
            TextureRegion bossFrame = bossAnimManager.getFrame(boss, stateTime);
            batch.draw(bossFrame, boss.position.x, boss.position.y);
        }


        // کشیدن انمی ها
        for (Enemy enemy : activeEnemies) {
            TextureRegion frame = null;
            if (enemy instanceof Crawlid) {
                frame = crawlidAnimManager.getFrame((Crawlid) enemy, stateTime);
            } else if (enemy instanceof HuskHornhead) {
                frame = huskAnimManager.getFrame((HuskHornhead) enemy, stateTime);
            } else if (enemy instanceof Mossfly) {
                frame = mossflyAnimManager.getFrame((Mossfly) enemy, stateTime);
            } else if (enemy instanceof CrystalGuardian) {
                CrystalGuardian guardian = (CrystalGuardian) enemy;
                frame = crystalAnimManager.getFrame(guardian, stateTime);

                if (guardian.getStation() == com.HollowKnight.model.enums.CrystalGuardianStation.SHOOT) {
                    TextureRegion laserFrame = crystalAnimManager.getLaserFrame(guardian, stateTime);
                    float laserWidth = 1600f;
                    float laserHeight = 50f;
                    float laserX = guardian.isFacingRight
                        ? guardian.position.x + guardian.hitbox.width + 5
                        : guardian.position.x - laserWidth + 80;
                    float laserY = 70f + guardian.position.y + (guardian.hitbox.height / 2f) - (laserHeight / 2f);
                    batch.draw(laserFrame, laserX, laserY, laserWidth, laserHeight);
                }
            }

            if (frame != null) {
                batch.draw(frame, enemy.position.x, enemy.position.y);
            }
        }

        // کشیدن نایت
        TextureRegion currentFrame = animManager.getFrame(knight, stateTime);
        if (currentFrame != null) {

            if (knight.isFlashing()) {
                batch.setColor(1f, 0.1f, 0.8f, 0.6f);            }
            else if (knight.isFocusing) {
                batch.setColor(1f, 1f, 0.8f, 0.8f);
            }

            batch.draw(currentFrame, knight.position.x - 20, knight.position.y - 10);

            batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        }

        batch.end();

        mapRenderer.render(foregroundLayers);

        // اخر از همه پاپ رو میاریم روشون اگه بودش
        popupOverlay.render(delta);
    }
    public void forceRespawnKnight() {
        knight.fullRespawn(mapHelper.getRespawnPoint());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        batch.dispose();
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();

        huskAnimManager.dispose();
        crystalAnimManager.dispose();
        mossflyAnimManager.dispose();
    }
}
