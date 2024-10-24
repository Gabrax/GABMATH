package Game;

import com.raylib.Jaylib;
import org.bytedeco.javacpp.FloatPointer;

import java.io.File;

import static com.raylib.Raylib.*;
import static com.raylib.Jaylib.*;

public class Game {
    public static class Resources {

        public static Image ball;
        public static Texture ballTex;

        static void LoadResources() {
            String path = new File("resources/ball.png").getAbsolutePath();
            ball = LoadImage(path);
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
            SetMusicVolume(music, 0.10f);
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
        public static boolean isResized = false;

        public static void init() {
            SetConfigFlags(FLAG_WINDOW_RESIZABLE | FLAG_VSYNC_HINT | FLAG_MSAA_4X_HINT);
            InitWindow(Width, Height, "Pingy Pongy");
            Resources.LoadResources();
            SetTargetFPS(60);
            DisableCursor();
        }
    }

    public static class Player1 {

        static Jaylib.Vector2 pos = new Jaylib.Vector2(50.0f, Window.Height - 250.0f);
        static Jaylib.Vector2 initPos = new Jaylib.Vector2(pos.x(), pos.y());
        static Jaylib.Vector2 res_Pos = new Jaylib.Vector2((float) Window.Width - 750.0f, (float) Window.Height + 10.0f);
        static Jaylib.Vector2 size = new Jaylib.Vector2(20.0f, 200.0f);
        static float velocity = 500;

        public static Jaylib.Rectangle getPlayer1() {
            return new Jaylib.Rectangle(pos.x() - size.x() / 2, pos.y() - size.y() / 2, 10, 100);
        }

        public static void updatePosition() {
            if (Window.isResized) {
                pos = new Jaylib.Vector2(res_Pos.x(), res_Pos.y());
            } else {
                pos = new Jaylib.Vector2(initPos.x(), initPos.y());
            }
        }

        public static void movePlayer() {
            if (IsKeyDown(KEY_W)) {
                pos.y(pos.y() - velocity * GetFrameTime());
                velocity = IsKeyDown(KEY_LEFT_SHIFT) ? 900 : 500;
            }
            if (IsKeyDown(KEY_S)) {
                pos.y(pos.y() + velocity * GetFrameTime());
                velocity = IsKeyDown(KEY_LEFT_SHIFT) ? 900 : 500;
            }
        }
    }

    public static class Player2 {

        static Jaylib.Vector2 pos = new Jaylib.Vector2(Window.Width - 45.0f, Window.Height - 250.0f);
        static Jaylib.Vector2 initPos = new Jaylib.Vector2(pos.x(),pos.y());
        static Jaylib.Vector2 res_Pos = new Jaylib.Vector2((float) Window.Width + 700.0f, (float) Window.Height + 10.0f);
        static Jaylib.Vector2 size = new Jaylib.Vector2(20.0f, 200.0f);
        static boolean isMov = true;
        static float velocity = 500;

        public static Jaylib.Rectangle getPlayer2() {
            return new Jaylib.Rectangle(pos.x() - size.x()/2, pos.y() - size.y()/2, 10, 100);
        }

        public static void updatePosition() {
            if (Window.isResized) {
                pos = new Jaylib.Vector2(res_Pos.x(), res_Pos.y());
            } else {
                pos = new Jaylib.Vector2(initPos.x(), initPos.y());
            }
        }

