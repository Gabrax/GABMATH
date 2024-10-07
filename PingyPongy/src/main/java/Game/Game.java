package Game;

import com.raylib.Jaylib;
import static com.raylib.Raylib.*;
import static com.raylib.Jaylib.*;

public class Game {
    public static class Resources {

        public static Image ball;
        public static Texture ballTex;

        static void LoadResources() {
            ball = LoadImage("resources/ball.png");
            ballTex = LoadTextureFromImage(ball);
            UnloadImage(ball);
        }
    }

    public static class MusicPlayer {

        static Music music;
        static Sound hitPoint;

        public static void Init() {
            InitAudioDevice();
            music = LoadMusicStream("resources/music.mp3");
            PlayMusicStream(music);
            SetMusicVolume(music, 0.25f);
            hitPoint = LoadSound("resources/hit.wav");
        }

        public static void Update() {
            UpdateMusicStream(music);
        }
    }

    public static class Window {

        static int Height = 600;
        static int Width = 1000;
        public static boolean pause = false;

        public static void init() {
            SetConfigFlags(FLAG_WINDOW_RESIZABLE | FLAG_VSYNC_HINT | FLAG_MSAA_4X_HINT);
            InitWindow(Width, Height, "hello");
            Resources.LoadResources();
            SetTargetFPS(60);
            DisableCursor();
        }

        public static void resize() {

            if (IsKeyPressed(KEY_F)) {
                int display = GetCurrentMonitor();

                if (IsWindowFullscreen()) {
                    SetWindowSize(GetMonitorWidth(display), GetMonitorHeight(display));
                } else {
                    SetWindowSize(Width, Height);
                }
                ToggleBorderlessWindowed();
            }
        }
    }

    public static class Player1 {

        static Jaylib.Vector2 pos = new Jaylib.Vector2(50.0f, Window.Height - 250.0f);
        static Jaylib.Vector2 initPos = new Jaylib.Vector2(pos.x(),pos.y());
        static Jaylib.Vector2 size = new Jaylib.Vector2(20.0f, 200.0f);
        static float velocity = 500;

        public static Jaylib.Rectangle getPlayer1() {
            return new Jaylib.Rectangle(pos.x() - size.x()/2, pos.y() - size.y()/2, 10, 100);
        }

        public static void movePlayer() {

            if (IsKeyDown(KEY_W)) {
                pos.y(pos.y() - velocity * GetFrameTime());
                velocity = IsKeyDown(KEY_LEFT_SHIFT) ? 700 : 500;
            }
            if (IsKeyDown(KEY_S)) {
                pos.y(pos.y() + velocity * GetFrameTime());
                velocity = IsKeyDown(KEY_LEFT_SHIFT) ? 700 : 500;
            }
        }
    }

    public static class Player2 {

        static Jaylib.Vector2 pos = new Jaylib.Vector2(Window.Width - 45.0f, Window.Height - 250.0f);
        static Jaylib.Vector2 initPos = new Jaylib.Vector2(pos.x(),pos.y());
        static Jaylib.Vector2 size = new Jaylib.Vector2(20.0f, 200.0f);
        static boolean isMov = true;
        static float velocity = 500;

        public static Jaylib.Rectangle getPlayer2() {
            return new Jaylib.Rectangle(pos.x() - size.x()/2, pos.y() - size.y()/2, 10, 100);
        }

        public static void movePlayer() {

            if (IsKeyDown(KEY_UP)) {
                pos.y(pos.y() - velocity * GetFrameTime());
                velocity = IsKeyDown(KEY_RIGHT_CONTROL) ? 700 : 500;
            }
            if (IsKeyDown(KEY_DOWN)) {
                pos.y(pos.y() + velocity * GetFrameTime());
                velocity = IsKeyDown(KEY_RIGHT_CONTROL) ? 700 : 500;
            }
        }

