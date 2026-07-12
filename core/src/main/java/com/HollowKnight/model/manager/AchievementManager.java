package com.HollowKnight.model.manager;

import com.HollowKnight.data.GameDataManager;


public class AchievementManager {

    public static final String TRUE_HUNTER = "TRUE_HUNTER";
    public static final String CHARMED = "ACH_CHARMED";
    public static final String DEFEAT_BOSS = "ACH_FULLY_CHARMED";
    public static final String SOUL_MASTER = "ACH_SOUL_MASTER";
    public static final String ZOTE = "ZOTE";
    public static final String COMPLETION = "ACH_COMPLETION";
    public static final String SPEEDRUN = "ACH_SPEEDRUN";


    public void unlockCharmed() {
        unlock(CHARMED);
    }

    public void unlockDefeatBoss() {
        unlock(DEFEAT_BOSS);
    }


    public void unlockTrueHunter() {
        unlock(TRUE_HUNTER);
    }

    public void unlockSoulMaster() {
        unlock(SOUL_MASTER);
    }

    public void unlockZote() {
        unlock(ZOTE);
    }

    public void unlockCompletion() {
        unlock(COMPLETION);
    }

    public void unlockSpeedrun() {
        unlock(SPEEDRUN);
    }

    private void unlock(String id) {
        GameDataManager.getInstance().unlockAchievement(id);
    }
}
