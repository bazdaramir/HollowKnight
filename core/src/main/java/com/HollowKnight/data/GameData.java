package com.HollowKnight.data;

import java.util.ArrayList;


public class GameData {
    public int health;
    public int maxHealth;
    public int soul;
    public int maxSoul;
    public float x;
    public float y;
    public float gameTimer;
    public String equippedCharms = "";
    public int secretState;

    public ArrayList<EnemySaveData> enemies = new ArrayList<>();


    public BossSaveData boss;

    public GameData() {
    }
}
