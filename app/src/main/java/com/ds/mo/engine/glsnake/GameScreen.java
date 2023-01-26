package com.ds.mo.engine.glsnake;

import android.util.Log;

import com.ds.mo.engine.android.SpriteBatcher;
import com.ds.mo.engine.common.Camera2D;
import com.ds.mo.engine.common.OverlapTester;
import com.ds.mo.engine.common.Rectangle;
import com.ds.mo.engine.common.Vector2D;
import com.ds.mo.engine.framework.GLScreen;
import com.ds.mo.engine.framework.Game;
import com.ds.mo.engine.framework.Input.TouchEvent;
import com.ds.mo.engine.common.Color;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Actual game screen
 * <p>
 * Ties together all the classes
 * <p>
 * Created by Mo on 02/06/2017.
 */

public class GameScreen extends GLScreen {
    enum GameState {
        Ready,
        Running, Paused,
        GameOver
    }

    private GameState state = GameState.Ready;
    private World world;
    private int oldScore = 0;
    private String score = "0";

    public static final float WORLD_WIDTH = 360;
    public static final float WORLD_HEIGHT = 640;
    private Camera2D camera;
    private SpriteBatcher batcher;
    private Vector2D touchPos = new Vector2D();
    private Vector2D center;

    private static final int padding = 50;

    float r, g, b;

    // TODO: 06/09/2018 make static final
    private float X_SHIFT = (WORLD_WIDTH - (32 * 11)) / 2;  //align center
    private float Y_SHIFT = 32 * 8;                         //224 (8 tiles up)

    private Rectangle b1, b2, b3, b4;

    public GameScreen(Game game, int gameMode) {
        super(game);

        camera = new Camera2D(glGraphics, WORLD_WIDTH, WORLD_HEIGHT);
        batcher = new SpriteBatcher(glGraphics, 3500);   //max sprites
        center = new Vector2D(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);

        initGame(gameMode);
        float scale = 3;
        float w = 32 * scale;
        float h = 32 * scale;
        b1 = new Rectangle(center.x - w / 2, (32 * 5), w, h);
        b2 = new Rectangle(center.x - w / 2, 0, w, h);
        b3 = new Rectangle(center.x - w - w / 2, 32 * 8 / 2 - h / 2, w, h);
        b4 = new Rectangle(center.x + w - w / 2, 32 * 8 / 2 - h / 2, w, h);
        Log.d("GameScreen", "xShift: " + X_SHIFT + ", yShift: " + Y_SHIFT);
    }

    private void initGame(int gameMode) {
        int xTiles = 11;
        int yTiles = 12;
        int tileWidth = 32;
        int tileHeight = 32;
        int scale;
        switch (gameMode) {
            case World.EASY:
                scale = 1;
                world = new World(xTiles * scale, yTiles * scale,
                        tileWidth / scale, tileHeight / scale);
                break;
            case World.MEDIUM:
                scale = 2;
                world = new World(xTiles * scale, yTiles * scale,
                        tileWidth / scale, tileHeight / scale);
                break;
            case World.HARD:
                scale = 4;
                world = new World(xTiles * scale, yTiles * scale,
                        tileWidth / scale, tileHeight / scale);
                break;
        }
        r = 170 / 255f;
        g = 201 / 255f;
        b = 154 / 255f;
    }

    private void handleRunningTouch() {
        //If left/right button is pressed
        if (touchPos.y > (32 * 8)) {
            //Touch is on the game display
            if (touchPos.x < WORLD_WIDTH / 2) {
                Log.d("GameScreen", "turn left");
                world.snake.turnLeft();
            } else {
                Log.d("GameScreen", "turn right");
                world.snake.turnRight();
            }
        }

        if (OverlapTester.pointInRectangle(b1, touchPos)) {
            Log.d("GameScreen", "UP");
            world.snake.up();
        }
        if (OverlapTester.pointInRectangle(b2, touchPos)) {
            Log.d("GameScreen", "DOWN");
            world.snake.down();
        }
        if (OverlapTester.pointInRectangle(b3, touchPos)) {
            Log.d("GameScreen", "LEFT");
            world.snake.left();
        }
        if (OverlapTester.pointInRectangle(b4, touchPos)) {
            Log.d("GameScreen", "RIGHT");
            world.snake.right();
        }
    }

