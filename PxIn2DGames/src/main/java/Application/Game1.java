package Application;

import com.raylib.Jaylib;
import static com.raylib.Jaylib.*;

public class Game1 {
    static Jaylib.Vector2 pos = new Jaylib.Vector2(400, 300);
    static Jaylib.Vector2 velocity = new Jaylib.Vector2(300, 300);
    static float dt = GetFrameTime(); // DeltaTime

    static float gravity = 50.0f;
    static Jaylib.Rectangle targetArea = new Jaylib.Rectangle(400, 400, 100, 100);
    static float targetSpeedThreshold = 2;


        public static void renderGame1() {

            // Update position using Euler's method
            if (IsKeyDown(KEY_RIGHT)){
                pos.x(pos.x() + velocity.x() * GetFrameTime());
            }
            if (IsKeyDown(KEY_LEFT)){
                pos.x(pos.x() - velocity.x() * GetFrameTime());
            }
            if (IsKeyDown(KEY_UP)) {
                pos.y(pos.y() - velocity.y() * GetFrameTime());
            }
            if (IsKeyDown(KEY_DOWN)){
                pos.y(pos.y() + velocity.y() * GetFrameTime());
            }

            // Apply gravity
            pos.y(pos.y() + gravity * dt);

            // Check if the shape is in the target area
            if (pos.x() >= targetArea.x() && pos.x() <= targetArea.x() + targetArea.width() &&
                    pos.y() >= targetArea.y() && pos.y() <= targetArea.y() + targetArea.height()) {


                if (Math.abs(velocity.x()) > targetSpeedThreshold) velocity.x(velocity.x() * 0.4f);
                if (Math.abs(velocity.y()) > targetSpeedThreshold) velocity.y(velocity.y() * 0.4f);

                pos.y(pos.y() + gravity * 0.2f * dt);
            } else {
                // Outside target area, apply full gravity and reset velocity
                pos.y(pos.y() + gravity * dt);
                velocity.x(300.0f);
                velocity.y(300.0f);
            }

            ClearBackground(RAYWHITE);

            // Draw the target area
            DrawRectangleRec(targetArea, GREEN);
            // Draw the shape (a circle in this case)
            DrawCircle((int) pos.x(), (int) pos.y(), 20, BLUE);



            DrawText("vx: " + velocity.x() + ", vy: " + velocity.y(), 10, 10, 20, BLACK);
            DrawText("x: " + pos.x() + ", y: " + pos.y(), 10, 40, 20, BLACK);


        }
}

