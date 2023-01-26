package com.ds.mo.engine.glsnake;

import java.util.Random;

/**
 * Created by Mo on 02/06/2017.
 */

public class World {
    //    private static int scale = 4;
//    public static final int NO_X_TILES = 10 * scale;
//    public static final int NO_Y_TILES = 13 * scale;
//
//    public static final int TILE_WIDTH = 32 / scale;
//    public static final int TILE_HEIGHT = 32 / scale;

    //determines the num of tiles and size of tiles
    public static final int EASY = 0;
    public static final int MEDIUM = 1;
    public static final int HARD = 2;

    public static int NO_X_TILES ;
    public static int NO_Y_TILES ;
    public static int TILE_WIDTH ;
    public static int TILE_HEIGHT;

    private static final int SCORE_INCREMENT = 10;
//        private static final float TICK_INITIAL = 1.4f;
    private static final float TICK_INITIAL = 0.1f;         //speed
    private static final float TICK_DECREMENT = 0.05f;      //amount to speed up snake
    private float tickTime = 0;
    private float tick = TICK_INITIAL;

    Snake snake;                //World.snake
    Stain stain;                //World.stain
    boolean gameOver = false;   //World.gameOver
    int score = 0;              //World.score

    private boolean fields[][];
    private Random random = new Random();

    public World(int xTile, int yTile, int tileWidth, int tileHeight) {
        NO_X_TILES = xTile;
        NO_Y_TILES = yTile;
        TILE_WIDTH = tileWidth;
        TILE_HEIGHT = tileHeight;

        snake = new Snake();
        fields = new boolean[NO_Y_TILES][NO_X_TILES];
        placeStain();
    }


    private void placeStain() {
        for (int y = 0; y < NO_Y_TILES; y++) {
            for (int x = 0; x < NO_X_TILES; x++) {
                fields[y][x] = false;
            }
        }

        int len = snake.parts.size();
        for (int i = 0; i < len; i++) {
            SnakePart part = snake.parts.get(i);
            fields[part.y][part.x] = true;
        }

        int stainX = random.nextInt(NO_X_TILES);
        int stainY = random.nextInt(NO_Y_TILES);
        while (true) {
            if (fields[stainY][stainX] == false) {
                break;
            }
            stainX += 1;
            if (stainX >= NO_X_TILES) {
                stainX = 0;
                stainY += 1;
                if (stainY >= NO_Y_TILES) {
                    stainY = 0;
                }
            }
        }
        stain = new Stain(stainX, stainY, random.nextInt(3));
    }

    public void update(float deltaTime) {
        if (gameOver) {
            return;
        }

        tickTime += deltaTime;

        while (tickTime > tick) {
            tickTime -= tick;
            snake.advance();
            if (snake.checkBitten()) {
                gameOver = true;
                return;
            }

            SnakePart head = snake.parts.get(0);
            if (head.x == stain.x && head.y == stain.y) {
                score += SCORE_INCREMENT;
                snake.eat();
                //Size of snake is size of the world
                if (snake.parts.size() == NO_X_TILES * NO_Y_TILES) {
                    gameOver = true;
                    return;
                } else {
                    placeStain();
                }

//                //Speed up snake when score reaches multiple of 100
//                if (score % 100 == 0 && tick - TICK_DECREMENT > 0) {
//                    tick -= TICK_DECREMENT;
//                }
            }
        }
    }

}
