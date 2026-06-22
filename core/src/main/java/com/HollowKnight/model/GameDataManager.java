package com.HollowKnight.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;

public class GameDataManager {
    private static final String FILE_PATH = "savegame.json";
    private static GameDataManager instance;
    public SaveData saveData;
    private Json json;

    public static class GameSession {
        public String playerName = "Knight";
        public long playTimeSeconds = 0;
        public int completionPercentage = 0;
        // یه کلاس باید بزنیم که دقیق جایی که شوالیه تو اون بازی بوده و چقدر کامل شده و...
        // رو کاملا ذخیره بکنیم که بعدا بتونه بازی و ادامه بده اگه خواستش
        // و همچنین اگه بازی تموم شده بود مثلا یه صفحه بیاره که تموم شده یا فینیش
        // درکل باید این بخش ذخیرع اطلاعات بازی کامل تر بشه
    }

    public static class SaveData {
        public ArrayList<GameSession> recentGames = new ArrayList<>();

        public ArrayList<String> globalUnlockedAchievements = new ArrayList<>();
    }


    private GameDataManager() {
        json = new Json();
        loadData();
    }

    public static GameDataManager getInstance() {
        if (instance == null) {
            instance = new GameDataManager();
        }
        return instance;
    }

    public void loadData() {
        FileHandle file = Gdx.files.local(FILE_PATH);
        if (file.exists()) {
            saveData = json.fromJson(SaveData.class, file.readString());
        } else {
            saveData = new SaveData();
        }
    }

    public GameSession getCurrentGame() {
        if (saveData.recentGames.isEmpty()) {
            return null;
        }
        return saveData.recentGames.get(0);
    }

    public void saveData() {
        FileHandle file = Gdx.files.local(FILE_PATH);
        file.writeString(json.toJson(saveData), false);
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
}
