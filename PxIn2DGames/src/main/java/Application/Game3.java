package Application;

import com.raylib.Jaylib;
import static com.raylib.Jaylib.*;

public class Game3 {

    static final float G = 10.0f;
    static final float dt = (1.0f/60.0f) * 10.0f; // Time step

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

        //rungeKutta4Simultaneous();
        UpdateEuler();

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

    public static void UpdateEuler() {
        // Calculate the gravitational force between the two objects
        Jaylib.Vector2 force12 = calculateGravitationalForce(pos1, pos2, mass1, mass2);

        // Update velocities based on the gravitational force
        vel1 = add(vel1, multiply(force12, (1.0f / mass1) * dt));  // Update velocity of object 1
        vel2 = add(vel2, multiply(subtract(new Jaylib.Vector2(0, 0), force12), (1.0f / mass2) * dt)); // Update velocity of object 2

        // Update positions based on the updated velocities
        pos1 = add(pos1, multiply(vel1, dt));
        pos2 = add(pos2, multiply(vel2, dt));
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

    public static void rungeKutta4Simultaneous() {
        // Calculate forces at the initial positions
        Jaylib.Vector2 force12 = calculateGravitationalForce(pos1, pos2, mass1, mass2);
        Jaylib.Vector2 force21 = calculateGravitationalForce(pos2, pos1, mass2, mass1);

        // k1 - Initial velocity and position updates
        Jaylib.Vector2 k1_vel1 = multiply(force12, dt / mass1);
        Jaylib.Vector2 k1_vel2 = multiply(force21, dt / mass2);
        Jaylib.Vector2 k1_pos1 = multiply(vel1, dt);
        Jaylib.Vector2 k1_pos2 = multiply(vel2, dt);

        // Midpoint for k2
        Jaylib.Vector2 midPos1_k1 = add(pos1, multiply(k1_pos1, 0.5f));
        Jaylib.Vector2 midPos2_k1 = add(pos2, multiply(k1_pos2, 0.5f));
        Jaylib.Vector2 midVel1_k1 = add(vel1, multiply(k1_vel1, 0.5f));
        Jaylib.Vector2 midVel2_k1 = add(vel2, multiply(k1_vel2, 0.5f));

        // k2 - Forces and velocity/position updates at midpoint
        Jaylib.Vector2 k2_force12 = calculateGravitationalForce(midPos1_k1, midPos2_k1, mass1, mass2);
        Jaylib.Vector2 k2_force21 = calculateGravitationalForce(midPos2_k1, midPos1_k1, mass2, mass1);
        Jaylib.Vector2 k2_vel1 = multiply(k2_force12, dt / mass1);
        Jaylib.Vector2 k2_vel2 = multiply(k2_force21, dt / mass2);
        Jaylib.Vector2 k2_pos1 = multiply(midVel1_k1, dt);
        Jaylib.Vector2 k2_pos2 = multiply(midVel2_k1, dt);

        // Midpoint for k3
        Jaylib.Vector2 midPos1_k2 = add(pos1, multiply(k2_pos1, 0.5f));
        Jaylib.Vector2 midPos2_k2 = add(pos2, multiply(k2_pos2, 0.5f));
        Jaylib.Vector2 midVel1_k2 = add(vel1, multiply(k2_vel1, 0.5f));
        Jaylib.Vector2 midVel2_k2 = add(vel2, multiply(k2_vel2, 0.5f));

        // k3 - Forces and velocity/position updates at second midpoint
        Jaylib.Vector2 k3_force12 = calculateGravitationalForce(midPos1_k2, midPos2_k2, mass1, mass2);
        Jaylib.Vector2 k3_force21 = calculateGravitationalForce(midPos2_k2, midPos1_k2, mass2, mass1);
        Jaylib.Vector2 k3_vel1 = multiply(k3_force12, dt / mass1);
        Jaylib.Vector2 k3_vel2 = multiply(k3_force21, dt / mass2);
        Jaylib.Vector2 k3_pos1 = multiply(midVel1_k2, dt);
        Jaylib.Vector2 k3_pos2 = multiply(midVel2_k2, dt);

        // End velocities and positions for k4
        Jaylib.Vector2 endPos1_k3 = add(pos1, k3_pos1);
        Jaylib.Vector2 endPos2_k3 = add(pos2, k3_pos2);
        Jaylib.Vector2 endVel1_k3 = add(vel1, k3_vel1);
        Jaylib.Vector2 endVel2_k3 = add(vel2, k3_vel2);

        // k4 - Forces and velocity/position updates at the final step
        Jaylib.Vector2 k4_force12 = calculateGravitationalForce(endPos1_k3, endPos2_k3, mass1, mass2);
        Jaylib.Vector2 k4_force21 = calculateGravitationalForce(endPos2_k3, endPos1_k3, mass2, mass1);
        Jaylib.Vector2 k4_vel1 = multiply(k4_force12, dt / mass1);
        Jaylib.Vector2 k4_vel2 = multiply(k4_force21, dt / mass2);
        Jaylib.Vector2 k4_pos1 = multiply(endVel1_k3, dt);
        Jaylib.Vector2 k4_pos2 = multiply(endVel2_k3, dt);

        // Update positions and velocities using weighted averages
        pos1 = add(pos1, multiply(add(k1_pos1, add(multiply(k2_pos1, 2), add(multiply(k3_pos1, 2), k4_pos1))), 1.0f / 6.0f));
        vel1 = add(vel1, multiply(add(k1_vel1, add(multiply(k2_vel1, 2), add(multiply(k3_vel1, 2), k4_vel1))), 1.0f / 6.0f));

        pos2 = add(pos2, multiply(add(k1_pos2, add(multiply(k2_pos2, 2), add(multiply(k3_pos2, 2), k4_pos2))), 1.0f / 6.0f));
        vel2 = add(vel2, multiply(add(k1_vel2, add(multiply(k2_vel2, 2), add(multiply(k3_vel2, 2), k4_vel2))), 1.0f / 6.0f));
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

    public static Jaylib.Vector2 divide(Jaylib.Vector2 v, float scalar) {
        return new Jaylib.Vector2(v.x() / scalar, v.y() / scalar);
    }
}