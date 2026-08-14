package com.HollowKnight.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameDataManager {
    private static final String LEGACY_FILE_PATH = "savegame.json";
    private static GameDataManager instance;
    public SaveData saveData;

    public static class GameSession {
        public String playerName = "Knight";
        public long playTimeSeconds = 0;
        public int completionPercentage = 0;

    }

    public static class SaveData {
        public ArrayList<GameSession> recentGames = new ArrayList<>();

        public ArrayList<String> globalUnlockedAchievements = new ArrayList<>();

        public int selectedTheme = 0;

        public LinkedHashMap<String, Integer> keyBindings = new LinkedHashMap<>();
    }


    private GameDataManager() {
        if (Database.needsLegacyImport("global_data")) {
            importLegacyData();
        }
        loadData();
    }

    public static GameDataManager getInstance() {
        if (instance == null) {
            instance = new GameDataManager();
        }
        return instance;
    }

    public void loadData() {
        saveData = new SaveData();
        try (Statement st = Database.get().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                "SELECT player_name, play_time_seconds, completion_percentage FROM recent_games ORDER BY position")) {
                while (rs.next()) {
                    GameSession session = new GameSession();
                    session.playerName = rs.getString(1);
                    session.playTimeSeconds = rs.getLong(2);
                    session.completionPercentage = rs.getInt(3);
                    saveData.recentGames.add(session);
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT achievement_id FROM unlocked_achievements ORDER BY rowid")) {
                while (rs.next()) {
                    saveData.globalUnlockedAchievements.add(rs.getString(1));
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT selected_theme FROM global_data WHERE id = 1")) {
                if (rs.next()) {
                    saveData.selectedTheme = rs.getInt(1);
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT action, keycode FROM key_bindings")) {
                while (rs.next()) {
                    saveData.keyBindings.put(rs.getString(1), rs.getInt(2));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not read the save database", e);
        }
    }

    public GameSession getCurrentGame() {
        if (saveData.recentGames.isEmpty()) {
            return null;
        }
        return saveData.recentGames.get(0);
    }

    public void saveData() {
        Connection connection = Database.get();
        try {
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("DELETE FROM recent_games");
                st.executeUpdate("DELETE FROM unlocked_achievements");
            }
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO recent_games "
                + "(position, player_name, play_time_seconds, completion_percentage) VALUES (?, ?, ?, ?)")) {
                for (int i = 0; i < saveData.recentGames.size(); i++) {
                    GameSession session = saveData.recentGames.get(i);
                    ps.setInt(1, i);
                    ps.setString(2, session.playerName);
                    ps.setLong(3, session.playTimeSeconds);
                    ps.setInt(4, session.completionPercentage);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO unlocked_achievements (achievement_id) VALUES (?)")) {
                for (String achievementId : saveData.globalUnlockedAchievements) {
                    ps.setString(1, achievementId);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO global_data (id, selected_theme) VALUES (1, ?)")) {
                ps.setInt(1, saveData.selectedTheme);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO key_bindings (action, keycode) VALUES (?, ?)")) {
                for (Map.Entry<String, Integer> binding : saveData.keyBindings.entrySet()) {
                    ps.setString(1, binding.getKey());
                    ps.setInt(2, binding.getValue());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not write the save database", e);
        }
    }

    public void saveRecentGame(GameSession newSession) {
        saveData.recentGames.add(0, newSession);
        if (saveData.recentGames.size() > 4) {
            saveData.recentGames.remove(saveData.recentGames.size() - 1);
        }
        saveData();
    }


    public void unlockAchievement(String achievementId) {
        if (saveData == null) {
            saveData = new SaveData();
        }
        if (!saveData.globalUnlockedAchievements.contains(achievementId)) {
            saveData.globalUnlockedAchievements.add(achievementId);
            saveData();
        }
    }

    public boolean isAchievementUnlocked(String achievementId) {
        if (saveData == null || saveData.globalUnlockedAchievements == null) {
            return false;
        }
        return saveData.globalUnlockedAchievements.contains(achievementId);
    }

    public int getSelectedTheme() {
        if (saveData == null) {
            return 0;
        }
        return saveData.selectedTheme;
    }

    public LinkedHashMap<String, Integer> getKeyBindings() {
        if (saveData == null) {
            return new LinkedHashMap<>();
        }
        return saveData.keyBindings;
    }

    public void setKeyBindings(Map<String, Integer> newBindings) {
        if (saveData == null) {
            saveData = new SaveData();
        }
        saveData.keyBindings.clear();
        saveData.keyBindings.putAll(newBindings);
        saveData();
    }

    public void setSelectedTheme(int themeIndex) {
        if (saveData == null) {
            saveData = new SaveData();
        }
        saveData.selectedTheme = themeIndex;
        saveData();
    }

    // یکبار اطلاعات فایل json قدیمی رو میریزه تو دیتابیس که چیزی از دست نره
    private void importLegacyData() {
        FileHandle file = Gdx.files.local(LEGACY_FILE_PATH);
        if (!file.exists()) {
            return;
        }
        saveData = new Json().fromJson(SaveData.class, file.readString());
        saveData();
    }
}
