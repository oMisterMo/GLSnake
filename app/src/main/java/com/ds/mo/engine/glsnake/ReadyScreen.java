package com.ds.mo.engine.glsnake;

import android.util.Log;

import com.ds.mo.engine.android.SpriteBatcher;
import com.ds.mo.engine.common.Camera2D;
import com.ds.mo.engine.common.Circle;
import com.ds.mo.engine.common.Color;
import com.ds.mo.engine.common.OverlapTester;
import com.ds.mo.engine.common.Rectangle;
import com.ds.mo.engine.common.Vector2D;
import com.ds.mo.engine.framework.GLScreen;
import com.ds.mo.engine.framework.Game;
import com.ds.mo.engine.framework.Input.TouchEvent;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class ReadyScreen extends GLScreen {
    private static final float TIME_TO_WAIT = 0f;

    public static final float WORLD_WIDTH = 360;
    public static final float WORLD_HEIGHT = 640;

    private Camera2D camera;
    private SpriteBatcher batcher;
    private Vector2D touchPos = new Vector2D();

    private Vector2D center;
    private float elapsed;

    private Rectangle easyBounds;
    private Rectangle mediumBounds;
    private Rectangle hardBounds;

    //random test circle
    private Circle circleBounds;
    private Vector2D circlePos;


    public ReadyScreen(Game game) {
        super(game);
        Log.d("ReadyScreen", "ReadyScreen constructor...");
        camera = new Camera2D(glGraphics, WORLD_WIDTH, WORLD_HEIGHT);
        batcher = new SpriteBatcher(glGraphics, 100);   //max sprites

        center = new Vector2D(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        elapsed = 0;

        float gap = 10f;
        float w = 100;
        float h = 100;
        easyBounds = new Rectangle(center.x - w / 2, (center.y - h / 2) + w + gap, w, h);
        mediumBounds = new Rectangle(center.x - w / 2, center.y - h / 2, w, h);
        hardBounds = new Rectangle(center.x - w / 2, (center.y - h / 2) - w - gap, w, h);
        //---------------------------------------
        circlePos = new Vector2D(center.x, center.y);
        circleBounds = new Circle(circlePos.x, circlePos.y, 16);
    }

    private void updateReady(List<TouchEvent> events, float deltaTime) {
        int len = events.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = events.get(i);
            touchPos.set(event.x, event.y);     //0, 0, 2560, 1440      yDown
            camera.touchToWorld(touchPos);      //0, 0, 360, 640        yUp

            circlePos.set(touchPos);
            circleBounds.center.set(circlePos);
            if (event.type == TouchEvent.TOUCH_DOWN) {
                Log.d("ReadyScreen", "Touch up, creating new GameScreen");
                if (elapsed > TIME_TO_WAIT) {
                    if (OverlapTester.pointInRectangle(easyBounds, touchPos)) {
                        Log.d("ReadyScreen", "Easy selected");
                        game.setScreen(new GameScreen(game, World.EASY));
                        return;
                    }
                    if (OverlapTester.pointInRectangle(mediumBounds, touchPos)) {
                        Log.d("ReadyScreen", "Medium selected");
                        game.setScreen(new GameScreen(game, World.MEDIUM));
                        return;
                    }
                    if (OverlapTester.pointInRectangle(hardBounds, touchPos)) {
                        Log.d("ReadyScreen", "Hard selected");
                        game.setScreen(new GameScreen(game, World.HARD));
                        return;
                    }
                }
            }
        }
    }

    private void drawReady() {
        batcher.beginBatch(Assets.textureAtlas);
        batcher.setColor(Color.WHITE);

        //circleBounds.x
//        batcher.setColor(Color.YELLOW);
//        batcher.drawSprite(circleBounds.center.x, circleBounds.center.y,
//                circleBounds.radius * 2, circleBounds.radius * 2, Assets.fillCircle);
//        //circlePos.x
//        batcher.setColor(Color.RED);
//        batcher.drawSprite(circlePos.x, circlePos.y, 32, 32, Assets.drawCircle);
//        batcher.setColor(Color.WHITE);
        int scale = 3;
        //DRAW TITLE
        Assets.font.drawText(batcher, "SNAKE", center.x, WORLD_HEIGHT - 50,
                16 * scale, 16 * scale);
        Assets.font.drawText(batcher, "PLAY", center.x, 50);

        //DRAW HIT BOUNDS
        batcher.setColor(0, 0, 0.5f, 1);
        //draw sprites draws from the center of an image
        batcher.drawSprite(easyBounds.lowerLeft.x + easyBounds.width / 2,
                easyBounds.lowerLeft.y + easyBounds.height / 2,
                easyBounds.width, easyBounds.height, Assets.fillRect);
        batcher.drawSprite(mediumBounds.lowerLeft.x + mediumBounds.width / 2,
                mediumBounds.lowerLeft.y + mediumBounds.height / 2,
                mediumBounds.width, mediumBounds.height, Assets.fillRect);
        batcher.drawSprite(hardBounds.lowerLeft.x + hardBounds.width / 2,
                hardBounds.lowerLeft.y + hardBounds.height / 2,
                hardBounds.width, hardBounds.height, Assets.fillRect);

        //DRAW TEXT
        batcher.setColor(Color.WHITE);
        Assets.font.drawText(batcher, "EASY", easyBounds.lowerLeft.x + easyBounds.width / 2,
                easyBounds.lowerLeft.y + easyBounds.height / 2);
        Assets.font.drawText(batcher, "MEDIUM", mediumBounds.lowerLeft.x + mediumBounds.width / 2,
                mediumBounds.lowerLeft.y + mediumBounds.height / 2);
        Assets.font.drawText(batcher, "HARD", hardBounds.lowerLeft.x + hardBounds.width / 2,
                hardBounds.lowerLeft.y + hardBounds.height / 2);

        batcher.endBatch();
    }

    @Override
    public void update(float deltaTime) {
        game.getInput().getKeyEvents();
        List<TouchEvent> events = game.getInput().getTouchEvents();
        elapsed += deltaTime;

        updateReady(events, deltaTime);
    }

    @Override
    public void draw(float deltaTime) {
        GL10 gl = glGraphics.getGL();
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);               //Specifies which buffer to clear
        gl.glClearColor(0, 0, 0, 1);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        camera.setViewportAndMatrices();    //640 : 360
        drawReady();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
    }
}