    private void updateReady(List<TouchEvent> touchEvents) {
//        if (touchEvents.size() > 0) {
//            state = GameState.Running;
//        }
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            touchPos.set(event.x, event.y);
            camera.touchToWorld(touchPos);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (touchPos.y > (32 * 8)) {
                    state = GameState.Running;
                }
            }
        }
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            touchPos.set(event.x, event.y);
            camera.touchToWorld(touchPos);
            //Touch down
            if (event.type == TouchEvent.TOUCH_DOWN) {
                if (state == GameState.Running) {
                    handleRunningTouch();
                }
            }
            //Touch up
            if (event.type == TouchEvent.TOUCH_UP) {
                //If the pause button
//                if (inBounds(event, pauseX, pauseY, pauseW, pauseH)) {
//                    if (Settings.soundEnabled) {
//                        Assets.click.play(1);
//                    }
//                    state = GameState.Paused;
//                    return;
//                }
            }
        }
        world.update(deltaTime);
        if (world.gameOver) {
            if (Settings.soundEnabled) {
//                Assets.click.play(1);
            }
            state = GameState.GameOver;
            Log.d("GameScreen", "state = GameOver");
        }

        //Update highest score
        if (oldScore != world.score) {
            oldScore = world.score;
            score = "" + oldScore;
            if (Settings.soundEnabled) {
//                Assets.eat.play(1);
            }
        }
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            touchPos.set(event.x, event.y);
            camera.touchToWorld(touchPos);
            if (event.type == TouchEvent.TOUCH_UP) {
                state = GameState.Running;
                return;
            }
