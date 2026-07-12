package com.HollowKnight.model;

public class Achievement {
    private String name;
    private String desc;
    private boolean isUnlocked;

    public Achievement(String name, String desc, boolean isUnlocked) {
        this.name = name;
        this.desc = desc;
        this.isUnlocked = isUnlocked;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public String getName() {
        return name;
    }
}