        static void AI() {

            if (isMov) {
                pos.y(pos.y() + 0.1f);
                if (pos.y() >= 5) {
                    isMov = false;
                } else {
                    pos.y(pos.y() - 0.1f);
                    if (pos.y() <= 0) {
                        isMov = true;
                    }
                }
            }
        }
    }

    public static class Ball {

        public static Jaylib.Vector2 pos = new Jaylib.Vector2(Window.Width / 2.0f, Window.Height / 2.0f);
        static Jaylib.Vector2 initPos = new Jaylib.Vector2(pos.x(),pos.y());
        public static float radius = 20.0f;
        static float velocityX = 300.0f;
        static float velocityY = 300.0f;
        static float rotation = 0.0f;

        public static void draw() {
            DrawTexturePro(Resources.ballTex,
                    new Jaylib.Rectangle(0, 0, Resources.ballTex.width(), Resources.ballTex.height()),
                    new Jaylib.Rectangle(pos.x(), pos.y(), radius * 2, radius * 2),
                    new Jaylib.Vector2(radius, radius), rotation, RAYWHITE);
        }

        public static void letBounce() {

            pos.x(pos.x() + velocityX * GetFrameTime());
            pos.y(pos.y() + velocityY * GetFrameTime());

            float speed = (float) Math.sqrt((velocityX * velocityX) + (velocityY * velocityY));  // Calculate the total speed
            rotation += speed * GetFrameTime();

            if (rotation >= 360.0f) {
                rotation -= 360.0f;
            }

            if (pos.y() < 0) {
                pos.y(0);
                velocityY *= -1.0f;
                PlaySound(MusicPlayer.hitPoint);
            }

            if (pos.y() > GetScreenHeight()) {
                pos.y(GetScreenHeight());
                velocityY *= -1.0f;
                PlaySound(MusicPlayer.hitPoint);
            }

            if (CheckCollisionCircleRec(pos, radius, Player1.getPlayer1())) {
                if (velocityX < 0) {
                    velocityX *= -1.1f;
                    velocityY = (pos.y() - Player1.pos.y()) / (Player1.size.y() / 2) * velocityX;
                }
                PlaySound(MusicPlayer.hitPoint);
            }

            if (CheckCollisionCircleRec(pos, radius, Player2.getPlayer2())) {
                if (velocityX > 0) {
                    velocityX *= -1.1f;
                    velocityY = (pos.y() - Player2.pos.y()) / (Player2.size.y() / 2) * -velocityX;
                }
                PlaySound(MusicPlayer.hitPoint);
            }
        }
    }

    public static void Reset() {
        Player1.pos = new Jaylib.Vector2(Player1.initPos.x(),Player1.initPos.y());
        Player2.pos = new Jaylib.Vector2(Player2.initPos.x(),Player2.initPos.y());
        Ball.pos = new Jaylib.Vector2(Ball.initPos.x(),Ball.initPos.y());
        Ball.velocityX = 300.0f;
        Ball.velocityY = 300.0f;
        Ball.rotation = 0.0f;
    }

    public static class ScoreBoard {

        public static int p1 = 0;
        public static int p2 = 0;
        static boolean p1Wins = false;
        static boolean p2Wins = false;

        public static void UpdateBoard() {
            if (Ball.pos.x() < 0) {
                p2++;
                Reset();
                if (p2 == 3) p2Wins = true;
            }
            if (Ball.pos.x() > GetScreenWidth()) {
                p1++;
                Reset();
                if (p1 == 3) p1Wins = true;
            }
            if (p1Wins || p2Wins) {
                Reset();
                p1 = 0;
                p2 = 0;
                p1Wins = false;
                p2Wins = false;
            }
        }
    }



    public static String DEBUG(String str, float arg1, float arg2){

        int re_arg1 = (int)arg1;
        int re_arg2 = (int)arg2;

        return String.format("%s (%d, %d)",str,re_arg1,re_arg2);
    }
}