//                //handle paused button
//                if (inBounds(event, resumeX, resumeY, resumeW, resumeH)) {
//                    if (Settings.soundEnabled) {
//                        Assets.click.play(1);
//                    }
//                //handle quite
//                if (inBounds(event, quitX, quitY, quitW, quitH)) {
//                    if (Settings.soundEnabled) {
//                        Assets.click.play(1);
//                    }
//                    game.setScreen(new MainMenuScreen(game));
//                    return;
//                }
//            }
        }
    }

    private void updateGameOver(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            touchPos.set(event.x, event.y);
            camera.touchToWorld(touchPos);
            //Touch down
            if (event.type == TouchEvent.TOUCH_DOWN) {
                game.setScreen(new ReadyScreen(game));
                return;
            }
        }
    }

    private void drawWorld(SpriteBatcher batcher) {
        batcher.setColor(r, g, b, 1);
        for (int y = 0; y < World.NO_Y_TILES; y++) {
            for (int x = 0; x < World.NO_X_TILES; x++) {
                batcher.drawSprite(World.TILE_WIDTH / 2 + x * World.TILE_WIDTH + X_SHIFT,
                        World.TILE_HEIGHT / 2 + y * World.TILE_HEIGHT + Y_SHIFT,
                        World.TILE_WIDTH, World.TILE_HEIGHT, Assets.tile);
            }
        }
    }

    private void drawGame(SpriteBatcher batcher, World world) {
        Snake snake = world.snake;
        SnakePart head = snake.parts.get(0);
        Stain stain = world.stain;

//        Pixmap stainPixmap = null;
//        if (stain.type == Stain.TYPE_1) {
//            stainPixmap = Assets.statin1;
//        }
//        if (stain.type == Stain.TYPE_2) {
//            stainPixmap = Assets.statin2;   //fix
//        }
//        if (stain.type == Stain.TYPE_3) {
//            stainPixmap = Assets.statin1;   //fix
//        }
        int x = stain.x * World.TILE_WIDTH + World.TILE_WIDTH / 2;
        int y = stain.y * World.TILE_HEIGHT + World.TILE_HEIGHT / 2;
//        g.drawPixmap(stainPixmap, x, y);
        batcher.setColor(Color.ROYAL);
        batcher.drawSprite(x + X_SHIFT, y + Y_SHIFT, World.TILE_WIDTH, World.TILE_HEIGHT, Assets.tile);
        batcher.setColor(Color.BLACK);
        batcher.drawSprite(x + X_SHIFT, y + Y_SHIFT, World.TILE_WIDTH, World.TILE_HEIGHT, Assets.food);

        int len = snake.parts.size();
        for (int i = 1; i < len; i++) {
            SnakePart part = snake.parts.get(i);
            x = part.x * World.TILE_WIDTH + World.TILE_WIDTH / 2;
            y = part.y * World.TILE_HEIGHT + World.TILE_HEIGHT / 2;
//            g.drawPixmap(Assets.tailV, x, y);
//            if (snake.direction == Snake.UP || snake.direction == Snake.DOWN) {
////                g.drawPixmap(Assets.tailV, x, y);
//                batcher.drawSprite(x, y, World.TILE_WIDTH, World.TILE_HEIGHT, Assets.tile);
//            } else {
////                g.drawPixmap(Assets.tailH, x, y);
//                batcher.drawSprite(x, y, World.TILE_WIDTH, World.TILE_HEIGHT, Assets.tile);
//            }
            batcher.setColor(0.6f, 0f, 0f, 1f);
            batcher.drawSprite(x + X_SHIFT, y + Y_SHIFT, World.TILE_WIDTH, World.TILE_HEIGHT, Assets.tile);

        }

//        Pixmap headPixmap = null;
//        if (snake.direction == Snake.UP) {
//            headPixmap = Assets.headUp;
//        }
//        if (snake.direction == Snake.LEFT) {
//            headPixmap = Assets.headLeft;
//        }
//        if (snake.direction == Snake.DOWN) {
//            headPixmap = Assets.headDown;
//        }
//        if (snake.direction == Snake.RIGHT) {
//            headPixmap = Assets.headRight;
//        }
        x = head.x * World.TILE_WIDTH + World.TILE_WIDTH / 2;
        y = head.y * World.TILE_HEIGHT + World.TILE_HEIGHT / 2;
//        g.drawPixmap(headPixmap, x - headPixmap.getWidth() / 2, y - headPixmap.getHeight() / 2);
        batcher.setColor(0.4f, 0f, 0f, 1f);
        batcher.drawSprite(x + X_SHIFT, y + Y_SHIFT, World.TILE_WIDTH, World.TILE_HEIGHT, Assets.tile);
    }

    private void drawControls(SpriteBatcher batcher) {
        float opacity = 0.1f;
        batcher.setColor(Color.GOLD);
        batcher.setOpacity(opacity);
        batcher.drawSprite(b1.lowerLeft.x + b1.width / 2, b1.lowerLeft.y + b1.height / 2,
                b1.width, b1.height, Assets.drawRectFull);
        batcher.setColor(Color.PURPLE);
        batcher.setOpacity(opacity);
        batcher.drawSprite(b2.lowerLeft.x + b2.width / 2, b2.lowerLeft.y + b2.height / 2,
                b2.width, b2.height, Assets.drawRectFull);
        batcher.setColor(Color.GRAY);
        batcher.setOpacity(opacity);
        batcher.drawSprite(b3.lowerLeft.x + b3.width / 2, b3.lowerLeft.y + b3.height / 2,
                b3.width, b3.height, Assets.drawRectFull);
        batcher.setColor(Color.ORANGE);
        batcher.setOpacity(opacity);
        batcher.drawSprite(b4.lowerLeft.x + b4.width / 2, b4.lowerLeft.y + b4.height / 2,
                b4.width, b4.height, Assets.drawRectFull);
    }

    private void drawReadyUI(SpriteBatcher batcher) {
//        g.drawPixmap(Assets.ready, readyX, readyY);
//        g.drawLine(0, 416, 480, 416, Color.BLACK);
        Assets.font.drawText(batcher, "READY!", center.x, padding);
    }

    private void drawRunningUI(SpriteBatcher batcher) {
//        g.drawPixmap(Assets.buttons, leftX, leftY, 0, 0, leftW, leftH);
//        g.drawPixmap(Assets.buttons, rightX, rightY, leftW, 0, rightW, rightH);
//        g.drawPixmap(Assets.buttons, pauseX, pauseY, 0, pauseW, pauseW, pauseH);
//        g.drawLine(0, 416, 480, 416, Color.BLACK);
        Assets.font.drawText(batcher, "RUNNING!", center.x, padding);
    }

    private void drawPausedUI(SpriteBatcher batcher) {
//        //Draw pause button
//        g.drawPixmap(Assets.pause, resumeX, resumeY, 0, 0, resumeW, resumeH);
//        g.drawPixmap(Assets.pause, quitX, quitY, 0, quitH, quitW, quitH);
//        g.drawLine(0, 416, 480, 416, Color.BLACK);
        Assets.font.drawText(batcher, "PAUSED!", center.x, padding);
    }

    private void drawGameOverUI(SpriteBatcher batcher) {
//        g.drawPixmap(Assets.gameOver, gameoverX, gameoverY);
//        g.drawPixmap(Assets.buttons, crossX, crossY, crossW, crossH, crossW, crossH);
//        g.drawLine(0, 416, 480, 416, Color.BLACK);
        Assets.font.drawText(batcher, "GAMEOVER!", center.x, padding);
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents(); //Get key events too to clear the internal buffer

        if (state == GameState.Ready) {
            updateReady(touchEvents);
        }
        if (state == GameState.Running) {
            updateRunning(touchEvents, deltaTime);
        }
        if (state == GameState.Paused) {
            updatePaused(touchEvents);
        }
        if (state == GameState.GameOver) {
            updateGameOver(touchEvents);
        }
    }

    @Override
    public void draw(float deltaTime) {
        //Log to console the average fps
//        FPSCounter.logFrame();
        GL10 gl = glGraphics.getGL();
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);               //Specifies which buffer to clear
        gl.glClearColor(0, 0, 0, 1);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        camera.setViewportAndMatrices();    //640 : 360

        batcher.beginBatch(Assets.textureAtlas);
        //Draw background tiles
        drawWorld(batcher);
        //Draw snake and food
        drawGame(batcher, world);

        //Overlay game with controls
        drawControls(batcher);

        //Draw title
        batcher.setColor(Color.WHITE);
        if (state == GameState.Ready) {
            drawReadyUI(batcher);
        }
        if (state == GameState.Running) {
            drawRunningUI(batcher);
        }
        if (state == GameState.Paused) {
            drawPausedUI(batcher);
        }
        if (state == GameState.GameOver) {
            drawGameOverUI(batcher);
        }

        // TODO: 02/09/2018 draw score
        batcher.endBatch();
    }

    @Override
    public void pause() {
        //Pause game if running
        if (state == GameState.Running) {
            state = GameState.Paused;
        }
        //Save the settings
        if (world.gameOver) {
            Settings.addScore(world.score);
            Settings.save(game.getFileIO());
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

}
