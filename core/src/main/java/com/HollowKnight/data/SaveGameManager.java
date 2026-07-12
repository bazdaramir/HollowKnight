package com.HollowKnight.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;


public class SaveGameManager {

    public static final int SLOT_COUNT = 4;
    private static final String SAVE_DIR = "data/saves/";

    private static SaveGameManager instance;
    private final Json json;

    private SaveGameManager() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
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

    private FileHandle slotFile(int slot) {
        validateSlot(slot);
        return Gdx.files.local(SAVE_DIR + "slot_" + slot + ".json");
    }


    public void saveGame(GameData data, int slot) {
        FileHandle file = slotFile(slot);
        file.writeString(json.toJson(data, GameData.class), false);
    }

    public GameData loadGame(int slot) {
        FileHandle file = slotFile(slot);
        if (!file.exists()) {
            return null;
        }
        return json.fromJson(GameData.class, file.readString());
    }


    public boolean hasSave(int slot) {
        return slotFile(slot).exists();
    }


    public void deleteSave(int slot) {
        FileHandle file = slotFile(slot);
        if (file.exists()) {
            file.delete();
        }
    }
}
