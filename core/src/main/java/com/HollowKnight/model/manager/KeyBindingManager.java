package com.HollowKnight.model.manager;

import com.HollowKnight.data.GameDataManager;
import com.badlogic.gdx.Input;

import java.util.LinkedHashMap;
import java.util.Map;

// کلید هر اکشن بازی از اینجا خونده میشه، ذخیره سازیش هم تو همون دیتابیس بازیه
public class KeyBindingManager {

    public static final String LEFT = "key_left";
    public static final String RIGHT = "key_right";
    public static final String UP = "key_up";
    public static final String DOWN = "key_down";
    public static final String JUMP = "key_jump";
    public static final String ATTACK = "key_attack";
    public static final String DASH = "key_dash";
    public static final String FOCUS = "key_focus";
    public static final String FIREBALL = "key_fireball";
    public static final String SCREAM = "key_scream";
    public static final String INTERACT = "key_interact";
    public static final String INVENTORY = "key_inventory";
    public static final String PAUSE = "key_pause";

    private static KeyBindingManager instance;

    private final LinkedHashMap<String, Integer> defaults = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> bindings = new LinkedHashMap<>();

    private KeyBindingManager() {
        defaults.put(LEFT, Input.Keys.LEFT);
        defaults.put(RIGHT, Input.Keys.RIGHT);
        defaults.put(UP, Input.Keys.UP);
        defaults.put(DOWN, Input.Keys.DOWN);
        defaults.put(JUMP, Input.Keys.SPACE);
        defaults.put(ATTACK, Input.Keys.X);
        defaults.put(DASH, Input.Keys.D);
        defaults.put(FOCUS, Input.Keys.F);
        defaults.put(FIREBALL, Input.Keys.B);
        defaults.put(SCREAM, Input.Keys.S);
        defaults.put(INTERACT, Input.Keys.E);
        defaults.put(INVENTORY, Input.Keys.I);
        defaults.put(PAUSE, Input.Keys.ESCAPE);

        // اول پیش فرض ها، بعد هرچی تو دیتابیس ذخیره شده روش نوشته میشه
        bindings.putAll(defaults);
        Map<String, Integer> saved = GameDataManager.getInstance().getKeyBindings();
        for (String action : defaults.keySet()) {
            Integer keycode = saved.get(action);
            if (keycode != null) {
                bindings.put(action, keycode);
            }
        }
    }

    public static KeyBindingManager getInstance() {
        if (instance == null) {
            instance = new KeyBindingManager();
        }
        return instance;
    }

    public int get(String action) {
        Integer keycode = bindings.get(action);
        return keycode == null ? Input.Keys.UNKNOWN : keycode;
    }

    public String getKeyName(String action) {
        return Input.Keys.toString(get(action)).toUpperCase();
    }

    // کلید جدید فقط وقتی قبول میشه که معتبر باشه و رو اکشن دیگه ای ننشسته باشه
    public boolean rebind(String action, int keycode) {
        if (!defaults.containsKey(action) || !isValidKey(keycode)) {
            return false;
        }
        for (Map.Entry<String, Integer> entry : bindings.entrySet()) {
            if (entry.getValue() == keycode && !entry.getKey().equals(action)) {
                return false;
            }
        }
        bindings.put(action, keycode);
        GameDataManager.getInstance().setKeyBindings(bindings);
        return true;
    }

    public void resetToDefaults() {
        bindings.clear();
        bindings.putAll(defaults);
        GameDataManager.getInstance().setKeyBindings(bindings);
    }

    // اسکیپ برای لغو کردنه و کنترل هم مال کدهای تقلبه، پس نباید بایند بشن
    public static boolean isValidKey(int keycode) {
        if (keycode == Input.Keys.ANY_KEY || keycode == Input.Keys.UNKNOWN) {
            return false;
        }
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.CONTROL_LEFT || keycode == Input.Keys.CONTROL_RIGHT) {
            return false;
        }
        try {
            return Input.Keys.toString(keycode) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
