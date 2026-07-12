package com.HollowKnight.view;

import com.HollowKnight.controller.KnightController;
import com.HollowKnight.controller.PopupController;
import com.HollowKnight.model.*;
import com.HollowKnight.model.animations.*;
import com.HollowKnight.model.enums.Map;
import com.HollowKnight.model.enums.PopupType;
import com.HollowKnight.model.manager.*;
import com.HollowKnight.data.GameData;
import com.HollowKnight.data.SaveGameManager;
import com.HollowKnight.data.EnemySaveData;
import com.HollowKnight.data.BossSaveData;
import com.HollowKnight.model.mob.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
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
    private final App app;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private CameraShake cameraShake;
    private KnightAnimationManager animManager;
    private CrawlidAnimationManager crawlidAnimManager;
    private HuskHornheadAnimationManager huskAnimManager;
    private CrystalGuardianAnimationManager crystalAnimManager;
    private MossflyAnimationManager mossflyAnimManager;
    private OrthographicCamera uiCamera;

    private Array<AmbientMob> ambientMobs;
    private AmbientAnimationManager ambientAnimManager;
    private Texture maskFullTex, maskEmptyTex;
    private Texture orbEyeTex;

    private Array<Texture> maskFillFrames;
    private Array<Texture> maskBreakFrames;
    private SoulOrbAnimationManager soulOrbAnimManager;

    private int lastHealth = -1;
    private float[] maskBreakTimers = new float[20];
    private static final float MASK_BREAK_DURATION = 0.4f;

    int totalKills;
    private float mapPixelWidth;
    private float mapPixelHeight;

    private AudioManager audioManager;
    public float gameTimer = 0f;
    private static final float SOUL_ORB_WIDTH  = 130f;
    private static final float SOUL_ORB_HEIGHT = 130f;
    private static final float HUD_MARGIN_LEFT = 20f;
    private static final float HUD_MARGIN_TOP  = 20f;

    private static final float MASK_WIDTH      = 55f;
    private static final float MASK_HEIGHT     = 75f;
    private static final float MASK_PADDING    = 8f;
    private static final float MASK_OFFSET_X   = 150f;
    private static final float MASK_OFFSET_Y   = 40f;

    private boolean popupTRUE_HUNTER ;
    private boolean popupCHARMED ;
    private boolean popupSOUL_MASTER ;
    private boolean popupDEFEAT_BOSS ;
    private boolean popupZOTE ;
    private boolean popupCOMPLETION ;
    private boolean popupSPEEDRUN ;

    private static final float SPEEDRUN_TIME_LIMIT = 180f;

    private static final float ENEMY_RESPAWN_DISTANCE = 900f;
    private Map MAP_TYPE;
    private String mapAddres;
    private PopupOverlay popupOverlay;
    private PopupController popupController;
    private BitmapFont hintFont;
    private KnightEffectManager effectManager;
    private float stateTime = 0;
    private Knight knight;
    private KnightController controller;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMapHelper mapHelper;

    private Array<Block> mapBlocks;
    private Array<TiledMapHelper.Portal> portals;

    private boolean isPortalActive = false;
    private String pendingSpawnName = null;
    private Float pendingTeleportX = null;
    private Float pendingTeleportY = null;

    private FalseKnight boss;
    private FalseKnightAnimationManager bossAnimManager;
    private final AchievementManager achievementManager = new AchievementManager();
    private boolean bossWasAlive = true;
    private Array<Enemy> activeEnemies;
    private Array<Enemy> deadEnemies;
    private Array<EnemySlot> enemySlots;


    private static class EnemySlot {
        final float spawnX, spawnY;
        final String type;
        Enemy enemy;

        EnemySlot(float spawnX, float spawnY, String type, Enemy enemy) {
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.type = type;
            this.enemy = enemy;
        }
    }
    private Zote zote;
    private ZoteAnimationManager zoteAnimManager;

    private int[] backgroundLayers;
    private int[] foregroundLayers;
    private boolean initialized = false;
    private int saveSlot = -1;
    private GameData pendingLoad;

    private void drawAnchoredToHitbox(TextureRegion frame, Rectangle hitbox) {
        float frameW = frame.getRegionWidth();
        float drawX = hitbox.x + (hitbox.width - frameW) / 2f;
        float drawY = hitbox.y;
        batch.draw(frame, drawX, drawY);
    }

    public GameScreen(App app) {
        this.app = app;
    }

    public GameScreen(App app, int saveSlot) {
        this.app = app;
        this.saveSlot = saveSlot;
        this.pendingLoad = SaveGameManager.getInstance().loadGame(saveSlot);
        popupTRUE_HUNTER = false;
        popupCHARMED = false;
        popupSOUL_MASTER = false;
        popupDEFEAT_BOSS = false;
        popupZOTE = false;
        popupCOMPLETION = false;
        popupSPEEDRUN = false;
    }

    public GameScreen(App app, Knight existingKnight, Map targetMap, String spawnPointName) {
        this.app = app;
        this.MAP_TYPE = (targetMap != null) ? targetMap : Map.FORGOTTEN_CROSSROADS;
        this.knight = existingKnight;

        this.pendingSpawnName = spawnPointName;
        this.isPortalActive = false;

        if (this.knight != null) {
            this.knight.velocity.set(0, 0);
        }
    }

    public GameScreen(App app, Knight existingKnight, Map targetMap, Float targetX, Float targetY) {
        this.app = app;
        this.MAP_TYPE = (targetMap != null) ? targetMap : Map.FORGOTTEN_CROSSROADS;
        this.knight = existingKnight;

        this.pendingTeleportX = targetX;
        this.pendingTeleportY = targetY;
        this.isPortalActive = false;

        if (this.knight != null) {
            this.knight.velocity.set(0, 0);
        }
    }

    public int getSaveSlot() {
        return saveSlot;
    }

    @Override
    public void show() {
        if (initialized) {
            popupOverlay.showPauseMenu();
            return;
        }
        initialized = true;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1920*0.7f, 1080*0.7f, camera);
        batch = new SpriteBatch();
        audioManager = AudioManager.getInstance();
        animManager        = new KnightAnimationManager();
        crawlidAnimManager = new CrawlidAnimationManager();
        huskAnimManager    = new HuskHornheadAnimationManager();
        crystalAnimManager = new CrystalGuardianAnimationManager();
        mossflyAnimManager = new MossflyAnimationManager();
        effectManager      = new KnightEffectManager();
        zoteAnimManager    = new ZoteAnimationManager();
        popupOverlay = new PopupOverlay(this, app);
        popupController = new PopupController(popupOverlay, this);
        mapHelper = new TiledMapHelper();
        cameraShake = new CameraShake();

        if (MAP_TYPE == null) MAP_TYPE = Map.FORGOTTEN_CROSSROADS;

        mapAddres = String.format("maps/%s.tmx", MAP_TYPE.name());
        map = mapHelper.loadMap(mapAddres);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
        mapPixelWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        mapPixelHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);

        AudioManager.getInstance().mapSoundHandler(MAP_TYPE.toString().toLowerCase().replace("_", ""));

        maskFullTex  = new Texture(Gdx.files.internal("ui/HUD/FilledHealth.png"));
        maskEmptyTex = new Texture(Gdx.files.internal("ui/HUD/EmptyHealth.png"));
        orbEyeTex = new Texture(Gdx.files.internal("ui/HUD/SoulOrb_Eye.png"));
        ambientMobs = new Array<>();
        ambientAnimManager = new AmbientAnimationManager();

        maskFillFrames = new Array<>();
        for (int i = 0; i <= 4; i++) {
            maskFillFrames.add(new Texture(Gdx.files.internal("ui/HUD/HealthRefill_00" + i + ".png")));
        }

        maskBreakFrames = new Array<>();
        for (int i = 0; i <= 5; i++) {
            maskBreakFrames.add(new Texture(Gdx.files.internal("ui/HUD/BreakHealth_00" + i + ".png")));
        }

        soulOrbAnimManager = new SoulOrbAnimationManager();

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());
        Array<Integer> bgIndices = new Array<>();
        Array<Integer> fgIndices = new Array<>();
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);
            boolean isRenderableLayer = (layer instanceof TiledMapTileLayer)
                || (layer instanceof com.badlogic.gdx.maps.tiled.TiledMapImageLayer);

            if (isRenderableLayer) {
                String layerName = layer.getName().toLowerCase();
                if (layerName.startsWith("forground") || layerName.startsWith("foreground")) {
                    fgIndices.add(i);
                } else if (layerName.equals("base") || layerName.startsWith("background")
                    || layerName.startsWith("bakground") || layerName.equals("image")||layerName.startsWith("breakable_layer")) {
                    bgIndices.add(i);
                }
            }
        }
        backgroundLayers = new int[bgIndices.size];
        for (int i = 0; i < bgIndices.size; i++) backgroundLayers[i] = bgIndices.get(i);

        foregroundLayers = new int[fgIndices.size];
        for (int i = 0; i < fgIndices.size; i++) foregroundLayers[i] = fgIndices.get(i);

        mapBlocks = mapHelper.getMapBlocks();
        portals = mapHelper.getPortals();

        System.out.println("✅ Map Fully Loaded: " + MAP_TYPE.name() + " | Portals count: " + portals.size);

        if (this.knight == null) {
            Vector2 spawn = mapHelper.getRespawnPoint();
            this.knight = new Knight(spawn.x, spawn.y);
            this.knight.setCharmManager(popupOverlay.getCharmManager());
        } else {
            Vector2 finalPos;
            if (pendingSpawnName != null) {
                finalPos = mapHelper.getSpawnPoint(pendingSpawnName);
            } else if (pendingTeleportX != null && pendingTeleportY != null) {
                finalPos = new Vector2(pendingTeleportX, pendingTeleportY);
            } else {
                finalPos = mapHelper.getRespawnPoint();
            }

            this.knight.position.set(finalPos.x, finalPos.y);
            this.knight.hitbox.setPosition(finalPos.x, finalPos.y);
            this.knight.lastSafePosition.set(finalPos.x, finalPos.y);
        }

        camera.position.set(this.knight.position.x, this.knight.position.y + 100f, 0);
        camera.update();

        if (MAP_TYPE == Map.GREEN_PATH) {
            for(int i=0; i<3; i++) ambientMobs.add(new AmbientMob(i*300 - 600, com.badlogic.gdx.math.MathUtils.random(-150f, 150f), 100f, "bee"));
        }
        if (MAP_TYPE == Map.FORGOTTEN_CROSSROADS) {
            for(int i=0; i<2; i++) ambientMobs.add(new AmbientMob(i*500 - 500, com.badlogic.gdx.math.MathUtils.random(-150f, 150f), 150f, "firefly"));
        }

        controller = new KnightController(this.knight);

        activeEnemies = new Array<>();
        deadEnemies = new Array<>();
        enemySlots = new Array<>();
        bossAnimManager = new FalseKnightAnimationManager();
        totalKills = deadEnemies.size;

        for (TiledMapHelper.EnemySpawn s : mapHelper.getEnemySpawns()) {
            if (s.type.equalsIgnoreCase("zote")) {
                zote = new Zote(s.x, s.y+4000f);
            }
            else if (s.type.equalsIgnoreCase("falseknight")) {
                boss = new FalseKnight(2108f, 1976f);
                boss.velocity.set(0, 0);
                boss.health = 30;
                boss.isDead = false;
                bossWasAlive = true;
            }
            else {
                Enemy newEnemy = createEnemy(s);
                activeEnemies.add(newEnemy);
                enemySlots.add(new EnemySlot(s.x, s.y, s.type, newEnemy));
            }
        }

        com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator gen =
            new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator(Gdx.files.internal("ui/Fonts/trajan.ttf"));
        com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter param =
            new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20; param.color = Color.WHITE;
        hintFont = gen.generateFont(param);
        gen.dispose();

        if (pendingLoad != null) {
            applyGameData(pendingLoad);
            pendingLoad = null;
        }
    }

    public GameData captureGameData() {
        GameData data = new GameData();

        data.health = knight.health;
        data.maxHealth = knight.maxHealth;
        data.soul = knight.soul;
        data.maxSoul = knight.maxSoul;
        data.x = knight.position.x;
        data.y = knight.position.y;
        data.gameTimer = this.gameTimer;

        StringBuilder charmsBuilder = new StringBuilder();
        if (popupOverlay != null && popupOverlay.getCharmManager() != null) {
            for (Charm c : popupOverlay.getCharmManager().getCharms()) {
                if (c.isEquipped()) {
                    if (charmsBuilder.length() > 0) charmsBuilder.append(",");
                    charmsBuilder.append(c.getName());
                }
            }
        }
        data.equippedCharms = charmsBuilder.toString();

        for (Enemy e : activeEnemies) {
            EnemySaveData ed = new EnemySaveData();
            ed.x = e.position.x;
            ed.y = e.position.y;
            if (e instanceof Crawlid) ed.type = "Crawlid";
            else if (e instanceof HuskHornhead) ed.type = "HuskHornhead";
            else if (e instanceof Mossfly) ed.type = "Mossfly";
            else if (e instanceof CrystalGuardian) ed.type = "CrystalGuardian";
            else ed.type = "Crawlid";

            data.enemies.add(ed);
        }

        if (boss != null) {
            BossSaveData bd = new BossSaveData();
            bd.x = boss.position.x;
            bd.y = boss.position.y;
            bd.health = boss.health;
            bd.isDead = boss.isDead;
            data.boss = bd;
        }

        return data;
    }

    private void applyGameData(GameData data) {
        if (data == null) return;
        knight.loadFromSave(data);
        gameTimer = data.gameTimer;
        restoreEquippedCharms(data.equippedCharms);
        restoreEnemies(data.enemies);
        restoreBoss(data.boss);
    }

    private void restoreEquippedCharms(String equippedCharmsCsv) {
        if (equippedCharmsCsv == null || equippedCharmsCsv.isEmpty()) return;
        java.util.Set<String> shouldBeEquipped = new java.util.HashSet<>(
            java.util.Arrays.asList(equippedCharmsCsv.split(",")));
        CharmManager cm = popupOverlay.getCharmManager();
        for (Charm c : cm.getCharms()) {
            boolean wantsEquip = shouldBeEquipped.contains(c.getName());
            if (c.isEquipped() != wantsEquip) {
                cm.toggleCharm(c);
            }
        }
    }

    private void restoreEnemies(java.util.List<EnemySaveData> savedEnemies) {
        activeEnemies.clear();
        if (savedEnemies == null) return;

        for (EnemySaveData ed : savedEnemies) {
            Enemy e = recreateEnemy(ed);
            activeEnemies.add(e);
        }
    }

    private Enemy recreateEnemy(EnemySaveData ed) {
        Enemy e;
        switch (ed.type) {
            case "HuskHornhead":
                e = new HuskHornhead(ed.x, ed.y);
                break;
            case "Mossfly":
                e = new Mossfly(ed.x, ed.y);
                break;
            case "CrystalGuardian":
                e = new CrystalGuardian(ed.x, ed.y);
                break;
            case "Crawlid":
            default:
                e = new Crawlid(ed.x, ed.y);
                break;
        }
        e.setHealth(ed.health);
        return e;
    }

    private void restoreBoss(BossSaveData bd) {
        if (bd == null || boss == null) {
            return;
        }
        boss.position.set(bd.x, bd.y);
        boss.hitbox.setPosition(bd.x, bd.y);
        boss.health = bd.health;

        if (boss.health <= 0) {
            boss.isDead = true;
        }
    }

    @Override
    public void render(float delta) {
        boolean isPopupOpen = popupOverlay.isVisible();

        popupController.handleInput(knight, zote);

        if (!isPopupOpen) {
            gameTimer += delta;
            controller.handleInput(activeEnemies, boss, zote, mapBlocks, popupOverlay);

            if (controller.requestBossTeleport) {
                controller.requestBossTeleport = false;
                app.setScreen(new GameScreen(app, knight, Map.BOSS_ROOM, "boss_spawn_point"));
                return;
            }

            knight.update(delta, mapBlocks, activeEnemies, boss, zote, MAP_TYPE);
            for (int i = mapBlocks.size - 1; i >= 0; i--) {
                Block block = mapBlocks.get(i);
                if (block.isBreakable && block.health <= 0) {
                    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("breakable_layer");
                    if (layer != null) layer.setCell(block.cellX, block.cellY, null);
                    mapBlocks.removeIndex(i);
                }
            }


            boolean currentlyOverlapping = false;
            TiledMapHelper.Portal activePortal = null;

            if (portals != null) {
                for (TiledMapHelper.Portal portal : portals) {
                    if (knight.hitbox.overlaps(portal.rect)) {
                        currentlyOverlapping = true;
                        activePortal = portal;
                        break;
                    }
                }
            }

            if (!currentlyOverlapping) {
                isPortalActive = true;
            }

            if (currentlyOverlapping && isPortalActive && activePortal != null && activePortal.targetMap != null) {
                if (activePortal.targetMap != MAP_TYPE) {
                    System.out.println("🚀 [TELEPORT] Switching map from " + MAP_TYPE.name() + " to " + activePortal.targetMap.name()
                        + " | spawn=" + activePortal.spawnPointName);
                    if (activePortal.spawnPointName != null) {
                        app.setScreen(new GameScreen(app, knight, activePortal.targetMap, activePortal.spawnPointName));
                    } else {
                        app.setScreen(new GameScreen(app, knight, activePortal.targetMap, activePortal.targetX, activePortal.targetY));
                    }
                    return;
                } else {
                    System.out.println("🔄 [TELEPORT] Local teleport inside: " + MAP_TYPE.name());
                    Vector2 localSpawn = (activePortal.spawnPointName != null)
                        ? mapHelper.getSpawnPoint(activePortal.spawnPointName)
                        : null;
                    if (localSpawn != null) {
                        knight.position.set(localSpawn.x, localSpawn.y);
                    } else {
                        if (activePortal.targetX != null) knight.position.x = activePortal.targetX;
                        if (activePortal.targetY != null) knight.position.y = activePortal.targetY;
                    }
                    knight.hitbox.setPosition(knight.position.x, knight.position.y);
                    camera.position.set(knight.position.x, knight.position.y + 100f, 0);
                    camera.update();
                    isPortalActive = false;
                }
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1)) {
                app.setScreen(new GameScreen(app, knight, Map.FORGOTTEN_CROSSROADS, null, null)); return;
            }
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.T)) {
                app.setScreen(new GameScreen(app, knight, Map.GREEN_PATH, null, null)); return;
            }
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3)) {
                app.setScreen(new GameScreen(app, knight, Map.BOSS_ROOM, null, null)); return;
            }
            if (knight.isDead && knight.deathTimer <= 0) {
                if (boss != null && boss.isDead) totalKills++;
                popupOverlay.showDeathMenu(gameTimer,totalKills,false);
            }

            if ((knight.soul >= knight.maxSoul)&& !popupSOUL_MASTER) {
                achievementManager.unlockSoulMaster();
                popupOverlay.showAchiementUnlocked("Soul Master");
                popupSOUL_MASTER = true;
            }

            for (Enemy enemy : activeEnemies) enemy.update(delta, mapBlocks, knight);

            if (boss != null) {
                boss.update(delta, mapBlocks, knight);

                if (MAP_TYPE == Map.BOSS_ROOM && boss.position.y < 1976f) {
                    boss.position.y = 1976f;
                    boss.hitbox.y = 1976f;
                    if (boss.velocity != null) boss.velocity.y = 0;
                }

                if ((bossWasAlive && boss.isDead)&&!popupDEFEAT_BOSS) {
                    achievementManager.unlockDefeatBoss();
                    popupOverlay.showAchiementUnlocked("Defeat Boss");
                    popupDEFEAT_BOSS = true;
                }
                bossWasAlive = !boss.isDead;

                if (boss.isDead && !popupCOMPLETION) {
                    achievementManager.unlockCompletion();
                    popupOverlay.showAchiementUnlocked("Completion");
                    popupCOMPLETION = true;
                }

                if (boss.isDead && !popupSPEEDRUN && gameTimer <= SPEEDRUN_TIME_LIMIT) {
                    achievementManager.unlockSpeedrun();
                    popupOverlay.showAchiementUnlocked("Speedrun");
                    popupSPEEDRUN = true;
                }

                if (boss.isDead && boss.deathTimer <= 0) {
                    System.out.println("🎉 GAME OVER - YOU WIN! 🎉");
                    AudioManager.getInstance().mapSoundHandler("none");
                    AudioManager.getInstance().playBossVictoryMusic();

                    popupOverlay.showDeathMenu(gameTimer,totalKills,true);
                }
            }

            if (zote != null) {
                zote.update(delta, mapBlocks, knight);

                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.E) && zote.isPlayerInRange(knight)) {
                    zote.interact(knight);
                    if (zote.isInteracting) {
                        popupOverlay.showDialogue(zote.displayedText);
                        achievementManager.unlockZote();
                        if (!popupZOTE) {
                            popupOverlay.showAchiementUnlocked("Zote");
                            popupZOTE = true;
                        }
                    }
                }
            }

            for (int i = activeEnemies.size - 1; i >= 0; i--) {
                Enemy enemy = activeEnemies.get(i);
                if (enemy.isReadyForRemoval()) {
                    deadEnemies.add(enemy);
                    activeEnemies.removeIndex(i);

                    for (EnemySlot slot : enemySlots) {
                        if (slot.enemy == enemy) {
                            slot.enemy = null;
                            break;
                        }
                    }
                }
            }

            // دوباره زنده شن
            for (EnemySlot slot : enemySlots) {
                if (slot.enemy == null) {
                    float dx = knight.position.x - slot.spawnX;
                    float dy = knight.position.y - slot.spawnY;
                    float distanceSquared = dx * dx + dy * dy;

                    if (distanceSquared >= ENEMY_RESPAWN_DISTANCE * ENEMY_RESPAWN_DISTANCE) {
                        Enemy respawnedEnemy = createEnemy(
                            new TiledMapHelper.EnemySpawn(slot.spawnX, slot.spawnY, slot.type)
                        );
                        activeEnemies.add(respawnedEnemy);
                        slot.enemy = respawnedEnemy;
                    }
                }
            }

            if ((activeEnemies.size == 0)&&!popupTRUE_HUNTER) {
                achievementManager.unlockTrueHunter();
                popupOverlay.showAchiementUnlocked("True Hunter");
                popupTRUE_HUNTER = true;
            }

            stateTime += delta;
        } else {
            if (popupOverlay.getType() == PopupType.DIALOGUE && zote != null) {
                zote.update(delta, mapBlocks, knight);
            }
        }

        if (!isPopupOpen) {
            if (knight.shakeDuration > 0) {
                cameraShake.shake(knight.shakeIntensity, knight.shakeDuration);
                knight.shakeDuration = 0f;
            }
            if (boss != null && boss.shakeDuration > 0) {
                cameraShake.shake(boss.shakeIntensity, boss.shakeDuration);
                boss.shakeDuration = 0f;
            }
        }

        float idealX = knight.position.x;
        float idealY = knight.position.y + 100f;
        float targetZoom = 1.0f;

        if (MAP_TYPE == Map.BOSS_ROOM) {
            targetZoom = 1.70f;
            idealX = mapPixelWidth / 2f-80;
            idealY = (mapPixelHeight / 2f) + 350f;
        }

        camera.zoom = com.badlogic.gdx.math.MathUtils.lerp(camera.zoom, targetZoom, 3f * delta);

        float lerpSpeed = 6f;
        float lerpedX = camera.position.x + (idealX - camera.position.x) * lerpSpeed * delta;
        float lerpedY = camera.position.y + (idealY - camera.position.y) * lerpSpeed * delta;

        float halfViewportWidth = (viewport.getWorldWidth() * camera.zoom) / 2f;
        float halfViewportHeight = (viewport.getWorldHeight() * camera.zoom) / 2f;

        float clampedX = com.badlogic.gdx.math.MathUtils.clamp(lerpedX, halfViewportWidth, mapPixelWidth - halfViewportWidth);
        float clampedY = com.badlogic.gdx.math.MathUtils.clamp(lerpedY, halfViewportHeight, mapPixelHeight - halfViewportHeight);

        cameraShake.update(delta, camera, clampedX, clampedY);

        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1f);

        mapRenderer.setView(camera);
        mapRenderer.render(backgroundLayers);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (AmbientMob mob : ambientMobs) {
            mob.attachToCamera(delta, clampedX, clampedY, halfViewportWidth, halfViewportHeight, 80f);
        }

        for (AmbientMob mob : ambientMobs) {
            TextureRegion frame = ambientAnimManager.getFrame(mob.type, mob.time);
            float scale = mob.type.equals("bee") ? 0.4f : 0.25f;
            float w = frame.getRegionWidth() * scale;
            float h = frame.getRegionHeight() * scale;
            if (mob.facingRight) {
                if (frame.isFlipX()) frame.flip(true, false);
                batch.draw(frame, mob.position.x, mob.position.y, w, h);
            } else {
                if (!frame.isFlipX()) frame.flip(true, false);
                batch.draw(frame, mob.position.x, mob.position.y, w, h);
                frame.flip(true, false);
            }
        }

        effectManager.update(knight, delta);
        effectManager.render(batch);
        effectManager.renderSpells(batch, knight);

        if (zote != null) {
            TextureRegion zoteFrame = zoteAnimManager.getFrame(zote, stateTime);
            drawAnchoredToHitbox(zoteFrame, zote.hitbox);
            if (zote.isPlayerInRange(knight) && !isPopupOpen && !zote.isAngry && zote.getStation() != com.HollowKnight.model.enums.ZoteStation.FALL) {
                hintFont.draw(batch, Translator.getText("PRESS_E"), zote.position.x, zote.position.y + 80);
            }
        }

        if (boss != null) {
            TextureRegion bossFrame = bossAnimManager.getFrame(boss, stateTime);
            drawAnchoredToHitbox(bossFrame, boss.hitbox);
        }

        batch.setColor(0.35f, 0.35f, 0.4f, 1f);
        for (Enemy deadEnemy : deadEnemies) {
            TextureRegion frame = null;
            if (deadEnemy instanceof Crawlid) {
                frame = crawlidAnimManager.getFrame((Crawlid) deadEnemy, stateTime);
            } else if (deadEnemy instanceof HuskHornhead) {
                frame = huskAnimManager.getFrame((HuskHornhead) deadEnemy, stateTime);
            } else if (deadEnemy instanceof Mossfly) {
                frame = mossflyAnimManager.getFrame((Mossfly) deadEnemy, stateTime);
            } else if (deadEnemy instanceof CrystalGuardian) {
                frame = crystalAnimManager.getFrame((CrystalGuardian) deadEnemy, stateTime);
            }

            if (frame != null) {
                drawAnchoredToHitbox(frame, deadEnemy.hitbox);
            }
        }
        batch.setColor(Color.WHITE);

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
                drawAnchoredToHitbox(frame, enemy.hitbox);
            }
        }

        TextureRegion currentFrame = animManager.getFrame(knight, stateTime);
        if (currentFrame != null) {
            if (knight.isFlashing()) {
                batch.setColor(1f, 1f, 0.7f, 0.6f);
            }
            else if (knight.isFocusing) {
                batch.setColor(1f, 1f, 0.7f, 0.8f);
            }

            drawAnchoredToHitbox(currentFrame, knight.hitbox);
            batch.setColor(Color.WHITE);
        }

        batch.end();
        mapRenderer.render(foregroundLayers);

        renderHud(delta);

        popupOverlay.render(delta);
    }

    private Enemy createEnemy(TiledMapHelper.EnemySpawn spawn) {
        switch (spawn.type.toLowerCase()) {
            case "husk":
            case "huskhornhead":
            case "husk_hornhead":
                return new HuskHornhead(spawn.x, spawn.y);
            case "mossfly":
                return new Mossfly(spawn.x, spawn.y);
            case "crystal":
            case "crystalguardian":
            case "crystal_guardian":
            case "crystallgaurdian":
            case "crystallgarudian":
                return new CrystalGuardian(spawn.x, spawn.y);
            case "crawlid":
            default:
                return new Crawlid(spawn.x, spawn.y);
        }
    }

    private void renderHud(float delta) {
        if (lastHealth == -1) lastHealth = knight.health;

        if (knight.health < lastHealth) {
            for (int i = knight.health; i < lastHealth; i++) {
                if (i >= 0 && i < maskBreakTimers.length) {
                    maskBreakTimers[i] = MASK_BREAK_DURATION;
                }
            }
        }
        lastHealth = knight.health;

        for (int i = 0; i < maskBreakTimers.length; i++) {
            if (maskBreakTimers[i] > 0) {
                maskBreakTimers[i] -= delta;
            }
        }

        float orbX = HUD_MARGIN_LEFT;
        float orbY = uiCamera.viewportHeight - HUD_MARGIN_TOP - SOUL_ORB_HEIGHT;

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (soulOrbAnimManager != null) {
            float soulPercent = (float) knight.soul / knight.maxSoul;
            Texture currentOrbFrame = soulOrbAnimManager.getFrameByPercentage(soulPercent);
            if (currentOrbFrame != null) {
                batch.draw(currentOrbFrame, orbX, orbY, SOUL_ORB_WIDTH, SOUL_ORB_HEIGHT);
            }
        }

        float startMaskX = orbX + MASK_OFFSET_X;
        float maskY = orbY + MASK_OFFSET_Y;

        float focusProgress = knight.getFocusProgress();

        for (int i = 0; i < knight.maxHealth; i++) {
            float currentX = startMaskX + i * (MASK_WIDTH + MASK_PADDING);

            if (maskEmptyTex != null) {
                batch.draw(maskEmptyTex, currentX, maskY, MASK_WIDTH*1.2f, MASK_HEIGHT*1.2f);
            }

            if (i < knight.health && maskFullTex != null) {
                batch.draw(maskFullTex, currentX, maskY, MASK_WIDTH*1.2f, MASK_HEIGHT*1.2f);
            }
            else if (maskBreakTimers[i] > 0 && maskBreakFrames != null && maskBreakFrames.size > 0) {
                float progress = 1f - (maskBreakTimers[i] / MASK_BREAK_DURATION);
                progress = com.badlogic.gdx.math.MathUtils.clamp(progress, 0f, 1f);

                int frameIndex = (int) (progress * (maskBreakFrames.size - 1));
                Texture currentMaskFrame = maskBreakFrames.get(frameIndex);

                batch.draw(currentMaskFrame, currentX, maskY, MASK_WIDTH*1.2f, MASK_HEIGHT*1.2f);
            }
            else if (i == knight.health && knight.isFocusing && maskFillFrames != null && maskFillFrames.size > 0) {
                int frameIndex = (int) (focusProgress * (maskFillFrames.size - 1));
                Texture currentMaskFrame = maskFillFrames.get(frameIndex);

                batch.draw(currentMaskFrame, currentX, maskY, MASK_WIDTH*1.2f, MASK_HEIGHT*1.2f);
            }
        }

        int minutes = (int) (gameTimer / 60);
        int seconds = (int) (gameTimer % 60);
        String timeString = String.format(java.util.Locale.US, "%02d : %02d", minutes, seconds);

        if (hintFont != null) {
            hintFont.setColor(Color.CYAN);
            hintFont.draw(batch, timeString, (uiCamera.viewportWidth / 2f), 40);
            hintFont.setColor(Color.WHITE);
        }

        batch.end();
    }

    public void forceRespawnKnight() {
        knight.fullRespawn(mapHelper.getRespawnPoint());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (popupOverlay != null) popupOverlay.resizeViewport(width, height);
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
        if (maskFullTex != null) maskFullTex.dispose();
        if (maskEmptyTex != null) maskEmptyTex.dispose();
        if (orbEyeTex != null) orbEyeTex.dispose();

        if (soulOrbAnimManager != null) soulOrbAnimManager.dispose();

        if (maskFillFrames != null) {
            for (Texture t : maskFillFrames) t.dispose();
        }
        if (maskBreakFrames != null) {
            for (Texture t : maskBreakFrames) t.dispose();
        }
    }
}
