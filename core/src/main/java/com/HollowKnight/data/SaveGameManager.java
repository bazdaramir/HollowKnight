package com.HollowKnight.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SaveGameManager {

    public static final int SLOT_COUNT = 4;
    private static final String LEGACY_SAVE_DIR = "data/saves/";

    private static SaveGameManager instance;

    private SaveGameManager() {
        if (Database.needsLegacyImport("save_slots")) {
            importLegacySaves();
        }
    }

    public static SaveGameManager getInstance() {
        if (instance == null) {
            instance = new SaveGameManager();
        }
        return instance;
    }

    private void validateSlot(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            throw new IllegalArgumentException(
                "Save slot must be between 0 and " + (SLOT_COUNT - 1) + ", got: " + slot);
        }
    }


    public void saveGame(GameData data, int slot) {
        validateSlot(slot);
        Connection connection = Database.get();
        try {
            try (PreparedStatement ps = connection.prepareStatement("INSERT OR REPLACE INTO save_slots "
                + "(slot, health, max_health, soul, max_soul, x, y, game_timer, equipped_charms, secret_state, "
                + "boss_x, boss_y, boss_health, boss_is_dead) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, slot);
                ps.setInt(2, data.health);
                ps.setInt(3, data.maxHealth);
                ps.setInt(4, data.soul);
                ps.setInt(5, data.maxSoul);
                ps.setFloat(6, data.x);
                ps.setFloat(7, data.y);
                ps.setFloat(8, data.gameTimer);
                ps.setString(9, data.equippedCharms);
                ps.setInt(10, data.secretState);
                if (data.boss == null) {
                    ps.setNull(11, java.sql.Types.REAL);
                    ps.setNull(12, java.sql.Types.REAL);
                    ps.setNull(13, java.sql.Types.INTEGER);
                    ps.setNull(14, java.sql.Types.INTEGER);
                } else {
                    ps.setFloat(11, data.boss.x);
                    ps.setFloat(12, data.boss.y);
                    ps.setInt(13, data.boss.health);
                    ps.setBoolean(14, data.boss.isDead);
                }
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM save_slot_enemies WHERE slot = ?")) {
                ps.setInt(1, slot);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO save_slot_enemies "
                + "(slot, position, type, x, y, health) VALUES (?, ?, ?, ?, ?, ?)")) {
                for (int i = 0; i < data.enemies.size(); i++) {
                    EnemySaveData enemy = data.enemies.get(i);
                    ps.setInt(1, slot);
                    ps.setInt(2, i);
                    ps.setString(3, enemy.type);
                    ps.setFloat(4, enemy.x);
                    ps.setFloat(5, enemy.y);
                    ps.setInt(6, enemy.health);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not save slot " + slot, e);
        }
    }

    public GameData loadGame(int slot) {
        validateSlot(slot);
        Connection connection = Database.get();
        try {
            GameData data;
            try (PreparedStatement ps = connection.prepareStatement("SELECT health, max_health, soul, max_soul, "
                + "x, y, game_timer, equipped_charms, secret_state, boss_x, boss_y, boss_health, boss_is_dead "
                + "FROM save_slots WHERE slot = ?")) {
                ps.setInt(1, slot);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    data = new GameData();
                    data.health = rs.getInt(1);
                    data.maxHealth = rs.getInt(2);
                    data.soul = rs.getInt(3);
                    data.maxSoul = rs.getInt(4);
                    data.x = rs.getFloat(5);
                    data.y = rs.getFloat(6);
                    data.gameTimer = rs.getFloat(7);
                    data.equippedCharms = rs.getString(8);
                    data.secretState = rs.getInt(9);
                    if (rs.getObject(10) != null) {
                        BossSaveData boss = new BossSaveData();
                        boss.x = rs.getFloat(10);
                        boss.y = rs.getFloat(11);
                        boss.health = rs.getInt(12);
                        boss.isDead = rs.getBoolean(13);
                        data.boss = boss;
                    }
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(
                "SELECT type, x, y, health FROM save_slot_enemies WHERE slot = ? ORDER BY position")) {
                ps.setInt(1, slot);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        EnemySaveData enemy = new EnemySaveData();
                        enemy.type = rs.getString(1);
                        enemy.x = rs.getFloat(2);
                        enemy.y = rs.getFloat(3);
                        enemy.health = rs.getInt(4);
                        data.enemies.add(enemy);
                    }
                }
            }
            return data;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load slot " + slot, e);
        }
    }


    public boolean hasSave(int slot) {
        validateSlot(slot);
        try (PreparedStatement ps = Database.get().prepareStatement("SELECT 1 FROM save_slots WHERE slot = ?")) {
            ps.setInt(1, slot);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not read slot " + slot, e);
        }
    }


    public void deleteSave(int slot) {
        validateSlot(slot);
        Connection connection = Database.get();
        try {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM save_slots WHERE slot = ?")) {
                ps.setInt(1, slot);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM save_slot_enemies WHERE slot = ?")) {
                ps.setInt(1, slot);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete slot " + slot, e);
        }
    }

    // یکبار سیوهای json قدیمی رو میریزه تو دیتابیس که اسلات های قبلی از دست نرن
    private void importLegacySaves() {
        Json json = new Json();
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            FileHandle file = Gdx.files.local(LEGACY_SAVE_DIR + "slot_" + slot + ".json");
            if (file.exists()) {
                saveGame(json.fromJson(GameData.class, file.readString()), slot);
            }
        }
    }
}
