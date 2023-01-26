package com.ds.mo.engine.glsnake;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mo on 02/06/2017.
 */

public class Snake {
    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;

    public List<SnakePart> parts = new ArrayList<>();
    public int direction;
    private int lastDirection;

    public Snake() {
        direction = DOWN;
        lastDirection = DOWN;
        parts.add(new SnakePart(5, 6));
        parts.add(new SnakePart(6, 6));
        parts.add(new SnakePart(7, 6));
    }

    public void turnLeft() {
        direction += 1;
        if (direction > RIGHT) {
            direction = UP;     // UP = 0 (min value)
        }

        if (lastDirection == UP && direction == DOWN) {
            Log.d("SNAKE", "CAN'T GO UP AND DOWN");
            direction -= 1;
        }
        if (lastDirection == LEFT && direction == RIGHT) {
            Log.d("SNAKE", "CAN'T GO LEFT AND RIGHT");
            direction -= 1;
        }
        if (lastDirection == DOWN && direction == UP) {
            Log.d("SNAKE", "CAN'T GO DOWN AND UP");
            direction = RIGHT;
        }
        if (lastDirection == RIGHT && direction == LEFT) {
            Log.d("SNAKE", "CAN'T GO RIGHT AND LEFT");
            direction -= 1;
        }
    }

    public void turnRight() {
        direction -= 1;
        if (direction < UP) {
            direction = RIGHT;  //RIGHT = 3 (max value)
        }

        //Don't let snake turn back on itself
        if (lastDirection == UP && direction == DOWN) {
            Log.d("SNAKE", "CAN'T GO UP AND DOWN");
            direction += 1;
        }
        if (lastDirection == LEFT && direction == RIGHT) {
            Log.d("SNAKE", "CAN'T GO LEFT AND RIGHT");
            direction = UP;
        }
        if (lastDirection == DOWN && direction == UP) {
            Log.d("SNAKE", "CAN'T GO DOWN AND UP");
            direction += 1;
        }
        if (lastDirection == RIGHT && direction == LEFT) {
            Log.d("SNAKE", "CAN'T GO RIGHT AND LEFT");
            direction += 1;
        }
    }

    public void up() {
        // TODO: 07/09/2018 When turning up - lef/right really quickly, snake bites one self
        if(direction == DOWN || direction == UP){
            return;
        }
        direction = UP;
    }

    public void down() {
        if(direction == DOWN || direction == UP){
            return;
        }
        direction = DOWN;
    }

    public void left() {
        if(direction == LEFT || direction == RIGHT){
            return;
        }
        direction = LEFT;
    }

    public void right() {
        if(direction == LEFT || direction == RIGHT){
            return;
        }
        direction = RIGHT;
    }

    public void eat() {
        SnakePart end = parts.get(parts.size() - 1);
        parts.add(new SnakePart(end.x, end.y));
    }

    public void advance() {
        SnakePart head = parts.get(0);

        //Move the snakes body forward 1 position
        int len = parts.size() - 1;
        for (int i = len; i > 0; i--) {
            SnakePart before = parts.get(i - 1);
            SnakePart part = parts.get(i);
            part.x = before.x;
            part.y = before.y;
        }

        //Move head forward
        switch ((direction)) {
            case UP:
                head.y += 1;
                lastDirection = UP;
                break;
            case LEFT:
                head.x -= 1;
                lastDirection = LEFT;
                break;
            case DOWN:
                head.y -= 1;
                lastDirection = DOWN;
                break;
            case RIGHT:
                head.x += 1;
                lastDirection = RIGHT;
                break;
        }

        //Wrap snake back in world
        if (head.x < 0) {
            head.x = World.NO_X_TILES - 1;  //place in the array
        }
        if (head.x >= World.NO_X_TILES) {
            head.x = 0;
        }
        if (head.y < 0) {
            head.y = World.NO_Y_TILES - 1;
        }
        if (head.y >= World.NO_Y_TILES) {
            head.y = 0;
        }
//        Log.d("Snake", "current tile: " + head);
    }

    /**
     * Check if head is equal to any snake body
     *
     * @return true if head bites tail
     */
    public boolean checkBitten() {
        int len = parts.size();
        SnakePart head = parts.get(0);
        for (int i = 1; i < len; i++) {
            SnakePart part = parts.get(i);
            if (part.x == head.x && part.y == head.y) {
                return true;
            }
        }
        return false;
    }
}
