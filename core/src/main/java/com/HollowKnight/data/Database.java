package com.HollowKnight.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String DB_PATH = "data/hollowknight.db";

    private static Connection connection;

    private Database() {
    }

    public static Connection get() {
        if (connection == null) {
            FileHandle file = Gdx.files.local(DB_PATH);
            file.parent().mkdirs();
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + file.file().getAbsolutePath());
                createTables();
            } catch (SQLException e) {
                throw new RuntimeException("Could not open the save database", e);
            }
        }
        return connection;
    }

    public static boolean needsLegacyImport(String name) {
        try (PreparedStatement ps = get().prepareStatement("INSERT OR IGNORE INTO legacy_imports (name) VALUES (?)")) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not check the legacy import state", e);
        }
    }

    private static void createTables() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS global_data ("
                + "id INTEGER PRIMARY KEY, "
                + "selected_theme INTEGER NOT NULL DEFAULT 0)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS recent_games ("
                + "position INTEGER PRIMARY KEY, "
                + "player_name TEXT, "
                + "play_time_seconds INTEGER, "
                + "completion_percentage INTEGER)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS unlocked_achievements ("
                + "achievement_id TEXT PRIMARY KEY)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS save_slots ("
                + "slot INTEGER PRIMARY KEY, "
                + "health INTEGER, max_health INTEGER, soul INTEGER, max_soul INTEGER, "
                + "x REAL, y REAL, game_timer REAL, equipped_charms TEXT, secret_state INTEGER, "
                + "boss_x REAL, boss_y REAL, boss_health INTEGER, boss_is_dead INTEGER)");

            try {
                st.executeUpdate("ALTER TABLE save_slots ADD COLUMN secret_state INTEGER");
            } catch (SQLException ignored) {
            }

            st.executeUpdate("CREATE TABLE IF NOT EXISTS save_slot_enemies ("
                + "slot INTEGER, position INTEGER, type TEXT, x REAL, y REAL, health INTEGER, "
                + "PRIMARY KEY (slot, position))");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS key_bindings ("
                + "action TEXT PRIMARY KEY, keycode INTEGER NOT NULL)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS legacy_imports ("
                + "name TEXT PRIMARY KEY)");
        }
    }
}
