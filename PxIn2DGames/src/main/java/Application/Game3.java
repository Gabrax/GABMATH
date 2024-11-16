package Application;

import com.raylib.Jaylib;
import static com.raylib.Jaylib.*;

public class Game3 {

    static final float G = 50.0f;
    static final float dt = 1.0f; // Time step

    static float mass1 = 10.0f;
    static float mass2 = 10.0f;

    static Jaylib.Vector2 centerOfMass = new Jaylib.Vector2(400, 300);

    // Initial positions of the bodies around the center of mass
    static Jaylib.Vector2 pos1 = new Jaylib.Vector2(centerOfMass.x() - 100, centerOfMass.y());
    static Jaylib.Vector2 pos2 = new Jaylib.Vector2(centerOfMass.x() + 100, centerOfMass.y());

    // Calculate distance between bodies
    static float distance = distance(pos1, pos2);

    // EXPERIMENTAL //
    //static float forceMagnitude = (G * mass1 * mass2) / (distance * distance);
    //static final float G_calculated = (forceMagnitude * distance) / (mass1 * mass2);
    // EXPERIMENTAL //

    // Calculate initial velocity for stable orbit
    static float orbitalVelocity = (float) Math.sqrt(G * mass1 / distance / 2);

    // Set initial velocities perpendicular to the line connecting the two bodies
    static Jaylib.Vector2 vel1 = new Jaylib.Vector2(0, orbitalVelocity);  // Body1 moves upward
    static Jaylib.Vector2 vel2 = new Jaylib.Vector2(0, -orbitalVelocity); // Body2 moves downward

    public static void renderGame3() {
        // Update positions and velocities using Runge-Kutta 4th
        Jaylib.Vector2[] rk4_1 = rungeKutta4(pos1, vel1, mass1, pos2, mass2, dt);
        Jaylib.Vector2[] rk4_2 = rungeKutta4(pos2, vel2, mass2, pos1, mass1, dt);

        // Update the positions and velocities with RK4
        pos1 = rk4_1[0];
        vel1 = rk4_1[1];
        pos2 = rk4_2[0];
        vel2 = rk4_2[1];


        ClearBackground(RAYWHITE);
        DrawCircleV(pos1, 10, BLUE);
        DrawCircleV(pos2, 10, RED);
        DrawLineV(pos1, pos2, DARKGRAY);

        float kineticEnergy1 = 0.5f * mass1 * (vel1.x() * vel1.x() + vel1.y() * vel1.y());
        float kineticEnergy2 = 0.5f * mass2 * (vel2.x() * vel2.x() + vel2.y() * vel2.y());
        float potentialEnergy = -G * mass1 * mass2 / distance(pos1, pos2);
        float totalEnergy = kineticEnergy1 + kineticEnergy2 + potentialEnergy;

        DrawText("kineticEnergy1: " + kineticEnergy1, 10, 30, 20, BLACK);
        DrawText("kineticEnergy2: " + kineticEnergy2, 10, 50, 20, BLACK);
        DrawText("potentialEnergy: " + potentialEnergy, 10, 70, 20, BLACK);
        DrawText("totalEnergy: " + totalEnergy, 10, 90, 20, BLACK);
        DrawText("CTRL to return, R to reset positions", 10, GetScreenHeight() - 25, 20, BLACK);

        if (IsKeyPressed(KEY_R)) {
            pos1 = new Jaylib.Vector2(centerOfMass.x() - 100, centerOfMass.y());
            pos2 = new Jaylib.Vector2(centerOfMass.x() + 100, centerOfMass.y());
            vel1 = new Jaylib.Vector2(0, orbitalVelocity);
            vel2 = new Jaylib.Vector2(0, -orbitalVelocity);
        }
    }

    // Runge-Kutta 4th order method for position and velocity update
    public static Jaylib.Vector2[] rungeKutta4(Jaylib.Vector2 pos, Jaylib.Vector2 vel, float mass, Jaylib.Vector2 otherPos, float otherMass, float dt) {
        // Calculate initial force and acceleration
        Jaylib.Vector2 force = calculateGravitationalForce(pos, otherPos, mass, otherMass);
        Jaylib.Vector2 acc = new Jaylib.Vector2(force.x() / mass, force.y() / mass);

        // Calculate k1, k2, k3, k4 for position and velocity
        Jaylib.Vector2 k1_pos = multiply(vel, dt);
        Jaylib.Vector2 k1_vel = multiply(acc, dt);

        Jaylib.Vector2 k2_pos = multiply(add(vel, multiply(k1_vel, 0.5f)), dt);
        Jaylib.Vector2 k2_vel = multiply(calculateGravitationalForce(add(pos, multiply(k1_pos, 0.5f)), otherPos, mass, otherMass), dt / mass);

        Jaylib.Vector2 k3_pos = multiply(add(vel, multiply(k2_vel, 0.5f)), dt);
        Jaylib.Vector2 k3_vel = multiply(calculateGravitationalForce(add(pos, multiply(k2_pos, 0.5f)), otherPos, mass, otherMass), dt / mass);

        Jaylib.Vector2 k4_pos = multiply(add(vel, k3_vel), dt);
        Jaylib.Vector2 k4_vel = multiply(calculateGravitationalForce(add(pos, k3_pos), otherPos, mass, otherMass), dt / mass);

        // Calculate the new position and velocity
        Jaylib.Vector2 newPos = add(pos, multiply(add(add(k1_pos, multiply(k2_pos, 2)), add(multiply(k3_pos, 2), k4_pos)), 1.0f / 6.0f));
        Jaylib.Vector2 newVel = add(vel, multiply(add(add(k1_vel, multiply(k2_vel, 2)), add(multiply(k3_vel, 2), k4_vel)), 1.0f / 6.0f));

        return new Jaylib.Vector2[]{newPos, newVel};
    }

    // Calculate the gravitational force between two bodies
    public static Jaylib.Vector2 calculateGravitationalForce(Jaylib.Vector2 pos1, Jaylib.Vector2 pos2, float m1, float m2) {
        float dist = distance(pos1, pos2);
        float forceMagnitude = (G * m1 * m2) / (dist * dist);

        Jaylib.Vector2 direction = normalize(subtract(pos2, pos1));

        // Apply force magnitude to direction
        return new Jaylib.Vector2(direction.x() * forceMagnitude, direction.y() * forceMagnitude);
    }

    public static float distance(Jaylib.Vector2 v1, Jaylib.Vector2 v2) {
        return (float) Math.sqrt(Math.pow(v2.x() - v1.x(), 2) + Math.pow(v2.y() - v1.y(), 2));
    }

    public static Jaylib.Vector2 add(Jaylib.Vector2 v1, Jaylib.Vector2 v2) {
        return new Jaylib.Vector2(v1.x() + v2.x(), v1.y() + v2.y());
    }

    public static Jaylib.Vector2 subtract(Jaylib.Vector2 v1, Jaylib.Vector2 v2) {
        return new Jaylib.Vector2(v1.x() - v2.x(), v1.y() - v2.y());
    }

    public static Jaylib.Vector2 multiply(Jaylib.Vector2 v, float scalar) {
        return new Jaylib.Vector2(v.x() * scalar, v.y() * scalar);
    }

    public static Jaylib.Vector2 normalize(Jaylib.Vector2 v) {
        float length = (float) Math.sqrt(v.x() * v.x() + v.y() * v.y());
        return new Jaylib.Vector2(v.x() / length, v.y() / length);
    }
}
