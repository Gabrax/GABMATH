package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.example.GabKeys.*;

public class Camera {
    private final Vector3f position = new Vector3f(-1.94f, 1.25f, 6.14f); // Start position above ground level
    private Vector3f front = new Vector3f(0, 0, -1); // Direction the camera is facing
    private Vector3f up = new Vector3f(0, 1, 0);    // Up vector
    private Vector3f right = new Vector3f();        // Right vector
    private float movementSpeed = 0.001f;              // Movement speed
    private float yaw = -90.0f;                      // Yaw
    private float pitch = 0.0f;                      // Pitch

    public Camera() {
        updateCameraVectors();
    }

    public void move() {
        // Update camera vectors to reflect current yaw and pitch
        updateCameraVectors();

        // Calculate movement vectors
        Vector3f forwardMovement = new Vector3f(front).mul(movementSpeed);
        Vector3f strafeMovement = new Vector3f(right).mul(movementSpeed);
        Vector3f verticalMovement = new Vector3f(up).mul(movementSpeed);

        // Keyboard movement handling
        if (Input.keyDown(GAB_KEY_W)) {
            position.add(forwardMovement);  // Move forward
        }
        if (Input.keyDown(GAB_KEY_S)) {
            position.sub(forwardMovement);  // Move backward
        }
        if (Input.keyDown(GAB_KEY_A)) {
            position.sub(strafeMovement);   // Move left
        }
        if (Input.keyDown(GAB_KEY_D)) {
            position.add(strafeMovement);   // Move right
        }
        if (Input.keyDown(GAB_KEY_SPACE)) {
            position.add(verticalMovement); // Move up
        }
        if (Input.keyDown(GAB_KEY_LEFT_CONTROL)) {
            position.sub(verticalMovement); // Move down
        }
    }

    private void updateCameraVectors() {
        // Calculate the front vector
        float cosYaw = (float) Math.cos(Math.toRadians(yaw));
        float sinYaw = (float) Math.sin(Math.toRadians(yaw));
        float cosPitch = (float) Math.cos(Math.toRadians(pitch));
        float sinPitch = (float) Math.sin(Math.toRadians(pitch));

        front.x = cosYaw * cosPitch;
        front.y = sinPitch;
        front.z = sinYaw * cosPitch;
        front.normalize();

        // Recalculate right and up vectors
        right.set(front).cross(up).normalize(); // Right vector is perpendicular to the front and up vectors
        up.set(right).cross(front).normalize(); // Up vector recalculated to ensure orthogonality
    }

    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.lookAt(position, new Vector3f(position).add(front), up); // Look from position to position + front direction
        return viewMatrix;
    }

    // Getters and setters for position, pitch, and yaw
    public Vector3f getPosition() {
        return position;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        updateCameraVectors();
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        updateCameraVectors();
    }
}
