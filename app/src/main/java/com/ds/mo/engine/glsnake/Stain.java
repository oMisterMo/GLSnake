package com.ds.mo.engine.glsnake;

/**
 * Food the snake must eat
 * <p>
 * Created by Mo on 02/06/2017.
 */

public class Stain {
    public static final int TYPE_1 = 0;
    public static final int TYPE_2 = 1;
    public static final int TYPE_3 = 2;
    public int x, y;
    public int type;

    public Stain(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
