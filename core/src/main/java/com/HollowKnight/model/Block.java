package com.HollowKnight.model;

import com.badlogic.gdx.math.Rectangle;

public class Block {
    public Rectangle rect;
    public boolean isSolid;
    public boolean isDeadly;

    public Block(float x, float y, float width, float height, boolean isSolid, boolean isDeadly) {
        this.rect = new Rectangle(x, y, width, height);
        this.isSolid = isSolid;
        this.isDeadly = isDeadly;
    }
}
