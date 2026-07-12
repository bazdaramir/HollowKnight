package com.HollowKnight.model;

import com.badlogic.gdx.math.Rectangle;

public class Block {
    public Rectangle rect;
    public boolean isSolid;
    public boolean isDeadly;
    public boolean isBreakable;
    public int health;
    public int cellX;
    public int cellY;

    public Block(float x, float y, float width, float height, boolean isSolid, boolean isDeadly, boolean isBreakable, int health, int cellX, int cellY) {
        this.rect = new Rectangle(x, y, width, height);
        this.isSolid = isSolid;
        this.isDeadly = isDeadly;
        this.isBreakable = isBreakable;
        this.health = health;
        this.cellX = cellX;
        this.cellY = cellY;
    }
}
