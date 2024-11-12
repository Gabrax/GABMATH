package Application;

import com.raylib.Jaylib;
import static com.raylib.Jaylib.*;

public class Game2 {
    static Jaylib.Vector2 pos1 = new Jaylib.Vector2(200, 300);
    static Jaylib.Vector2 pos2 = new Jaylib.Vector2(400, 300);
    static Jaylib.Vector2 velocity1 = new Jaylib.Vector2(100, 0);
    static Jaylib.Vector2 velocity2 = new Jaylib.Vector2(-50, 0);
    static float radius = 20; // Radius of both circles
    static float mass1 = 1.0f;
    static float mass2 = 1.0f;
    static float dt = GetFrameTime();

    public static void renderGame2() {

        if (isColliding(pos1, pos2, radius)) {
            resolveCollision(pos1, pos2, velocity1, velocity2, mass1, mass2);
        }

        pos1.x(pos1.x() + velocity1.x() * dt);
        pos1.y(pos1.y() + velocity1.y() * dt);
        pos2.x(pos2.x() + velocity2.x() * dt);
        pos2.y(pos2.y() + velocity2.y() * dt);

        ClearBackground(RAYWHITE);

        // Draw circles
        DrawCircleV(pos1, radius, BLUE);
        DrawCircleV(pos2, radius, RED);


        DrawText("vx: " + velocity1.x() + ", vy: " + velocity1.y(), 10, 10, 20, BLACK);
        DrawText("vx: " + velocity2.x() + ", vy: " + velocity2.y(), 10, 40, 20, BLACK);
    }

    // Detect if two circles are colliding
    public static boolean isColliding(Jaylib.Vector2 pos1, Jaylib.Vector2 pos2, float radius) {
        return distance(pos1, pos2) < 2 * radius;
    }

    public static void resolveCollision(Jaylib.Vector2 pos1, Jaylib.Vector2 pos2, Jaylib.Vector2 v1, Jaylib.Vector2 v2, float m1, float m2) {
        Jaylib.Vector2 collisionNormal = normalize(subtract(pos2, pos1));

        // Project velocities onto the collision normal
        float v1Normal = dotProduct(v1, collisionNormal);
        float v2Normal = dotProduct(v2, collisionNormal);

        // Calculate new velocities along the normal
        float v1New = ((v1Normal * (m1 - m2)) + (2 * m2 * v2Normal)) / (m1 + m2);
        float v2New = ((v2Normal * (m2 - m1)) + (2 * m1 * v1Normal)) / (m1 + m2);

        // Adjust velocities based on the new normal components
        v1.x(collisionNormal.x() * v1New + v1.x() - collisionNormal.x() * v1Normal);
        v1.y(collisionNormal.y() * v1New + v1.y() - collisionNormal.y() * v1Normal);
        v2.x(collisionNormal.x() * v2New + v2.x() - collisionNormal.x() * v2Normal);
        v2.y(collisionNormal.y() * v2New + v2.y() - collisionNormal.y() * v2Normal);

        // Adjust positions to prevent overlap after collision
        float overlap = (2 * radius) - distance(pos1, pos2);
        pos1.x(pos1.x() - collisionNormal.x() * overlap * 0.5f);
        pos1.y(pos1.y() - collisionNormal.y() * overlap * 0.5f);
        pos2.x(pos2.x() + collisionNormal.x() * overlap * 0.5f);
        pos2.y(pos2.y() + collisionNormal.y() * overlap * 0.5f);
    }

    public static float distance(Jaylib.Vector2 v1, Jaylib.Vector2 v2) {
        return (float) Math.sqrt(Math.pow(v2.x() - v1.x(), 2) + Math.pow(v2.y() - v1.y(), 2));
    }

    public static Jaylib.Vector2 subtract(Jaylib.Vector2 v1, Jaylib.Vector2 v2) {
        return new Jaylib.Vector2(v1.x() - v2.x(), v1.y() - v2.y());
    }

    public static float dotProduct(Jaylib.Vector2 v1, Jaylib.Vector2 v2) {
        return v1.x() * v2.x() + v1.y() * v2.y();
    }

    public static Jaylib.Vector2 normalize(Jaylib.Vector2 v) {
        float length = (float) Math.sqrt(v.x() * v.x() + v.y() * v.y());
        return new Jaylib.Vector2(v.x() / length, v.y() / length);
    }
}
