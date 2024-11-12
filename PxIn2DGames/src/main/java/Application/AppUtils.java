package Application;

import com.raylib.Raylib;

import java.io.File;

import static com.raylib.Raylib.*;
import static com.raylib.Raylib.UpdateMusicStream;

public class AppUtils {
    public static class Resources {

        private static Texture ballTex;

        static void LoadResources() {
            String path = new File("resources/debugball.png").getAbsolutePath();
            Image ball = LoadImage(path);
            ballTex = LoadTextureFromImage(ball);
            UnloadImage(ball);
        }

        // Method to get a resource by name
        public static Texture getResource(String name) {
            switch (name.toLowerCase()) {
                case "balltex":
                    return ballTex;
                default:
                    throw new IllegalArgumentException("Resource not found: " + name);
            }
        }
    }

    public static class MusicPlayer {

        private static Raylib.Music music;
        private static Raylib.Sound hitPoint;

        public static void Init() {
            InitAudioDevice();
            //music = LoadMusicStream("resources/music.mp3");
            //PlayMusicStream(music);
            //SetMusicVolume(music, 0.10f);
            hitPoint = LoadSound("resources/hit.wav");
        }

        public static void playMusic(Music music){
            PlayMusicStream(music);
        }

        public static void Update() {
            UpdateMusicStream(music);
        }

        public static Music getMusic(String name) {
            switch (name.toLowerCase()) {
                case "music":
                    return music;
                default:
                    throw new IllegalArgumentException("Audio not found: " + name);
            }
        }

        public static Sound getSound(String name) {
            switch (name.toLowerCase()) {
                case "hitpoint":
                    return hitPoint;
                default:
                    throw new IllegalArgumentException("Audio not found: " + name);
            }
        }
    }

    public static class Window {

        static int _height = 600;
        static int _width = 1000;
        public static boolean pause = false;

        public static void init() {
            SetConfigFlags(FLAG_VSYNC_HINT | FLAG_MSAA_4X_HINT);
            InitWindow(_width, _height, "PxInGames");
            Resources.LoadResources();
            MusicPlayer.Init();
            //SetTargetFPS(60);
            DisableCursor();
        }

        public static int getWidth() {
            return _width;
        }

        public static int getHeight() {
            return _height;
        }
    }

}
