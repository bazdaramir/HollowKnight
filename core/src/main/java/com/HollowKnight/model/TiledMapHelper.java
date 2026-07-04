package com.HollowKnight.model;

import com.badlogic.gdx.maps.MapLayer;
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
        MapLayer layer = tiledMap.getLayers().get("object");

        if (layer != null) {
            for (MapObject object : layer.getObjects()) {
                if (object.getName() != null && object.getName().equalsIgnoreCase("respawn")) {
                    Float rx = object.getProperties().get("x", Float.class);
                    Float ry = object.getProperties().get("y", Float.class);
                    if (rx != null && ry != null) respawnPoint.set(rx, ry);
                    continue;
                }

                if (object.getName() != null && object.getName().equalsIgnoreCase("enemy")) {
                    continue;
                }

                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObj = (RectangleMapObject) object;
                    Rectangle rect = rectObj.getRectangle();

                    boolean isSolid = parseBooleanProperty(object, "isSolid");
                    boolean isDeadly = parseBooleanProperty(object, "isDeadly");
                    blocks.add(new Block(rect.x, rect.y, rect.width, rect.height, isSolid, isDeadly));
                }
            }
        }
        return blocks;
    }

    public Array<EnemySpawn> getEnemySpawns() {
        Array<EnemySpawn> spawns = new Array<>();
        MapLayer layer = tiledMap.getLayers().get("object");
        if (layer == null) return spawns;

        for (MapObject object : layer.getObjects()) {
            if (object.getName() == null || !object.getName().equalsIgnoreCase("enemy")) continue;

            Float x = object.getProperties().get("x", Float.class);
            Float y = object.getProperties().get("y", Float.class);

            if (x == null || y == null) {
                System.err.println("⚠ Enemy spawn skipped: Missing x or y coordinate.");
                continue;
            }

            Object typeProp = object.getProperties().get("type");
            String type = (typeProp != null) ? typeProp.toString().toLowerCase().trim() : "crawlid";

            spawns.add(new EnemySpawn(x, y, type));
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


    public static class EnemySpawn {
        public final float x, y;
        public final String type;
        public EnemySpawn(float x, float y, String type) {
            this.x = x; this.y = y; this.type = type;
        }
    }
}
