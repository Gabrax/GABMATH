package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Math;

public class Camera {

    public enum CameraMovement {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    // Default camera values
    private static final float YAW = -90.0f;
    private static final float PITCH = 0.0f;
    private static final float SPEED = 2.5f;
    private static final float SENSITIVITY = 0.1f;
    private static final float ZOOM = 45.0f;

    // Camera attributes
    private Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;
    private float yaw;
    private float pitch;
    private float movementSpeed;
    private float mouseSensitivity;
    public float zoom;

    // Constructor with vectors
    public Camera(Vector3f position, Vector3f up, float yaw, float pitch) {
        this.position = position;
        this.worldUp = up;
        this.yaw = yaw;
        this.pitch = pitch;
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        this.movementSpeed = SPEED;
        this.mouseSensitivity = SENSITIVITY;
        this.zoom = ZOOM;
        updateCameraVectors();
    }

    // Constructor with scalar values
    public Camera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch) {
        this(new Vector3f(posX, posY, posZ), new Vector3f(upX, upY, upZ), yaw, pitch);
    }

    // Returns the view matrix calculated using Euler Angles and the LookAt Matrix
    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        return viewMatrix.lookAt(position, position.add(front, new Vector3f()), up);
    }

    // Processes input received from any keyboard-like input system
    public void processKeyboard(CameraMovement direction, float deltaTime) {
        float velocity = movementSpeed * deltaTime;
        if (direction == CameraMovement.FORWARD)
            position.add(front.mul(velocity));
        if (direction == CameraMovement.BACKWARD)
            position.sub(front.mul(velocity));
        if (direction == CameraMovement.LEFT)
            position.sub(right.mul(velocity));
        if (direction == CameraMovement.RIGHT)
            position.add(right.mul(velocity));
        if (direction == CameraMovement.UP)
            position.y += 2.5f * velocity;
        if (direction == CameraMovement.DOWN)
            position.y -= 2.5f * velocity;
    }

    // Processes input received from a mouse input system
    public void processMouseMovement(float xoffset, float yoffset, boolean constrainPitch) {
        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;

        yaw += xoffset;
        pitch += yoffset;

        // Make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch) {
            if (pitch > 89.0f)
                pitch = 89.0f;
            if (pitch < -89.0f)
                pitch = -89.0f;
        }

        // Update Front, Right and Up Vectors using the updated Euler angles
        updateCameraVectors();
    }

    // Processes input received from a mouse scroll-wheel event
    public void processMouseScroll(float yoffset) {
        zoom -= yoffset;
        if (zoom < 1.0f)
            zoom = 1.0f;
        if (zoom > 45.0f)
            zoom = 45.0f;
    }

    // Calculates the front vector from the Camera's (updated) Euler Angles
    private void updateCameraVectors() {
        // Calculate the new Front vector
        Vector3f front = new Vector3f();
        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        this.front = front.normalize();

        // Re-calculate the Right and Up vector
        right = front.cross(worldUp).normalize();
        up = right.cross(front).normalize();
    }
}
