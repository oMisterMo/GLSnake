package com.ds.mo.engine.glsnake;

import android.content.Context;
import android.util.Log;

import com.ds.mo.engine.android.GLGame;
import com.ds.mo.engine.android.Texture;
import com.ds.mo.engine.android.TextureRegion;
import com.ds.mo.engine.common.Font;
import com.ds.mo.engine.framework.Music;
import com.ds.mo.engine.framework.Sound;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Pac-man assets
 * <p/>
 * Created by Mo on 31/05/2017.
 */
public class Assets {
    public static Texture textureAtlas;

    //------------------------------------------------------------------------

    //Primitive shapes
    public static TextureRegion fillRect;
    public static TextureRegion fillCircle;
    public static TextureRegion drawRect;
    public static TextureRegion drawRectFull;
    public static TextureRegion drawCircle;

    public static TextureRegion tile;
    public static TextureRegion food;

    //Game music
    public static Music music;
    public static Sound waka;

    //Game font
//    public static Font numbers;
    public static Font font;

    //World file
    public static JSONObject pacWorld;
    public static JSONObject pacIntersection;

    public static void load(GLGame glGame) {
        Log.d("Assets", "Loading assets...");

        //Load Images
        textureAtlas = new Texture(glGame, "textureAtlas.png");
        fillRect = new TextureRegion(textureAtlas, 32 * 15, 0, 32, 32);
        drawRect = new TextureRegion(textureAtlas, 32 * 14, 0, 32, 32);
        drawRectFull = new TextureRegion(textureAtlas, 32 * 13, 0, 32, 32);
        fillCircle = new TextureRegion(textureAtlas, 32 * 15, 32, 32, 32);
        drawCircle = new TextureRegion(textureAtlas, 32 * 14, 32, 32, 32);

        tile = new TextureRegion(textureAtlas, 0, 0, 32, 32);
        food = new TextureRegion(textureAtlas, 0, 32 * 1, 32, 32);

        //Load font (16 * 16)
        font = new Font(textureAtlas, 0, 32 * 4, 8, 16, 16);

        //Load world map


        //Load SFX
//        waka = glGame.getAudio().newSound("sfx/waka_waka.wav");

        //Load Audio
        music = glGame.getAudio().newMusic("music.mp3");
        music.setLooping(true);
        if (Settings.soundEnabled) {
            music.play();
        }
    }

    /**
     * Reloads all Textures and plays music when app resumes
     */
    public static void reload() {
        //Reload textures
        textureAtlas.reload();

        //------------------------------------------------------------
        //Play background music
        if (Settings.soundEnabled) {
            music.play();
        }
    }

    public static void playSound(Sound sound) {
        if (Settings.soundEnabled) {
            sound.play(1);
        }
    }

    private static String loadJson(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
}
