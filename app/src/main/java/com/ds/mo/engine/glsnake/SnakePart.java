package com.ds.mo.engine.glsnake;

/**
 * Created by Mo on 02/06/2017.
 */

public class SnakePart {
    public int x, y;

    public SnakePart(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }
}
