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
            String path = new File("resources/debugball.png").getAbsolutePath();
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
        static float friction = 0.1f;
        public static float airResistance = 0.00000001f;
        public static float magnus = 0.001f;
        static boolean hasCollided = false;
        static float speed = (float) Math.sqrt((velocityX * velocityX) + (velocityY * velocityY));

        public static float gravity = 500.0f;
        public static float bounceDampening = 0.7f;
        public static boolean isGravityEnabled = false;
        static float timeOnGround = 0.0f;
        public static boolean isSunEnabled = false;
        static boolean initialOrbitVelocitySet = false;

        public static void draw() {
            DrawTexturePro(Resources.ballTex,
                    new Jaylib.Rectangle(0, 0, Resources.ballTex.width(), Resources.ballTex.height()),
                    new Jaylib.Rectangle(pos.x(), pos.y(), radius * 2, radius * 2),
                    new Jaylib.Vector2(radius, radius), rotation, RAYWHITE);
        }

        static boolean CheckCollision(Jaylib.Vector2 center, float radius, Jaylib.Rectangle rec) {
            boolean collision;

            float recCenterX = rec.x() + rec.width()/2.0f;
            float recCenterY = rec.y() + rec.height()/2.0f;

            float dx = Math.abs(center.x() - recCenterX);
            float dy = Math.abs(center.y() - recCenterY);

            if (dx > (rec.width()/2.0f + radius)) { return false; }
            if (dy > (rec.height()/2.0f + radius)) { return false; }

            if (dx <= (rec.width()/2.0f)) { return true; }
            if (dy <= (rec.height()/2.0f)) { return true; }

            float cornerDistanceSq = (dx - rec.width()/2.0f)*(dx - rec.width()/2.0f) +
                    (dy - rec.height()/2.0f)*(dy - rec.height()/2.0f);

            collision = (cornerDistanceSq <= (radius*radius));

            return collision;
        }

        public static void letBounce() {

            if (isGravityEnabled) {
                velocityY += gravity * GetFrameTime();
            }

            pos.x(pos.x() + velocityX * GetFrameTime());
            pos.y(pos.y() + velocityY * GetFrameTime());

            // Update rotation based on friction
            if (hasCollided) {
                rotation += (speed * friction) * GetFrameTime();
                if (rotation >= 360.0f) rotation -= 360.0f;
            }

            if (isSunEnabled) {
                if (!initialOrbitVelocitySet) {
                    // Set initial tangential velocity for orbiting effect
                    velocityX = 200.0f; // Adjust as needed
                    initialOrbitVelocitySet = true;
                }
                Sun.GravityPull();
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

            // Collision with window boundaries
            if (pos.y() < 0) {
                pos.y(0);
                velocityY *= -1.0f;
                PlaySound(MusicPlayer.hitPoint);
            }

            if (pos.y() + radius >= GetScreenHeight()) {
                pos.y(GetScreenHeight() - radius); // Ensure ball stays at ground level

                if (Math.abs(velocityY) > 1.0f) { // Ensure sufficient velocity for bounce
                    velocityY *= -bounceDampening; // Reverse velocity and apply dampening
                    // Gradually reduce horizontal speed due to friction
                    velocityX *= 0.9f;
                } else {
                    velocityY = 0.0f; // Stop small bounces that may cause sticking
                }
                PlaySound(MusicPlayer.hitPoint);
            }

            //System.out.println(timeOnGround);
            // Collision with Player1
            if (CheckCollision(pos, radius, Player1.getPlayer1())) {
                if (velocityX < 0) {
                    velocityX *= -1.1f; // Bounce back with increased speed
                    velocityY = (pos.y() - Player1.pos.y()) / (Player1.size.y() / 2) * velocityX;

                    // Apply friction to reduce velocity slightly upon collision
                    velocityX *= (1.0f - friction);
                    velocityY *= (1.0f - friction);

                    // Mark that collision has occurred
                    hasCollided = true;
                }
                PlaySound(MusicPlayer.hitPoint);
            }

            // Collision with Player2
            if (CheckCollision(pos, radius, Player2.getPlayer2())) {
                if (velocityX > 0) {
                    velocityX *= -1.1f; // Bounce back with increased speed
                    velocityY = (pos.y() - Player2.pos.y()) / (Player2.size.y() / 2) * -velocityX;

                    // Apply friction to reduce velocity slightly upon collision
                    velocityX *= (1.0f - friction);
                    velocityY *= (1.0f - friction);

                    // Mark that collision has occurred
                    hasCollided = true;
                }
                PlaySound(MusicPlayer.hitPoint);
            }

            if(IsKeyPressed(KEY_T)) airResistance += 0.10f;
            else if (IsKeyPressed(KEY_Y)) airResistance -= 0.10f;

            if(IsKeyPressed(KEY_G)) magnus += 0.001f;
            else if (IsKeyPressed(KEY_H)) magnus -= 0.001f;

            if(IsKeyPressed(KEY_X)) isGravityEnabled = !isGravityEnabled;
            if(IsKeyPressed(KEY_Z)) isSunEnabled = !isSunEnabled;
        }

        public static float speed() {
            return speed * 3.6f;
        }
    }

    public static class Sun {
        public static final Jaylib.Vector2 sunPos = new Jaylib.Vector2(Window.Width / 2.0f, Window.Height / 2.0f);
        private static final float gravitationalConstant = 200000000.0f;

        public static void draw() {
            DrawCircle((int) sunPos.x(), (int) sunPos.y(), 30, GOLD);
        }

        public static void GravityPull() {
            // Calculate the direction vector from Ball to Sun
            float dx = sunPos.x() - Ball.pos.x();
            float dy = sunPos.y() - Ball.pos.y();

            // Calculate the distance between Ball and Sun
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            // Prevent division by zero if the Ball gets too close to the Sun
            if (distance < 1.0f) distance = 1.0f;

            // Normalize the direction vector to point towards the Sun
            float directionX = dx / distance;
            float directionY = dy / distance;

            // Calculate the gravitational force using the inverse square law
            // Increase the gravitational constant for stronger pull
            float force = gravitationalConstant / (distance * distance);

            // Calculate the acceleration in the direction of the Sun
            float accelerationX = force * directionX;
            float accelerationY = force * directionY;

            // Apply the calculated acceleration to the Ball's velocity
            Ball.velocityX += accelerationX * GetFrameTime();
            Ball.velocityY += accelerationY * GetFrameTime();

            // Limit the maximum speed to prevent it from instantly zooming towards the sun
            float maxSpeed = 400.0f; // You can adjust this value
            float currentSpeed = (float) Math.sqrt(Ball.velocityX * Ball.velocityX + Ball.velocityY * Ball.velocityY);
            if (currentSpeed > maxSpeed) {
                Ball.velocityX *= maxSpeed / currentSpeed;
                Ball.velocityY *= maxSpeed / currentSpeed;
            }

            // Update the Ball's position based on its velocity
            Ball.pos.x(Ball.pos.x() + Ball.velocityX * GetFrameTime());
            Ball.pos.y(Ball.pos.y() + Ball.velocityY * GetFrameTime());
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
        Ball.isGravityEnabled = false;
        Ball.isSunEnabled = false;
    }


    public static String DEBUG(String str, Object... args) {
        // Create a string builder to accumulate formatted values
        StringBuilder formattedArgs = new StringBuilder();

        // Iterate through each argument and append to the string builder
        for (Object arg : args) {
            if (!formattedArgs.isEmpty()) {
                formattedArgs.append(", "); // Add a separator for multiple arguments
            }

            // Format the argument based on its type (you can customize this if needed)
            if (arg instanceof Float || arg instanceof Double) {
                formattedArgs.append(String.format("%.2f", arg)); // Limit floats/doubles to 2 decimal places
            } else {
                formattedArgs.append(arg); // Default formatting for other types
            }
        }

        // Return the formatted string with all arguments
        return String.format("%s (%s)", str, formattedArgs);
    }
}