        public static void movePlayer() {

            if (IsKeyDown(KEY_UP)) {
                pos.y(pos.y() - velocity * GetFrameTime());
                velocity = IsKeyDown(KEY_RIGHT_CONTROL) ? 900 : 500;
            }
            if (IsKeyDown(KEY_DOWN)) {
                pos.y(pos.y() + velocity * GetFrameTime());
                velocity = IsKeyDown(KEY_RIGHT_CONTROL) ? 900 : 500;
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
        static Jaylib.Vector2 initPos = new Jaylib.Vector2(pos.x(), pos.y());
        public static float radius = 20.0f;
        public static float velocityX = 300.0f;
        public static float velocityY = 300.0f;
        static float rotation = 0.0f;
        static float gravity = 20.0f;
        static float friction = 0.5f;
        public static float airResistance = 0.00000001f;
        public static float magnus = 0.001f;
        static boolean hasCollided = false;
        static float speed = (float) Math.sqrt((velocityX * velocityX) + (velocityY * velocityY));

        public static void draw() {
            DrawTexturePro(Resources.ballTex,
                    new Jaylib.Rectangle(0, 0, Resources.ballTex.width(), Resources.ballTex.height()),
                    new Jaylib.Rectangle(pos.x(), pos.y(), radius * 2, radius * 2),
                    new Jaylib.Vector2(radius, radius), rotation, RAYWHITE);
        }

        public static boolean checkCollision(Jaylib.Vector2 circle, float radius, Jaylib.Rectangle rec) {
            // Find the closest point to the circle within the rectangle
            float closestX = Math.max(rec.x(), Math.min(circle.x(), rec.x() + rec.x()));
            float closestY = Math.max(rec.y(), Math.min(circle.y(), rec.y() + rec.y()));

            // Calculate the distance between the circle's center and the closest point
            float distanceX = circle.x() - closestX;
            float distanceY = circle.y() - closestY;

            // If the distance is less than the circle's radius, there is a collision
            float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
            return distanceSquared <= (radius * radius);
        }

        public static void letBounce() {
            pos.x(pos.x() + velocityX * GetFrameTime());
            pos.y(pos.y() + velocityY * GetFrameTime());

            // Calculate speed and update rotation
            if (hasCollided) {
                rotation += (speed * friction) * GetFrameTime();

                if (rotation >= 360.0f) rotation -= 360.0f;
            }

            // Apply air resistance
            velocityX -= airResistance * velocityX * GetFrameTime();
            velocityY -= airResistance * velocityY * GetFrameTime();

            // Calculate Magnus effect
            float magnusForceX = -magnus * rotation * velocityY; // Force perpendicular to velocityX
            float magnusForceY = magnus * rotation * velocityX;  // Force perpendicular to velocityY

            // Apply Magnus force to velocity
            velocityX += magnusForceX * GetFrameTime();
            velocityY += magnusForceY * GetFrameTime();

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
                    velocityX *= -1.1f; // Bounce back with increased speed
                    velocityY = (pos.y() - Player1.pos.y()) / (Player1.size.y() / 2) * velocityX;
                    applyFriction();
                }
                PlaySound(MusicPlayer.hitPoint);
            }

            if (CheckCollisionCircleRec(pos, radius, Player2.getPlayer2())) {
                if (velocityX > 0) {
                    velocityX *= -1.1f; // Bounce back with increased speed
                    velocityY = (pos.y() - Player2.pos.y()) / (Player2.size.y() / 2) * -velocityX;
                    applyFriction();
                }
                PlaySound(MusicPlayer.hitPoint);
            }

            if(IsKeyPressed(KEY_T)) airResistance += 0.10f;
            else if (IsKeyPressed(KEY_Y)) airResistance -= 0.10f;

            if(IsKeyPressed(KEY_G)) magnus += 0.001f;
            else if (IsKeyPressed(KEY_H)) magnus -= 0.001f;
        }

        private static void applyFriction() {
            float frameTime = GetFrameTime();
            velocityX -= velocityX * friction * frameTime;
            velocityY -= velocityY * friction * frameTime;
            hasCollided = true;
        }

        public static float speed() {
            return speed * 3.6f;
        }
    }

    public static class ScoreBoard {

        public static int x = 450;
        public static int y = 60;
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

        public static void updatePosition(){
            if(Window.isResized){
                x = 900;
            }else x = 450;
        }
    }

    public static void Resize() {

        if (IsKeyPressed(KEY_F)) {
            ToggleBorderlessWindowed();
            Window.isResized = !Window.isResized;
            Player1.updatePosition();
            Player2.updatePosition();
            ScoreBoard.updatePosition();
        }
    }

    public static void Reset() {

        if(Window.isResized){
            Player1.pos = new Jaylib.Vector2(Player1.res_Pos.x(),Player1.res_Pos.y());
            Player2.pos = new Jaylib.Vector2(Player2.res_Pos.x(),Player2.res_Pos.y());
        }else{
            Player1.pos = new Jaylib.Vector2(Player1.initPos.x(),Player1.initPos.y());
            Player2.pos = new Jaylib.Vector2(Player2.initPos.x(),Player2.initPos.y());
        }

        Ball.pos = new Jaylib.Vector2(Ball.initPos.x(),Ball.initPos.y());
        Ball.velocityX = 300.0f;
        Ball.velocityY = 300.0f;
        Ball.rotation = 0.0f;
    }


    public static String DEBUG(String str, float arg1, float arg2){

        int re_arg1 = (int)arg1;
        int re_arg2 = (int)arg2;

        return String.format("%s (%d, %d)",str,re_arg1,re_arg2);
    }
}
