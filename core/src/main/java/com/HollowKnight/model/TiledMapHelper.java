package com.HollowKnight.model;

import com.HollowKnight.model.enums.Map;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TiledMapHelper {
    private TiledMap tiledMap;
    private Vector2 respawnPoint = new Vector2(100, 100);

    /**
     * tiledMap.getLayers() فقط لایه‌های سطح بالا رو برمی‌گردونه.
     * اگه توی Tiled یه object (مثل enemy با type=zote) داخل یک Group Layer
     * (لایه‌ی تو در تو / پوشه‌ای از لایه‌ها) قرار گرفته باشه، اون گروه به عنوان
     * یک لایه‌ی معمولی توی getObjects() هیچ آبجکتی برنمی‌گردونه و آبجکت‌های
     * داخلش نادیده گرفته می‌شن؛ همین باعث می‌شه دشمنی مثل زوت اصلاً اسپاون نشه.
     * این متد همه‌ی لایه‌ها رو -با رفتن داخل گروه‌های تو در تو- به صورت مسطح برمی‌گردونه.
     */
    private Array<MapLayer> getAllLayersFlattened() {
        Array<MapLayer> result = new Array<>();
        collectLayers(tiledMap.getLayers(), result);
        return result;
    }

    private void collectLayers(MapLayers layers, Array<MapLayer> result) {
        for (MapLayer layer : layers) {
            result.add(layer);
            // getLayers() فقط روی MapGroupLayer وجود داره، نه روی MapLayer پایه
            if (layer instanceof MapGroupLayer) {
                MapLayers children = ((MapGroupLayer) layer).getLayers();
                if (children != null && children.getCount() > 0) {
                    collectLayers(children, result);
                }
            }
        }
    }

    public TiledMap loadMap(String path) {
        try {
            tiledMap = new TmxMapLoader().load(path);
            return tiledMap;
        } catch (Exception e) {
            System.err.println("❌ Error loading map: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Array<Block> getMapBlocks() {
        Array<Block> blocks = new Array<>();

        for (MapLayer layer : getAllLayersFlattened()) {
            for (MapObject object : layer.getObjects()) {
                if (object.getName() != null && object.getName().equalsIgnoreCase("respawn")) {
                    Float rx = parseSafeFloat(object.getProperties().get("x"));
                    Float ry = parseSafeFloat(object.getProperties().get("y"));
                    if (rx != null && ry != null) respawnPoint.set(rx, ry);
                    continue;
                }

                if (object.getName() != null && (object.getName().equalsIgnoreCase("enemy") || object.getName().toLowerCase().contains("teleport") || object.getName().toLowerCase().contains("portal"))) {
                    continue;
                }

                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObj = (RectangleMapObject) object;
                    Rectangle rect = rectObj.getRectangle();

                    boolean isSolid = parseBooleanProperty(object, "isSolid");
                    boolean isDeadly = parseBooleanProperty(object, "isDeadly");
                    boolean isBreakable = parseBooleanProperty(object, "isBreakable");
                    Object hpProp = object.getProperties().get("health");

                    int health = 3;
                    if (hpProp != null) {
                        health = (int) Float.parseFloat(hpProp.toString());
                    }
                    int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
                    int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
                    int cellX = (int) (rect.x / tileWidth);
                    int cellY = (int) (rect.y / tileHeight);

                    blocks.add(new Block(rect.x, rect.y, rect.width, rect.height, isSolid, isDeadly, isBreakable, health, cellX, cellY));
                }
            }
        }
        return blocks;
    }

    public Array<EnemySpawn> getEnemySpawns() {
        Array<EnemySpawn> spawns = new Array<>();

        for (MapLayer layer : getAllLayersFlattened()) {
            for (MapObject object : layer.getObjects()) {
                if (object.getName() == null || !object.getName().equalsIgnoreCase("enemy")) continue;

                Float x = parseSafeFloat(object.getProperties().get("x"));
                Float y = parseSafeFloat(object.getProperties().get("y"));

                if (x == null || y == null) {
                    continue;
                }

                Object typeProp = object.getProperties().get("type");
                String type = (typeProp != null) ? typeProp.toString().toLowerCase().trim() : "crawlid";

                spawns.add(new EnemySpawn(x, y, type));
            }
        }
        return spawns;
    }

    private boolean parseBooleanProperty(MapObject object, String propertyName) {
        Object prop = object.getProperties().get(propertyName);
        if (prop == null) return false;
        return prop instanceof Boolean ? (Boolean) prop : Boolean.parseBoolean(prop.toString());
    }

    public Vector2 getRespawnPoint() {
        return respawnPoint;
    }


    public Vector2 getSpawnPoint(String name) {
        if (name == null || name.trim().isEmpty()) return getRespawnPoint();

        for (MapLayer layer : getAllLayersFlattened()) {
            MapObject obj = layer.getObjects().get(name);
            if (obj != null) {
                Float x = parseSafeFloat(obj.getProperties().get("x"));
                Float y = parseSafeFloat(obj.getProperties().get("y"));
                if (x != null && y != null) return new Vector2(x, y);
            }
        }
        return getRespawnPoint();
    }

    public Array<Portal> getPortals() {
        Array<Portal> portals = new Array<>();

        for (MapLayer layer : getAllLayersFlattened()) {
            for (MapObject object : layer.getObjects()) {
                String objName = object.getName() != null ? object.getName().toLowerCase() : "";
                String layName = layer.getName() != null ? layer.getName().toLowerCase() : "";
                String typeProp = getPropertyIgnoreCase(object, layer, "type");

                boolean isDoor = (typeProp != null && typeProp.equalsIgnoreCase("door"))
                    || objName.contains("teleport") || layName.contains("teleport") || objName.contains("portal");

                if (isDoor && object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();

                    String tMapStr = getPropertyIgnoreCase(object, layer, "targetmap", "target_map", "target", "map");


                    String spawnName = getPropertyIgnoreCase(object, layer, "spawnpoint", "spawn_point", "spawnname", "spawn_name", "spawn");

                    Float tX = getFloatPropertyIgnoreCase(object, layer, "targetx", "target_x");
                    Float tY = getFloatPropertyIgnoreCase(object, layer, "targety", "target_y");
                    if (tY != null) {
                        int mapHeightPx = tiledMap.getProperties().get("height", Integer.class)
                            * tiledMap.getProperties().get("tileheight", Integer.class);
                        tY = mapHeightPx - tY;
                    }

                    Map parsedMap = parseMapEnum(tMapStr);
                    portals.add(new Portal(rect, parsedMap, spawnName, tX, tY));
                }
            }
        }
        return portals;
    }

    private String getPropertyIgnoreCase(MapObject obj, MapLayer layer, String... keys) {
        for (String key : keys) {

            if (obj != null && obj.getProperties() != null) {
                java.util.Iterator<String> objKeys = obj.getProperties().getKeys();
                while (objKeys.hasNext()) {
                    String objKey = objKeys.next();
                    if (objKey.equalsIgnoreCase(key)) {
                        Object val = obj.getProperties().get(objKey);
                        if (val != null) return val.toString();
                    }
                }
            }

            if (layer != null && layer.getProperties() != null) {
                java.util.Iterator<String> layKeys = layer.getProperties().getKeys();
                while (layKeys.hasNext()) {
                    String layKey = layKeys.next();
                    if (layKey.equalsIgnoreCase(key)) {
                        Object val = layer.getProperties().get(layKey);
                        if (val != null) return val.toString();
                    }
                }
            }
        }
        return null;
    }

    private Float getFloatPropertyIgnoreCase(MapObject obj, MapLayer layer, String... keys) {
        String val = getPropertyIgnoreCase(obj, layer, keys);
        return parseSafeFloat(val);
    }

    private Float parseSafeFloat(Object val) {
        if (val == null) return null;
        if (val instanceof Float) return (Float) val;
        if (val instanceof Integer) return ((Integer) val).floatValue();
        try {
            return Float.parseFloat(val.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static Map parseMapEnum(String val) {
        if (val == null || val.trim().isEmpty()) return null;
        String norm = val.trim().toUpperCase().replace(" ", "_");
        if (norm.contains("GREEN")) return Map.GREEN_PATH;
        if (norm.contains("CROSSROAD") || norm.contains("FORGOTTEN")) return Map.FORGOTTEN_CROSSROADS;
        if (norm.contains("BOSS")) return Map.BOSS_ROOM;
        try {
            return Map.valueOf(norm);
        } catch (Exception e) {
            return null;
        }
    }

    public static class Portal {
        public final Rectangle rect;
        public final Map targetMap;
        public final String spawnPointName;
        public final Float targetX;
        public final Float targetY;

        public Portal(Rectangle rect, Map targetMap, String spawnPointName, Float targetX, Float targetY) {
            this.rect = rect;
            this.targetMap = targetMap;
            this.spawnPointName = spawnPointName;
            this.targetX = targetX;
            this.targetY = targetY;
        }
    }

    public static class EnemySpawn {
        public final float x, y;
        public final String type;
        public EnemySpawn(float x, float y, String type) {
            this.x = x; this.y = y; this.type = type;
        }
    }
}
